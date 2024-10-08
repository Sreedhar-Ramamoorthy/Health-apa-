package ke.co.apollo.health.common.domain.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComingPolicyListRequestInExcel {
    private Date startDate;
    private Date endDate;
}
