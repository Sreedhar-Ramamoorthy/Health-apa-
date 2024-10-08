package ke.co.apollo.health.mapping;


import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.request.CustomerCreateRequest;
import ke.co.apollo.health.common.enums.DependantRelationship;
import ke.co.apollo.health.domain.request.CustomerAddRequest;

public class CustomerMapping {

  private CustomerMapping() {

  }

  public static Customer customerAddRequest2Entity(CustomerAddRequest request) {
    Customer customer = null;
    if (request != null) {
      customer = Customer.builder().agentId(request.getAgentId()).firstName(request.getFirstName())
          .lastName(request.getLastName()).dateOfBirth(request.getDateOfBirth())
          .title(request.getTitle()).gender(request.getGender())
          .phoneNumber(request.getPhoneNumber()).email(request.getEmail())
          .superCustomerId(request.getSuperCustomerId())
          .entityId(request.getEntityId()).relationshipDesc(DependantRelationship.POLICY_HOLDER
              .getValue()).build();
    }
    return customer;
  }

  public static Customer customerCreateRequest2Entity(CustomerCreateRequest request) {
    Customer customer = null;
    if (request != null) {
      customer = Customer.builder()
                         .agentId(request.getAgentId())
                         .customerId(request.getCustomerId())
                         .quoteId(request.getQuoteId())
                         .firstName(request.getFirstName())
                         .lastName(request.getLastName())
                         .dateOfBirth(request.getDateOfBirth())
                         .title(request.getTitle())
                         .gender(request.getGender())
                         .spouseSummary(request.getSpouse())
                         .childrenSummary(request.getChildren())
                         .benefit(request.getBenefit())
                         .relationshipDesc(DependantRelationship.POLICY_HOLDER.getValue())
                         .build();
    }
    return customer;
  }

}
