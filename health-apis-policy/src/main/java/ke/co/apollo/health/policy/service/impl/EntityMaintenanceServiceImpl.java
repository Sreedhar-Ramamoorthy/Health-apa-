package ke.co.apollo.health.policy.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.DependantDetail;
import ke.co.apollo.health.common.domain.model.EntityDetails;
import ke.co.apollo.health.common.domain.model.EntityDetails.Roles;
import ke.co.apollo.health.common.domain.model.EntityDetails.Roles.RoleAdditionalInformation;
import ke.co.apollo.health.common.domain.model.Principal;
import ke.co.apollo.health.common.domain.model.request.GetCustomerByPhoneNoRequest;
import ke.co.apollo.health.common.domain.model.request.GetCustomerByPolicyNoRequest;
import ke.co.apollo.health.common.domain.model.request.GetEntityDetailsRequest;
import ke.co.apollo.health.common.domain.model.response.GetCustomerResponse;
import ke.co.apollo.health.common.enums.EntityRoles;
import ke.co.apollo.health.common.utils.HealthDateUtils;
import ke.co.apollo.health.policy.mapper.hms.EntityHMSMapper;
import ke.co.apollo.health.policy.remote.EntityMaintenanceRemote;
import ke.co.apollo.health.policy.service.CustomerService;
import ke.co.apollo.health.policy.service.EntityMaintenanceService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityMaintenanceServiceImpl implements EntityMaintenanceService {

  @Autowired
  private EntityHMSMapper entityHMSMapper;

  @Autowired
  private EntityMaintenanceRemote entityMaintenanceRemote;

  @Autowired
  private CustomerService customerService;


  @Override
  public List<GetCustomerResponse> getEntityByPhoneNumber(GetCustomerByPhoneNoRequest request) {
    List<GetCustomerResponse> result = null;
    List<Customer> list = entityHMSMapper.getClientDetailsByPhoneNumber(request.getPhoneNumber());

    if (CollectionUtils.isNotEmpty(list)) {
      result = list.stream().map(this::fulfillCustomerResponse
      ).collect(Collectors.toList());
    }

    return result;
  }

  @Override
  public GetCustomerResponse getEntityByPolicyNumber(GetCustomerByPolicyNoRequest request) {

    GetCustomerResponse response = null;
    List<Customer> customers = entityHMSMapper
        .getClientDetailsByPolicyNumber(request.getPolicyNumber());

    Optional<Customer> p = customers.stream()
        .filter(customer -> StringUtils.isNotEmpty(customer.getPhoneNumber())).findFirst();

    if (p.isPresent()) {
      response = fulfillCustomerResponse(p.get());
    }

    return response;
  }

  private GetCustomerResponse fulfillCustomerResponse(Customer customer) {
    GetCustomerResponse result = null;
    if (customer != null) {
      result = GetCustomerResponse.builder().entityId(customer.getEntityId())
          .firstName(customer.getFirstName()).lastName(customer.getLastName())
          .title(customer.getTitle()).gender(customer.getGender())
          .dateOfBirth(customer.getDateOfBirth()).phoneNumber(customer.getPhoneNumber()).build();
    }
    return result;
  }


  @Override
  public List<DependantDetail> getDependantsByPolicyIdAndEffectiveDate(Integer policyId,
      Date effectiveDate) {
    return entityHMSMapper
        .getDependantByPolicyIdAndEffectiveDate(policyId, DateFormatUtils.format(effectiveDate,
            GlobalConstant.YYYYMMDD));
  }

  @Override
  public Principal getPrincipleByEntityId(Integer entityId) {

    EntityDetails entityDetails = entityMaintenanceRemote
        .getEntityDetailsById(GetEntityDetailsRequest.builder().entityId(entityId).build());

    if (entityDetails == null) {
      return null;
    }
    Principal principal = Principal.builder().firstName(entityDetails.getFirstName())
        .lastName(entityDetails.getSurname())
        .title(entityDetails.getTitle()).entityId(entityDetails.getEntityId()).build();

    for (Roles role : entityDetails.getRoles()) {
      if (EntityRoles.CLIETN.getValue().equals(role.getRoleDescription())) {
        for (RoleAdditionalInformation ai : role.getRoleAdditionalInformation()) {
          fulfillPrincipal(principal, ai);
        }
      }
    }

    Customer customer = customerService.getCustomer(entityId);
    if (customer != null) {
      principal.setCustomerId(customer.getCustomerId());
      if (StringUtils.isEmpty(principal.getPhoneNumber())) {
        principal.setPhoneNumber(customer.getPhoneNumber());
      }
      if (StringUtils.isEmpty(principal.getIdNo())) {
        principal.setIdNo(customer.getIdNo());
      }
    }
    return principal;
  }

  private void fulfillPrincipal(Principal principal, RoleAdditionalInformation ai) {
    switch (ai.getInfoDescription()) {
      case "Date Of Birth":
        if (ai.getInfoValue().length() == 10) {
          principal.setDateOfBirth(
              HealthDateUtils.parseDate(ai.getInfoValue(), GlobalConstant.YYYYMMDD));
        } else {
          principal.setDateOfBirth(HealthDateUtils.parseDate(ai.getInfoValue(),
              GlobalConstant.YYYYMMDD_T_HHMMSS));
        }
        break;
      case "Email":
        principal.setEmail(ai.getInfoValue());
        break;
      case "Gender":
        principal.setGender(ai.getInfoValue());
        break;
      case "Mobile":
        principal.setPhoneNumber(ai.getInfoValue());
        break;
      case "ID No":
        principal.setIdNo(ai.getInfoValue());
        break;
      default:
        break;
    }
  }
}
