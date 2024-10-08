package ke.co.apollo.health.repository;

import ke.co.apollo.health.domain.entity.HospitalEntity;
import ke.co.apollo.health.domain.request.HospitalUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.List;

public interface HospitalRepository extends JpaRepository<HospitalEntity, String>,
    JpaSpecificationExecutor<HospitalEntity> {

  HospitalEntity findByName(String name);

  List<HospitalEntity> findAllByNameAndAddress(String name, String address);

  @Query(value = "SELECT * FROM tbl_hospitals h \n" +
          "INNER JOIN tbl_locations l ON h.locations_id=l.id \n" +
          "JOIN tbl_hospital_service_rel r ON h.id = r.hospital_id \n" +
          "WHERE l.id=?1 AND ( h.payments_id IN (?2) OR r.service_id IN (?3)) GROUP BY h.name", nativeQuery = true)
  Page<HospitalEntity> findAllByPaymentIdAndServiceIdNative(Pageable page, int locationId, List<Integer> paymentIds,List<Integer> serviceId);

  @Transactional
  @Modifying
  @Query("update HospitalEntity u set u.address = :#{#hospitalUpdateRequest.address}, " +
          "u.contact = :#{#hospitalUpdateRequest.contact}, " +
          "u.email = :#{#hospitalUpdateRequest.email}, " +
          "u.locationId = :#{#hospitalUpdateRequest.locationId}, " +
          "u.paymentId = :#{#hospitalUpdateRequest.paymentId}, " +
          "u.workingHours = :#{#hospitalUpdateRequest.workingHours} " +
          "where u.name = :#{#hospitalUpdateRequest.name}")
  void updateHospital(@Param("hospitalUpdateRequest") HospitalUpdateRequest hospitalUpdateRequest);

}
