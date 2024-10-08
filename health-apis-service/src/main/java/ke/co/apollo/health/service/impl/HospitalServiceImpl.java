package ke.co.apollo.health.service.impl;

import com.github.wenhao.jpa.Specifications;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import ke.co.apollo.health.domain.request.HospitalUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ke.co.apollo.health.domain.entity.HospitalEntity;
import ke.co.apollo.health.domain.entity.LocationEntity;
import ke.co.apollo.health.domain.entity.PaymentEntity;
import ke.co.apollo.health.domain.entity.ServiceEntity;
import ke.co.apollo.health.domain.request.HospitalSearchRequest;
import ke.co.apollo.health.domain.response.HospitalInitialResponse;
import ke.co.apollo.health.repository.HospitalRepository;
import ke.co.apollo.health.repository.LocationRepository;
import ke.co.apollo.health.repository.PaymentRepository;
import ke.co.apollo.health.repository.ServiceRepository;
import ke.co.apollo.health.service.HospitalService;
import ke.co.apollo.health.utils.ExcelUtil;

@Service
@Slf4j
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    HospitalRepository hospitalRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    LocationRepository locationRepository;

    @Override
    public List<PaymentEntity> getPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public List<ServiceEntity> getServices() {
        return serviceRepository.findAll();
    }

    @Override
    public List<LocationEntity> getLocations() {
        List<LocationEntity> locationEntities = locationRepository.findAll();

        List<LocationEntity> nairobi = locationEntities.stream()
                                                       .filter(o -> StringUtils.startsWith(o.getName(), "NAIROBI"))
                                                       .collect(Collectors.toList());

        List<LocationEntity> others = locationEntities.stream()
                                                      .filter(o -> !StringUtils.startsWith(o.getName(), "NAIROBI"))
                                                      .sorted(Comparator.comparing(LocationEntity::getName))
                                                      .collect(Collectors.toList());
        nairobi.addAll(others);

        return nairobi;
    }

    @Override
    public List<HospitalEntity> getHospitals() {
        return hospitalRepository.findAll();
    }

    @Override
    public Page<HospitalEntity> searchHospitals(HospitalSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getIndex() - 1, request.getLimit());
        Integer location = request.getLocationId();
        List<Integer> paymentIds = request.getCoPaymentId();
        List<Integer> serviceIds = request.getServiceId();
        log.warn( "list list of payments {}" , paymentIds);
        log.warn("list list of services {}" , serviceIds);
        boolean isPaymentIds = CollectionUtils.isEmpty(paymentIds);
        boolean isServiceIds = CollectionUtils.isEmpty(serviceIds);

        if(isPaymentIds && isServiceIds){
            Specification<HospitalEntity> spec = Specifications.<HospitalEntity>and()
                    .in(false, "paymentId", paymentIds)
                    .eq(Objects.nonNull(location), "locationId", location)
                    .in(false, "services.id", serviceIds)
                    .build();
            return hospitalRepository.findAll(spec,pageable);
        }
        return hospitalRepository.findAllByPaymentIdAndServiceIdNative(pageable,location,paymentIds,serviceIds);
    }

    public boolean updateHospitalLocation(MultipartFile multipartFile) {
        locationRepository.deleteAll();
        return true;
    }


    public void updateHospital(MultipartFile multipartFile) throws IOException {
        log.debug("MultipartFile getName: {}", multipartFile.getName());
        log.debug("MultipartFile getOriginalFilename: {}", multipartFile.getOriginalFilename());
        log.debug("MultipartFile getContentType: {}", multipartFile.getContentType());

        hospitalRepository.deleteAll();
        Workbook workbook = null;
        InputStream inputStream = multipartFile.getInputStream();
        try {
            workbook = WorkbookFactory.create(inputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            inputStream.close();
        }
        if (workbook != null) {
            saveHospitalEntityList(workbook);
        }
    }

    private void saveHospitalEntityList(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(6);

        List<HospitalEntity> hospitalEntityList = new ArrayList<>();

        List<ServiceEntity> serviceEntityList = serviceRepository.findAll();
        Map<Integer, ServiceEntity> serviceEntityMap = serviceEntityList.stream()
                                                                        .collect(Collectors.toMap(ServiceEntity::getId, a -> a));

        List<LocationEntity> locationEntities = locationRepository.findAll();
        Map<String, Integer> locationMapId = new HashMap<>();
        locationEntities.forEach(o -> locationMapId.put(o.getName(), o.getId()));

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row rowH = sheet.getRow(i);

            String name = ExcelUtil.getCellValue(rowH.getCell(0));
            String address = ExcelUtil.getCellValue(rowH.getCell(1));
            String phone = ExcelUtil.getCellValue(rowH.getCell(3));
            String email = ExcelUtil.getCellValue(rowH.getCell(4));
            String location = ExcelUtil.getCellValue(rowH.getCell(5));
            String pay = ExcelUtil.getCellValue(rowH.getCell(6));
            Integer paymentId = null;
            if (StringUtils.equalsIgnoreCase(pay, "Yes")) {
                paymentId = 1;
            }
            if (StringUtils.equalsIgnoreCase(pay, "No")) {
                paymentId = 2;
            }

            Set<ServiceEntity> serviceEntitySet = getService(rowH, serviceEntityMap);
            HospitalEntity hospitalEntity = HospitalEntity.builder()
                                                          .name(name)
                                                          .address(address)
                                                          .locationId(locationMapId.get(location))
                                                          .contact(phone)
                                                          .email(email)
                                                          .paymentId(paymentId)
                                                          .services(serviceEntitySet)
                                                          .build();
            if (paymentId == null) {
                log.error(hospitalEntity.getAddress());
                log.error(hospitalEntity.getContact());
            }
            hospitalEntityList.add(hospitalEntity);
        }
        log.info("hospitalEntityList size: {}", hospitalEntityList.size());

        hospitalRepository.saveAll(hospitalEntityList);
    }

    private Set<ServiceEntity> getService(Row row, Map<Integer, ServiceEntity> serviceEntityMap) {
        Set<ServiceEntity> serviceEntitySet = new HashSet<>();
        for (int i = 8; i < 45; i++) {
            String name = ExcelUtil.getCellValue(row.getCell(i));
            Integer serviceId = StringUtils.isNotEmpty(name) ? Integer.valueOf(name) : null;

            serviceEntitySet.add(serviceEntityMap.get(serviceId));
        }
        return serviceEntitySet;
    }

    @Override
    public HospitalInitialResponse getInitialData() {
        return HospitalInitialResponse.builder()
                                      .locations(this.getLocations())
                                      .coPayments(this.getPayments())
                                      .services(this.getServices())
                                      .build();
    }

    @Override
    public void updateHospitalList(MultipartFile multipartFile) throws IOException {
        log.debug("UpdateHospitalList MultipartFile getName: {}", multipartFile.getName());
        log.debug("UpdateHospitalList MultipartFile getOriginalFilename: {}", multipartFile.getOriginalFilename());
        log.debug("UpdateHospitalList MultipartFile getContentType: {}", multipartFile.getContentType());
        Workbook validWorkbook = null;
        InputStream validInputStream = multipartFile.getInputStream();
        try {
            validWorkbook = WorkbookFactory.create(validInputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (validWorkbook != null) {
                validWorkbook.close();
            }
            validInputStream.close();
        }
        if (validWorkbook != null) {
            updateHospitalEntityList(validWorkbook);
        }
    }


    public void updateHospitalEntityList(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(2);
        List<HospitalEntity> hospitalList = new ArrayList<>();
        List<ServiceEntity> serviceList = serviceRepository.findAll();
        Map<Integer, ServiceEntity> serviceMap = serviceList.stream()
                .collect(Collectors.toMap(ServiceEntity::getId, a -> a));
        List<LocationEntity> locations = locationRepository.findAll();
        Map<String, Integer> locationMap = new HashMap<>();
        locations.forEach(o -> locationMap.put(o.getName(), o.getId()));
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            String validName = ExcelUtil.getCellValue(row.getCell(0));
            String validAddress = ExcelUtil.getCellValue(row.getCell(1));
            String validPhone = ExcelUtil.getCellValue(row.getCell(3));
            String validEmail = ExcelUtil.getCellValue(row.getCell(4));
            String validLocation = ExcelUtil.getCellValue(row.getCell(5));
            String validPay = ExcelUtil.getCellValue(row.getCell(6));
            String workingHours = ExcelUtil.getCellValue(row.getCell(7));
            Integer payId = null;
            if (StringUtils.equalsIgnoreCase(validPay, "Yes")) {
                payId = 1;
            }
            if (StringUtils.equalsIgnoreCase(validPay, "No")) {
                payId = 2;
            }
            List<HospitalEntity> nameEntity = hospitalRepository.findAllByNameAndAddress(validName, validAddress);
            if(!nameEntity.isEmpty()) {
                Integer paymentId = payId;
                nameEntity.stream().forEach(hospitalEntity-> {
                    HospitalUpdateRequest hospitalUpdateRequest = HospitalUpdateRequest.builder()
                            .name(validName)
                            .address(validAddress)
                            .locationId(locationMap.get(validLocation))
                            .contact(validPhone)
                            .email(validEmail)
                            .paymentId(paymentId)
                            .workingHours(workingHours)
                            .build();
                    hospitalRepository.updateHospital(hospitalUpdateRequest);
                });
            }
            else {
                Set<ServiceEntity> serviceEntitySet = getService(row, serviceMap);
                HospitalEntity hospitalEntity = HospitalEntity.builder()
                        .name(validName)
                        .address(validAddress)
                        .locationId(StringUtils.isNotBlank(validLocation) ? locationMap.get(validLocation): null)
                        .contact(validPhone)
                        .email(validEmail)
                        .paymentId(payId)
                        .services(serviceEntitySet)
                        .workingHours(workingHours)
                        .build();
                hospitalList.add(hospitalEntity);
            }
        }
        hospitalRepository.saveAll(hospitalList);
    }


}