package ke.co.apollo.health.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.common.domain.model.UnderwritingType;
import ke.co.apollo.health.common.domain.model.request.AddBeneficiariesToPolicyRequest;
import ke.co.apollo.health.common.domain.model.response.AddBeneficiariesToPolicyResponse;
import ke.co.apollo.health.common.enums.DependantRelationship;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.common.utils.HealthDateUtils;
import ke.co.apollo.health.domain.request.CustomerSearchRequest;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.service.BeneficiaryService;
import ke.co.apollo.health.service.CustomerService;
import ke.co.apollo.health.service.QuoteService;

@Service
public class BeneficiaryServiceImpl implements BeneficiaryService {

  @Autowired
  CustomerService customerService;

  @Autowired
  PolicyRemote policyRemote;

  @Autowired
  QuoteService quoteService;

  @Override
  public boolean addBeneficiaryToBase(String customerId, String quoteId, Integer policyId,
      String policyEffectiveDate) {
    boolean result = true;
    List<Customer> customers = getQuoteBeneficiary(customerId, quoteId);
    List<AddBeneficiariesToPolicyRequest> request = new ArrayList<>();
    for (Customer client : customers) {
      if (client.getEntityId() != null) {
        AddBeneficiariesToPolicyRequest beneficiary = AddBeneficiariesToPolicyRequest.builder()
            .entityId(client.getEntityId().intValue()).joinDate(new Date())
            .originalJoinDate(new Date()).policyEffectiveDate(policyEffectiveDate)
            .policyId(policyId)
            .underwritingType(new UnderwritingType(GlobalConstant.UNDERWRITING_TYPE,
                GlobalConstant.UNDERWRITING_TYPE)).build();
        request.add(beneficiary);
      }
    }
    List<AddBeneficiariesToPolicyResponse> list = policyRemote.addBeneficiariesToPolicy(request);
    if (CollectionUtils.isNotEmpty(list)) {
      for (AddBeneficiariesToPolicyResponse response : list) {
        result = result && response.getSuccess();
      }
    } else {
      result = false;
    }

    return result;
  }

  @Override
  public List<Customer> getQuoteBeneficiary(String customerId, String quoteId) {
    CustomerDetailResponse customerDetail = customerService.getCustomer(
        CustomerSearchRequest.builder().customerId(customerId).quoteId(quoteId).build());
    if (customerDetail == null) {
      throw new BusinessException("Can not find related Customer");
    }
    List<Customer> customers = customerService.getCustomerAndDependants(customerId, quoteId);
    if (CollectionUtils.isEmpty(customers)) {
      throw new BusinessException("Can not find related Customer and dependant");
    }
    Quote quote = quoteService.getQuote(quoteId, customerId, null);
    if (quote != null) {
      Iterator<Customer> iterator = customers.listIterator();
      Customer customer;
      int childCount = 0;
      int spouseCount = 0;
      while (iterator.hasNext()) {
        customer = iterator.next();
        switch (DependantRelationship.getRelationship(customer.getRelationshipDesc())) {
          case SPOUSE:
            removeUselessDependants(customerDetail, iterator);
            spouseCount++;
            break;
          case MARRIED_CHILD:
          case UNMARRIED_CHILD:
            childCount++;
            break;
          default:
            removeUselessDependants(customerDetail, iterator);
            break;
        }
      }
      initDependantForPremiumCalculate(customerDetail, customers, childCount, spouseCount);
    } else {
      throw new BusinessException("Can not find related quote");
    }
    return customers;
  }

  /**
   * Add init dependants for premium calculate feature. case 1: Principal user has spouse and spouse
   * DOB is not null case 2: Principal user child count is not 0 and user haven't update customer
   * detail to fulfill children detail yet.
   */
  public void initDependantForPremiumCalculate(CustomerDetailResponse customerDetail,
      List<Customer> customers, int childCount, int spouseCount) {
    if (customerDetail.getSpouse() != null && customerDetail.getSpouse().getDateOfBirth() != null
        && spouseCount == 0 && !customerDetail.isOnlyChild()) {
      customers.add(Customer.builder().relationshipDesc(DependantRelationship.SPOUSE.getValue())
          .dateOfBirth(customerDetail.getSpouse().getDateOfBirth()).build());
    }
    if (customerDetail.getChildren() != null && customerDetail.getChildren().getCount() != null) {
      int gap = customerDetail.getChildren().getCount() - childCount;
      if (gap > 0) {
        for (int i = 0; i < gap; i++) {
          customers.add(Customer.builder().relationshipDesc(DependantRelationship.UNMARRIED_CHILD
              .getValue()).dateOfBirth(DateUtils.addYears(HealthDateUtils.currentDate(), -1))
              .build());
        }
      }
    }
  }

  public void removeUselessDependants(CustomerDetailResponse customerDetail,
      Iterator<Customer> iterator) {
    if (customerDetail.isOnlyChild()) {
      iterator.remove();
    }
  }

}
