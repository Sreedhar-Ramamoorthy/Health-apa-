package ke.co.apollo.health.repository;

import ke.co.apollo.health.domain.entity.AgentBranchEntity;
import ke.co.apollo.health.domain.entity.HealthStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthAgentBranchRepository extends JpaRepository<AgentBranchEntity, Integer> {
    AgentBranchEntity findAgentBranchEntitiesByEntityId(Integer entityId);
}
