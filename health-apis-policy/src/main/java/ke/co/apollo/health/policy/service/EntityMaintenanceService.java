package ke.co.apollo.health.policy.service;

import java.util.Date;
import java.util.List;
import ke.co.apollo.health.common.domain.model.DependantDetail;
import ke.co.apollo.health.common.domain.model.Principal;
import ke.co.apollo.health.common.domain.model.request.GetCustomerByPhoneNoRequest;
import ke.co.apollo.health.common.domain.model.request.GetCustomerByPolicyNoRequest;
import ke.co.apollo.health.common.domain.model.response.GetCustomerResponse;

public interface EntityMaintenanceService {

  List<GetCustomerResponse> getEntityByPhoneNumber(GetCustomerByPhoneNoRequest phoneNumber);

  GetCustomerResponse getEntityByPolicyNumber(GetCustomerByPolicyNoRequest request);

  List<DependantDetail> getDependantsByPolicyIdAndEffectiveDate(Integer policyId,
      Date effectiveDate);

  Principal getPrincipleByEntityId(Integer entityId);

}
