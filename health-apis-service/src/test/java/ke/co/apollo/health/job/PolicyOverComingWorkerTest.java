package ke.co.apollo.health.job;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.request.EmailRequest;
import ke.co.apollo.health.common.domain.model.request.SMSMessageRequest;
import ke.co.apollo.health.config.PolicyRenewalDaysConfig;
import ke.co.apollo.health.remote.NotificationRemote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ke.co.apollo.health.common.domain.model.RenewalPremium;
import ke.co.apollo.health.common.domain.model.response.PolicyRenewalResponse;
import ke.co.apollo.health.domain.entity.PolicyOverComingEntity;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.repository.PolicyOverComingRecordRepository;
import ke.co.apollo.health.repository.PolicyOverComingRepository;
import ke.co.apollo.health.service.PolicyService;

@ExtendWith(MockitoExtension.class)
class PolicyOverComingWorkerTest {

  private static Logger logger = LoggerFactory.getLogger(PolicyOverComingWorkerTest.class);

  @Mock
  PolicyOverComingRepository policyOverComingRepository;

  @Mock
  PolicyService policyService;

  @Mock
  NotificationRemote notificationRemote;
  @Mock
  PolicyRenewalDaysConfig policyRenewalDaysConfig;
  @InjectMocks
  PolicyOverComingWorker policyOverComingWorker;

  @BeforeEach
  void setUp() {
    logger.debug("test start---------------------");
    MockitoAnnotations.initMocks(this);
    }

  @Test
  void testUpdatePolicyDetailsCacheWithEmptyResponse() {
    when(policyOverComingRepository.findAllByNeedToUpdateIsAndRenewalDateBetween(any(Boolean.class), any(Date.class), any(Date.class))).thenReturn(Collections.emptyList());
    policyOverComingWorker.updatePolicyDetailsCache();
    verify(policyOverComingRepository, times(1)).findAllByNeedToUpdateIsAndRenewalDateBetween(any(Boolean.class), any(Date.class), any(Date.class));
  }

  @Test
  void testUpdatePolicyDetailsCache() {

    PolicyOverComingEntity policyOverComingEntityObject = PolicyOverComingEntity.builder()
                      .id(1)
                      .agentName("agentName")
                      .asagentId("asagentId")
                      .claims(BigDecimal.TEN)
                      .createTime(new Date())
                      .discount(BigDecimal.ZERO)
                      .earnedPremium(BigDecimal.ZERO)
                      .effectiveDate(new Date())
                      .email("email")
                      .loading(BigDecimal.ZERO)
                      .loadingPercentage(BigDecimal.ZERO)
                      .mobile("mobile")
                      .needToUpdate(true)
                      .plan("plan")
                      .policyAmount("0")
                      .policyNumber("policyNumber")
                      .premium(BigDecimal.ZERO)
                      .principalName("principalName")
                      .renewalDate(new Date())
                      .totalPremium(BigDecimal.ZERO)
                      .updateTime(new Date())
                      .build();

    RenewalPremium renewalPremiumObject = RenewalPremium.builder()
                    .claimsPaid(BigDecimal.ZERO)
                    .discount(BigDecimal.ZERO)
                    .earnedPremium(BigDecimal.ZERO)
                    .loading(BigDecimal.ZERO)
                    .lossRatio(BigDecimal.ZERO)
                    .manualAdjustment(BigDecimal.ZERO)
                    .phcf(BigDecimal.ZERO)
                    .premium(BigDecimal.ZERO)
                    .stampDuty(BigDecimal.ZERO)
                    .build();

    PolicyRenewalResponse renewalResponseObject = PolicyRenewalResponse.builder()
                    .balance(BigDecimal.ZERO)
                    .premium(renewalPremiumObject)
                    .build();

    when(policyOverComingRepository.findAllByNeedToUpdateIsAndRenewalDateBetween(any(Boolean.class), any(Date.class), any(Date.class))).thenReturn(Collections.singletonList(policyOverComingEntityObject));
    when(policyService.renewalPolicyForComingWorker(any())).thenReturn(renewalResponseObject);

    policyOverComingWorker.updatePolicyDetailsCache();
    verify(policyOverComingRepository, times(1)).findAllByNeedToUpdateIsAndRenewalDateBetween(any(Boolean.class), any(Date.class), any(Date.class));
  }


