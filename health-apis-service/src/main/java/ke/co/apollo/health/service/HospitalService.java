package ke.co.apollo.health.service;

import java.io.IOException;
import java.util.List;
import ke.co.apollo.health.domain.entity.HospitalEntity;
import ke.co.apollo.health.domain.entity.LocationEntity;
import ke.co.apollo.health.domain.entity.PaymentEntity;
import ke.co.apollo.health.domain.entity.ServiceEntity;
import ke.co.apollo.health.domain.request.HospitalSearchRequest;
import ke.co.apollo.health.domain.response.HospitalInitialResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface HospitalService {

  List<PaymentEntity> getPayments();

  List<ServiceEntity> getServices();

  List<LocationEntity> getLocations();

  List<HospitalEntity> getHospitals();

  Page<HospitalEntity> searchHospitals(HospitalSearchRequest request);

  HospitalInitialResponse getInitialData();
  boolean updateHospitalLocation(MultipartFile multipartFile) throws IOException;
  void updateHospital(MultipartFile multipartFile) throws IOException;
  void updateHospitalList(MultipartFile multipartFile) throws IOException;

}
