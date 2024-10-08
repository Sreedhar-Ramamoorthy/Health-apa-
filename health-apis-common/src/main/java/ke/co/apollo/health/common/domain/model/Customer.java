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
public class Customer {
    private String customerId;

    private String parentId;

    private String agentId;

    private Long entityId;

    private String quoteId;

    private String superCustomerId;

    private String firstName;

    private String lastName;

    private Date dateOfBirth;

    private String title;

    private String gender;

    private String phoneNumber;

    private String email;

    private Date startDate;

    private String relationshipDesc;

    private Dependant spouseSummary;

    private Children childrenSummary;

    private DependantBenefit benefit;

    private String idNo;

    private String kraPin;

    private Date createTime;

    private Date updateTime;

}
