package ke.co.apollo.health.service;


import java.util.List;
import ke.co.apollo.health.common.domain.model.Customer;

public interface BeneficiaryService {

  boolean addBeneficiaryToBase(String customerId, String quoteId, Integer policyId,
      String policyEffectiveDate);

  List<Customer> getQuoteBeneficiary(String customerId, String quoteId);
}
