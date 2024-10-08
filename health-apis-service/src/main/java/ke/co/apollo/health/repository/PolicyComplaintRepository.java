package ke.co.apollo.health.repository;

import ke.co.apollo.health.domain.entity.PolicyComplaintEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(value = "transactionManager")
public interface PolicyComplaintRepository extends JpaRepository<PolicyComplaintEntity, String>,
    JpaSpecificationExecutor<PolicyComplaintEntity> {

}
