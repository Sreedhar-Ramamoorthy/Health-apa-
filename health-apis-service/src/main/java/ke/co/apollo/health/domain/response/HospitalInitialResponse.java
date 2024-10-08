package ke.co.apollo.health.domain.response;


import java.util.List;
import ke.co.apollo.health.domain.entity.LocationEntity;
import ke.co.apollo.health.domain.entity.PaymentEntity;
import ke.co.apollo.health.domain.entity.ServiceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HospitalInitialResponse {

  private List<LocationEntity> locations;

  private List<PaymentEntity> coPayments;

  private List<ServiceEntity> services;

}
