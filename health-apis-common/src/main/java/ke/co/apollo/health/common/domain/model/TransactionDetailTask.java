package ke.co.apollo.health.common.domain.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailTask implements java.io.Serializable {

    private static final long serialVersionUID = -4542303857545367865L;

    private String taskId;

    private Date scheduleTime;

    private String type;

    private String amount;

    private String checkoutRequestId;

    private String paymentStatus;

    private Long responseCode;

    private String responseDesc;

    private Long resultCode;

    private String resultDesc;

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private String updateBy;

}