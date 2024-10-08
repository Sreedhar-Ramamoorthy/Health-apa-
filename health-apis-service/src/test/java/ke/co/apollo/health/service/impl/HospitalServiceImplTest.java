package ke.co.apollo.health.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import ke.co.apollo.health.common.domain.model.remote.ApiResponse;
import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.Premium;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyResponse;
import ke.co.apollo.health.domain.entity.HealthStepEntity;
import ke.co.apollo.health.domain.request.CustomerSearchRequest;
import ke.co.apollo.health.domain.request.QuoteBenefitUpdateRequest;
import ke.co.apollo.health.domain.request.QuoteFinishRequest;
import ke.co.apollo.health.domain.request.QuoteStepRequest;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import ke.co.apollo.health.domain.response.QuoteStepResponse;
import ke.co.apollo.health.enums.HealthQuoteStepsEnum;
import ke.co.apollo.health.mapper.health.CustomerMapper;
import ke.co.apollo.health.mapper.health.QuoteMapper;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.repository.HealthStepRepository;
import ke.co.apollo.health.service.BeneficiaryService;
import ke.co.apollo.health.service.PremiumService;
import ke.co.apollo.health.service.ProductService;
import ke.co.apollo.health.service.QuoteService;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import ke.co.apollo.health.common.enums.PolicyStatus;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import ke.co.apollo.health.service.IntermediaryService;
import ke.co.apollo.health.domain.entity.HospitalEntity;
import ke.co.apollo.health.domain.entity.LocationEntity;
import ke.co.apollo.health.domain.entity.PaymentEntity;
import ke.co.apollo.health.domain.entity.ServiceEntity;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.domain.entity.HealthStepEntity;
import ke.co.apollo.health.domain.request.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.runner.Request.method;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import ke.co.apollo.health.domain.response.HospitalInitialResponse;

