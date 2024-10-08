package ke.co.apollo.health.repository;

import ke.co.apollo.health.domain.entity.PolicyOverComingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional(value = "transactionManager")
public interface PolicyOverComingRepository extends JpaRepository<PolicyOverComingEntity, String>, JpaSpecificationExecutor<PolicyOverComingEntity> {
    List<PolicyOverComingEntity> findAllByNeedToUpdateIsAndRenewalDateBetween(boolean neeUpdate, Date past, Date future);

    List<PolicyOverComingEntity> findAllByRenewalDateBetween(Date past, Date future);

    @Query(nativeQuery = true, value = "SELECT * FROM policy_over_coming WHERE renewalDate = DATE_ADD(CURRENT_DATE, INTERVAL :numberOfDays DAY)")
    List<PolicyOverComingEntity> findAllPoliciesDueForRenewalIn(Integer numberOfDays);

    Page<PolicyOverComingEntity> findAllByRenewalDateBetween(Date past, Date future, Pageable pageable);
    List<PolicyOverComingEntity> findAllByPolicyNumberIn(List<String> policyNumbers);
}
