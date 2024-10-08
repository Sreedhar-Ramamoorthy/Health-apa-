package ke.co.apollo.health.domain.entity;


import ke.co.apollo.health.enums.HealthQuoteStepsEnum;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "health_step")
public class HealthStepEntity {
    @Id
    @Column(name = "quote_or_policy_id", length = 50)
    private String quoteOrPolicyId;

    @Column(name = "agent_id", length = 50)
    private String agentId;

    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "step")
    private HealthQuoteStepsEnum step;
}
