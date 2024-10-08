package ke.co.apollo.health.domain.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "health_agent_branch")
public class AgentBranchEntity {
    @Id
    @Column(name = "entity_id", length = 50)
    private Integer entityId;
    @Column(name = "agent_id", length = 50)
    private String agentId;
    @Column(name = "branch_name", length = 50)
    private String branchName;

}