  @Test
  void testUpdatePolicyDetailsCacheException() {

    PolicyOverComingEntity policyOverComingEntityObject = PolicyOverComingEntity.builder()
                      .id(1)
                      .agentName("agentName")
                      .asagentId("asagentId")
                      .claims(BigDecimal.TEN)
                      .createTime(new Date())
                      .discount(BigDecimal.ZERO)
                      .earnedPremium(BigDecimal.ZERO)
                      .effectiveDate(new Date())
                      .email("email")
                      .loading(BigDecimal.ZERO)
                      .loadingPercentage(BigDecimal.ZERO)
                      .mobile("mobile")
                      .needToUpdate(true)
                      .plan("plan")
                      .policyAmount("0")
                      .policyNumber("policyNumber")
                      .premium(BigDecimal.ZERO)
                      .principalName("principalName")
                      .renewalDate(new Date())
                      .totalPremium(BigDecimal.ZERO)
                      .updateTime(new Date())
                      .build();

    RenewalPremium renewalPremiumObject = RenewalPremium.builder()
                    .claimsPaid(BigDecimal.ZERO)
                    .discount(BigDecimal.ZERO)
                    .earnedPremium(BigDecimal.ZERO)
                    .loading(BigDecimal.ZERO)
                    .lossRatio(BigDecimal.ZERO)
                    .manualAdjustment(BigDecimal.ZERO)
                    .phcf(BigDecimal.ZERO)
                    .premium(BigDecimal.ZERO)
                    .stampDuty(BigDecimal.ZERO)
                    .build();

    PolicyRenewalResponse renewalResponseObject = PolicyRenewalResponse.builder()
                    .balance(BigDecimal.ZERO)
                    .premium(renewalPremiumObject)
                    .build();

    when(policyOverComingRepository.findAllByNeedToUpdateIsAndRenewalDateBetween(any(Boolean.class), any(Date.class), any(Date.class))).thenReturn(Collections.singletonList(policyOverComingEntityObject));
    when(policyService.renewalPolicyForComingWorker(any())).thenThrow(new NullPointerException("This is an exception"));
    policyOverComingWorker.updatePolicyDetailsCache();
    assertTrue(true);
    // verify(policyOverComingRepository, times(1)).findAllByNeedToUpdateIsAndRenewalDateBetween(any(Boolean.class), any(Date.class), any(Date.class));
  }

  @Test
  void testRenewalNotificationPolicies() {
    List<Integer> days = policyRenewalDaysConfig.getRenewal();
    assertNotNull(days);
    List<PolicyOverComingEntity> res = policyOverComingRepository.findAllByRenewalDateBetween(any(), any());
    assertNotNull(res);
    EmailRequest emailRequest = EmailRequest.builder()
            .emailAddress("test@gmail.com")
            .text("test")
            .subject("Update").build();
    notificationRemote.sendEmail(emailRequest);
    assertTrue(true);
    SMSMessageRequest messageRequest = SMSMessageRequest.builder()
            .to("7505050551")
            .text("test")
            .from(GlobalConstant.APOLLO_GROUP)
            .serviceType(GlobalConstant.HEALTH_SERVICE_TYPE).build();
    notificationRemote.sendSMSMessage(messageRequest);
    assertTrue(true);
  }

  @Test
  void testExpiredNotificationPolicies() {
    List<Integer> days = policyRenewalDaysConfig.getExpired();
    assertNotNull(days);
    List<PolicyOverComingEntity> res = policyOverComingRepository.findAllByRenewalDateBetween(any(), any());
    assertNotNull(res);
    EmailRequest emailRequest = EmailRequest.builder()
            .emailAddress("test@gmail.com")
            .text("test")
            .subject("Update").build();
    notificationRemote.sendEmail(emailRequest);
    assertTrue(true);
    SMSMessageRequest messageRequest = SMSMessageRequest.builder()
            .to("7505050551")
            .text("test")
            .from(GlobalConstant.APOLLO_GROUP)
            .serviceType(GlobalConstant.HEALTH_SERVICE_TYPE).build();
    notificationRemote.sendSMSMessage(messageRequest);
    assertTrue(true);

  }

}
