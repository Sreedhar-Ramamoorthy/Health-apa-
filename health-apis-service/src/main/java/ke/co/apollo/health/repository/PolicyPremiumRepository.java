package ke.co.apollo.health.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import ke.co.apollo.health.domain.entity.PolicyPremiumEntity;
@Transactional(value = "transactionManager")
public interface PolicyPremiumRepository extends JpaRepository<PolicyPremiumEntity, String>,
    JpaSpecificationExecutor<PolicyPremiumEntity> {


  void deleteByQuoteId(String quoteId);


  void deleteByPolicyIdAndEffectiveDate(Integer policyId, Date effectiveDate);

  Optional<List<PolicyPremiumEntity>> findAllByQuoteId(String quoteId);

  Optional<List<PolicyPremiumEntity>> findAllByPolicyIdAndEffectiveDate(Integer policyId,
      Date effectiveDate);
}
