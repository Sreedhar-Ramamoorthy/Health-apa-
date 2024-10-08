package ke.co.apollo.health.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class HospitalUpdateRequest {
    private String name;
    private String address;
    private String contact;
    private String email;
    private Integer locationId;
    private Integer paymentId;
    private String workingHours;
}
