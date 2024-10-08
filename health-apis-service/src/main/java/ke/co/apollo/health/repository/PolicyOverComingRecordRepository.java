package ke.co.apollo.health.repository;

import ke.co.apollo.health.domain.entity.PolicyOverComingRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PolicyOverComingRecordRepository extends JpaRepository<PolicyOverComingRecordEntity, String>,
        JpaSpecificationExecutor<PolicyOverComingRecordEntity> {
    PolicyOverComingRecordEntity findByRecordDate(String recordDate);
}
