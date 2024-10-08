package ke.co.apollo.health.service;

import ke.co.apollo.health.common.domain.model.ApplicationRenewalPolicy;
// import ke.co.apollo.health.common.domain.model.Commission;
import ke.co.apollo.health.common.domain.model.HealthPolicy;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.ApplicationRenewalPolicyListResponse;
import ke.co.apollo.health.common.domain.model.response.PolicyOverComingResponseDto;
import ke.co.apollo.health.common.domain.model.response.PolicyRenewalResponse;
import ke.co.apollo.health.domain.request.ApplicationPolicyListSearchRequest;
import ke.co.apollo.health.domain.request.CustomerIdRequest;
import ke.co.apollo.health.domain.response.HealthPolicyListResponse;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface PolicyService {

  List<HealthPolicyListResponse> getCustomerPolicyList(CustomerPolicyListRequest request);

  List<HealthPolicyListResponse> getCustomerPolicyList(EntityPolicyListRequest request);

  boolean createCustomerPolicyCache(CustomerIdRequest request);

  void processUpdateCustomerPolicyCacheTask();

  PolicyRenewalResponse renewalPolicy(PolicyRenewalRequest renewalRequest);

  PolicyRenewalResponse renewalPolicyForComingWorker(PolicyRenewalRequest renewalRequest);

  boolean updatePolicyRenewalBalance(String policyNumber, Date effectiveDate, BigDecimal amount);

  HealthPolicy getPolicy(String policyNumber, Date effectiveDate);

  ApplicationRenewalPolicyListResponse searchApplicationRenewalPolicyList(
      ApplicationPolicyListSearchRequest request);

  ApplicationRenewalPolicy getApplicationRenewalPolicy(PolicyNumberRequest request);

  boolean archiveApplicationRenewalPolicy(PolicyNumberRequest request);

  PolicyOverComingResponseDto policyUpdateDetails(ComingPolicyListRequest request);
  byte[] comingPolicyListInExcel(ComingPolicyListRequestInExcel request);
  void renewalsDueIn60Days();

  void renewalNotificationPolicies() throws InterruptedException;

  void expiredNotificationPolicies() throws InterruptedException;
}
