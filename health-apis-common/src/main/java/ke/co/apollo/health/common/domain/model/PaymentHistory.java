package ke.co.apollo.health.common.domain.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistory {
    private String id;

    private boolean renewal;

    private String customerId;

    private String paymentPhone;

    private String amount;

    private String paymentType;

    private String quoteNumber;

    private String policyNumber;

    private Date effectiveDate;

    private String premium;

    private String merchantRequestId;

    private String checkoutRequestId;

    private String responseCode;

    private String responseDesc;

    private String customerMsg;

    private Date createTime;

    private Date updateTime;

    private String balance;
}
