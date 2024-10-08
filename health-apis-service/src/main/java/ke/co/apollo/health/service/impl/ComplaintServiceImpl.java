package ke.co.apollo.health.service.impl;

import java.util.Date;
import ke.co.apollo.health.common.domain.model.PolicyDetail;
import ke.co.apollo.health.common.domain.model.Principal;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.request.EmailRequest;
import ke.co.apollo.health.config.NotificationMessageBuilder;
import ke.co.apollo.health.domain.entity.PolicyComplaintEntity;
import ke.co.apollo.health.domain.request.PolicyComplaintRequest;
import ke.co.apollo.health.remote.CustomerRemote;
import ke.co.apollo.health.remote.NotificationRemote;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.repository.PolicyComplaintRepository;
import ke.co.apollo.health.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ComplaintServiceImpl implements ComplaintService {

  @Value("${email.recipient.address}")
  private String recipientAddress;

  @Autowired
  NotificationMessageBuilder notificationMessageBuilder;

  @Autowired
  PolicyComplaintRepository policyComplaintRepository;

  @Autowired
  NotificationRemote notificationRemote;

  @Autowired
  PolicyRemote policyRemote;

  @Autowired
  CustomerRemote customerRemote;

  @Override
  public boolean submitComplaint(PolicyComplaintRequest request) {
    boolean result = false;
    String policyNumber = request.getPolicyNumber();
    Date effectiveDate = request.getEffectiveDate();
    PolicyComplaintEntity entity = PolicyComplaintEntity.builder().policyNumber(
        policyNumber).effectiveDate(effectiveDate).agentId(
        request.getAgentId()).customerId(request.getCustomerId()).title(request.getTitle()).content(
        request.getContent()).createTime(new Date()).updateTime(new Date()).build();
    policyComplaintRepository.save(entity);

    String subject = notificationMessageBuilder
        .getMessage("EMAIL_POLICY_COMPLAINT_SUBJECT", request.getPolicyNumber());
    EmailRequest emailRequest = EmailRequest.builder().build();
    emailRequest.setEmailAddress(recipientAddress);
    emailRequest.setSubject(subject);

    PolicyDetail policyDetail = policyRemote.getPolicyDetail(
        PolicyNumberRequest.builder().policyNumber(policyNumber).effectiveDate(effectiveDate)
            .build());
    if (policyDetail != null) {
      Principal principal = customerRemote
          .getPrincipalByEntityId(policyDetail.getPolicyHolderEntityId());
      emailRequest.setText(this.generateText(principal, policyDetail, request));
      result = notificationRemote.sendEmail(emailRequest);
    }
    return result;
  }

  private String generateText(Principal principal, PolicyDetail policyDetail,
      PolicyComplaintRequest request) {
    StringBuilder sb = new StringBuilder();
    sb.append("Full Name: ").append(policyDetail.getPolicyHolderName()).append("\n");
    sb.append("Policy Number: ").append(policyDetail.getPolicyNumber()).append("\n");
    sb.append("Email: ").append(principal.getEmail()).append("\n");
    sb.append("Mobile: ").append(principal.getPhoneNumber()).append("\n");

    sb.append("\n").append("Name Of Service Provider: ").append(request.getTitle()).append("\n");
    sb.append("Short Description Of Compliant Details: ").append(request.getContent()).append("\n");

    return sb.toString();

  }

}
