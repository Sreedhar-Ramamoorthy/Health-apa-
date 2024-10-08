package ke.co.apollo.health.common.domain.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InAppNotificationMessageRequest {

    private Integer pageNumber;
    private Integer pageSize;
    @Length(min = 1, max = 100)
    private String phoneNumber;

}
