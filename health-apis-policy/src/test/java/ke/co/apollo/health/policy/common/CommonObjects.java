package ke.co.apollo.health.policy.common;

import ke.co.apollo.health.common.domain.model.PolicyOverComing;
import ke.co.apollo.health.common.domain.model.request.ComingPolicyListRequest;

import java.math.BigDecimal;
import java.util.Date;

public class CommonObjects {

    public static PolicyOverComing getPolicyOverComing = PolicyOverComing.builder()
            .policyNumber("PN001")
            .principalName("TEST")
            .renewalDate(new Date(2024, 8, 9))
            .effectiveDate(new Date(2023, 8, 18))
            .plan("plan")
            .agentName("Agt Smith")
            .asagentId("007")
            .policyAmount("69000")
            .claims(BigDecimal.valueOf(2l))
            .email("agentSmith@007.com")
            .mobile("0700700769")
            .CaptureDate(new Date())
            .build();

    public static ComingPolicyListRequest getComingPolicyListRequest = ComingPolicyListRequest.builder()
            .index(0)
            .limit(20)
            .build();
}