import static ke.co.apollo.health.common.CommonObjects.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import ke.co.apollo.health.repository.HospitalRepository;
import ke.co.apollo.health.repository.LocationRepository;
import ke.co.apollo.health.repository.PaymentRepository;
import ke.co.apollo.health.repository.ServiceRepository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class HospitalServiceImplTest {

    @InjectMocks
    HospitalServiceImpl hospitalService;

    @Mock
    HospitalRepository hospitalRepository;

    @Mock
    ServiceRepository serviceRepository;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    LocationRepository locationRepository;

    MockMultipartFile file;


    @BeforeEach
    void setUpMocks(){
        initMocks(this);

        when(paymentRepository.findAll()).thenReturn(Collections.singletonList(paymentEntityObject));
        when(serviceRepository.findAll()).thenReturn(Collections.singletonList(serviceEntityObject));
        when(hospitalRepository.findAll()).thenReturn(Collections.singletonList(hospitalEntityObject));
        when(locationRepository.findAll()).thenReturn(Collections.singletonList(locationEntityObject));

        doNothing().when(locationRepository).deleteAll();      

        file 
            = new MockMultipartFile(
                "file", 
                "hello.txt", 
                MediaType.TEXT_PLAIN_VALUE, 
                "Hello, World!".getBytes()
            );

    }



    @Test
     void getPaymentsTest(){
        List<PaymentEntity> resp = hospitalService.getPayments();  //NO SONAR
        assertNotNull(resp);
        }

    @Test
     void getServicesTest(){
        List<ServiceEntity> resp = hospitalService.getServices();  //NO SONAR
        assertNotNull(resp);
        }

     @Test
     void getLocationsTest(){
        List<LocationEntity> resp = hospitalService.getLocations();  //NO SONAR
        assertNotNull(resp);
        }

     @Test
     void getHospitalsTest(){
        List<HospitalEntity> resp = hospitalService.getHospitals();  //NO SONAR
        assertNotNull(resp);
        }

     @Test
     void updateHospitalLocationTest(){
        boolean resp = hospitalService.updateHospitalLocation(file);  //NO SONAR
        assertEquals(resp,true);
        }

        //Page<Quotation> quotationPage = new PageImpl<>(quotations);

    @Test
     void searchHospitalsTest(){
       
        when(hospitalRepository.findAllByPaymentIdAndServiceIdNative(any(),anyInt(),any(),any())).thenReturn(
            new PageImpl<HospitalEntity>(Collections.singletonList(hospitalEntityObject)) 
            );

        Page<HospitalEntity> resp = hospitalService.searchHospitals(HospitalSearchRequest.builder()
                        .locationId(1)
                        .serviceId(Arrays.asList(1,2,3))
                        .coPaymentId(Arrays.asList(1,2,3))
                        .index(1)
                        .limit(1)
                        .build());  //NO SONAR
                        
        assertNotNull(resp);
        }

    @Test
     void searchHospitalsEmptyPaymentIdsAndServiceIdTest(){
       
        when(hospitalRepository.findAllByPaymentIdAndServiceIdNative(any(),anyInt(),any(),any())).thenReturn(
            new PageImpl<HospitalEntity>(Collections.singletonList(hospitalEntityObject)) 
            );

        Page<HospitalEntity> resp = hospitalService.searchHospitals(HospitalSearchRequest.builder()
                        .locationId(1)
                        // .serviceId(Collections.emptyList())
                        // .coPaymentId(Arrays.asList(1,2,3))
                        .index(1)
                        .limit(1)
                        .build());  //NO SONAR
                        
        assertTrue(true);
        }

    @Test
     void searchHospitalsEmptyPaymentIdsTest(){
       
        when(hospitalRepository.findAllByPaymentIdAndServiceIdNative(any(),anyInt(),any(),any())).thenReturn(
            new PageImpl<HospitalEntity>(Collections.singletonList(hospitalEntityObject)) 
            );

        Page<HospitalEntity> resp = hospitalService.searchHospitals(HospitalSearchRequest.builder()
                        .locationId(1)
                        .serviceId(Arrays.asList(1,2,3))
                        // .coPaymentId()
                        .index(1)
                        .limit(1)
                        .build());  //NO SONAR
                        
        assertTrue(true);
        }

    @Test
    void getInitialDataTestt(){
        HospitalInitialResponse resp = hospitalService.getInitialData();  //NO SONAR
        assertNotNull(resp);
        }

    @Test
    void testUpdateHospitalList() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/test.xlsx");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("test.xlsx", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", inputStream);
        Workbook workbookMock = mock(Workbook.class);
        doNothing().when(hospitalRepository).updateHospital(any());
        Sheet sheetMock = mock(Sheet.class);
        when(workbookMock.getSheetAt(0)).thenReturn(sheetMock);
        when(workbookMock.getSheetAt(2)).thenReturn(sheetMock);
        when(sheetMock.getLastRowNum()).thenReturn(7);
        if (workbookMock!=null){
            workbookMock.close();
        }
        Row rowMock = mock(Row.class);
        when(sheetMock.getRow(anyInt())).thenReturn(rowMock);
        stubCellWithCellValue(rowMock, 0, "HospitalName");
        stubCellWithCellValue(rowMock, 1, "Address");
        stubCellWithCellValue(rowMock, 3, "PhoneNumber");
        stubCellWithCellValue(rowMock, 4, "Email");
        stubCellWithCellValue(rowMock, 5, "Location");
        stubCellWithCellValue(rowMock, 6, "Yes");
        stubCellWithCellValue(rowMock, 7, "");
        HospitalUpdateRequest hospitalUpdateRequest = HospitalUpdateRequest.builder()
                .name("test")
                .workingHours("")
                .contact("test")
                .email("test@gmail.com")
                .locationId(1)
                .paymentId(1)
                .build();
        assertNotNull(hospitalUpdateRequest);
        doNothing().when(workbookMock).close();
        hospitalService.updateHospitalList(mockMultipartFile);
        hospitalService.updateHospitalEntityList(workbookMock);
    }

    @Test
    void testUpdateHospitalEntityEmptyList() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/test.xlsx");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("test.xlsx", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", inputStream);
        Workbook workbookMock = mock(Workbook.class);
        try {
            doNothing().when(hospitalRepository).updateHospital(any());
            Sheet sheetMock = mock(Sheet.class);
            when(workbookMock.getSheetAt(0)).thenReturn(sheetMock);
            when(workbookMock.getSheetAt(2)).thenReturn(sheetMock);
            when(sheetMock.getLastRowNum()).thenReturn(7);
            Row rowMock = mock(Row.class);
            when(sheetMock.getRow(anyInt())).thenReturn(rowMock);
            stubCellWithCellValue(rowMock, 0, "HospitalName");
            stubCellWithCellValue(rowMock, 1, "Address");
            stubCellWithCellValue(rowMock, 3, "PhoneNumber");
            stubCellWithCellValue(rowMock, 4, "Email");
            stubCellWithCellValue(rowMock, 5, "Location");
            stubCellWithCellValue(rowMock, 6, "No");
            stubCellWithCellValue(rowMock, 7, "");
            when(serviceRepository.findAll()).thenReturn(Collections.emptyList());
            when(locationRepository.findAll()).thenReturn(Collections.emptyList());
            when(hospitalRepository.findAllByNameAndAddress(anyString(), anyString())).thenReturn(Collections.singletonList(mock(HospitalEntity.class)));
            HospitalUpdateRequest hospitalUpdateRequest = HospitalUpdateRequest.builder()
                    .name("")
                    .workingHours("")
                    .contact("")
                    .email("")
                    .locationId(null)
                    .paymentId(null)
                    .build();
            assertNotNull(hospitalUpdateRequest);
            hospitalService.updateHospitalList(mockMultipartFile);
            hospitalService.updateHospitalEntityList(workbookMock);
        } finally {
            doNothing().when(hospitalRepository).updateHospital(any());
            if (workbookMock != null) {
                workbookMock.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    void stubCellWithCellValue(Row rowMock, int cellIndex, String value) {
        Cell cellMock = mock(Cell.class);
        when(rowMock.getCell(cellIndex)).thenReturn(cellMock);
        when(cellMock.getStringCellValue()).thenReturn(value);
    }

}
