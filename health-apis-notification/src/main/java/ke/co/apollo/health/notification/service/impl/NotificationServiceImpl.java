package ke.co.apollo.health.notification.service.impl;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.CustomerPolicyCache;
import ke.co.apollo.health.common.domain.model.MarketingPreference;
import ke.co.apollo.health.common.domain.model.Notification;
import ke.co.apollo.health.common.domain.model.Policy;
import ke.co.apollo.health.common.domain.model.PolicyNotificationTask;
import ke.co.apollo.health.common.domain.model.request.EmailAttachmentRequest;
import ke.co.apollo.health.common.domain.model.request.EmailRequest;
import ke.co.apollo.health.common.domain.model.request.SMSMessageRequest;
import ke.co.apollo.health.common.domain.model.request.SNSNotificationRequest;
import ke.co.apollo.health.common.domain.model.response.SMSMessageResponse;
import ke.co.apollo.health.common.enums.InsuranceEnum;
import ke.co.apollo.health.common.enums.NotificationType;
import ke.co.apollo.health.common.enums.PolicyRenewalType;
import ke.co.apollo.health.common.enums.PolicyStatus;
import ke.co.apollo.health.common.enums.TaskStatusEnum;
import ke.co.apollo.health.common.utils.HealthDateUtils;
import ke.co.apollo.health.common.utils.JsonUtils;
import ke.co.apollo.health.notification.mapper.health.HealthMapper;
import ke.co.apollo.health.notification.mapper.intermediary.IntermediaryMapper;
import ke.co.apollo.health.notification.remote.NotificationRemote;
import ke.co.apollo.health.notification.service.CustomerService;
import ke.co.apollo.health.notification.service.NotificationService;
import ke.co.apollo.health.notification.service.SNSService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  HealthMapper healthMapper;

  @Autowired
  CustomerService customerService;

  @Autowired
  NotificationRemote notificationRemote;

  @Autowired
  SNSService snsService;

  @Value("${server.port}")
  String port;

  @Autowired
  IntermediaryMapper intermediaryMapper;

  @Override
  public SMSMessageResponse sendSMSMessageRequest(SMSMessageRequest smsMessageRequest) {
    return notificationRemote.sendSMSMessage(smsMessageRequest);
  }

  @Override
  public boolean sendSMSMessage(SMSMessageRequest smsMessageRequest) {
    SMSMessageResponse smsMessageResponse = this.sendSMSMessageRequest(smsMessageRequest);
    return smsMessageResponse != null && CollectionUtils.isEmpty(smsMessageResponse.getMessages());
  }

  @Override
  public String sendPolicySMSMessage(SMSMessageRequest smsMessageRequest) {
    String messageId = "";
    if (this.sendSMSMessage(smsMessageRequest)) {
      messageId = "success";
    }
    return messageId;
  }

  @Override
  public boolean sendReminder() {
    String now = HealthDateUtils.currentDateTimeString();
    logger.debug("send reminder task， port: {}, time: {}", port, now);
    return true;
  }

  @Override
  public boolean createPolicyRenewalNotificationTask() {
    boolean result = true;
    List<CustomerPolicyCache> policyCaches = healthMapper.selectCustomerPolicyCache();
    for (CustomerPolicyCache customerPolicyCache : policyCaches) {
      if (customerPolicyCache.getCheckTime() != null && DateUtils
          .isSameDay(customerPolicyCache.getCheckTime(), new Date())) {
        continue;
      }
      List<Policy> policyList = customerPolicyCache.getPolicyList();
      if (CollectionUtils.isNotEmpty(policyList)) {
        List<MarketingPreference> marketingPreferenceList = customerService
            .getMarketingPreference(customerPolicyCache.getEntityId(), "health");
        if (CollectionUtils.isNotEmpty(marketingPreferenceList)) {
          Customer customer = customerService
              .getCustomerByEntityId(customerPolicyCache.getEntityId());
          if (customer == null) {
            MarketingPreference marketingPreference = marketingPreferenceList.get(0);
            List<Policy> policies = JsonUtils.objectConvertToList(policyList, Policy[].class);
            this.createPolicyRenewalTask(marketingPreference, customer, policies);
          }
        }
      }
      customerPolicyCache.setCheckTime(new Date());
      result = healthMapper.updateCustomerPolicyCache(customerPolicyCache) == 1;
    }
    return result;
  }

  private void createPolicyRenewalTask(MarketingPreference marketingPreference, Customer customer,
      List<Policy> policies) {
    for (Policy policy : policies) {
      PolicyRenewalType category = this
          .checkRenewalDate(policy.getPolicyStatus(), policy.getPolicyRenewalDate());
      if (category != null) {
        this.createPolicyNotificationTask(marketingPreference, customer, category, policy);
      }
    }
  }

  private PolicyRenewalType checkRenewalDate(String status, Date renewalDate) {
    Date currentDate = new Date();
    PolicyRenewalType category = null;
    if (PolicyStatus.LAPSE.getValue().equals(status)) {
      if (HealthDateUtils.same(currentDate, renewalDate, 14)) {
        category = PolicyRenewalType.AFTER14DAYS;
      } else if (HealthDateUtils.same(currentDate, renewalDate, 29)) {
        category = PolicyRenewalType.AFTER29DAYS;
      }
    } else if (PolicyStatus.LIVE.getValue().equals(status)) {
      if (HealthDateUtils.same(currentDate, renewalDate, -30)) {
        category = PolicyRenewalType.BEFORE30DAYS;
      } else if (HealthDateUtils.same(currentDate, renewalDate, -14)) {
        category = PolicyRenewalType.BEFORE14DAYS;
      } else if (HealthDateUtils.same(currentDate, renewalDate, -1)) {
        category = PolicyRenewalType.BEFORE1DAY;
      }
    }
    logger.debug("check policy renewal date: {}", category);
    return category;
  }

  private boolean createPolicyNotificationTask(MarketingPreference marketingPreference,
      Customer customer, PolicyRenewalType category, Policy policy) {
    boolean sms = marketingPreference.isSms();
    boolean email = marketingPreference.isEmail();
    if (sms) {
      String text = this.createMessageText(category, NotificationType.SMS, policy);
      PolicyNotificationTask task = this
          .buildPolicyNotificationTask("", text, customer.getPhoneNumber(),
              policy.getPolicyStatus(), NotificationType.SMS.getValue(), policy);
      this.createPolicyNotificationTask(task);
    }

    if (email) {
      String text = this.createMessageText(category, NotificationType.EMAIL, policy);
      PolicyNotificationTask task = this
          .buildPolicyNotificationTask("Policy Renewal Reminder", text, customer.getEmail(),
              policy.getPolicyStatus(),
              NotificationType.EMAIL.getValue(), policy);
      this.createPolicyNotificationTask(task);
    }

    String text = this.createMessageText(category, NotificationType.NOTIFICATION, policy);
    PolicyNotificationTask task = this
        .buildPolicyNotificationTask("Policy Renewal Reminder", text, customer.getSuperCustomerId(),
            policy.getPolicyStatus(),
            NotificationType.NOTIFICATION.getValue(), policy);
    this.createPolicyNotificationTask(task);

    return true;
  }

  private String createMessageText(PolicyRenewalType category, NotificationType notificationType,
      Policy policy) {
    String text = "";
    switch (notificationType) {
      case SMS:
        text = this.createSMSText(category, policy);
        break;
      case EMAIL:
        text = this.createSMSText(category, policy);
        break;
      case NOTIFICATION:
        text = this.createSMSText(category, policy);
        break;
      default:

    }
    return text;
  }

  private String createSMSText(PolicyRenewalType category, Policy policy) {
    String message = "";
    String param = "";
    String template = "Hi, You policy [{0}] {1} expired for {2}, please process your renewal in policy details page.";
    String type = category.getValue();
    String days = type.substring(1);
    if ("1".equals(days)) {
      days = days + " day";
    } else {
      days = days + " days";
    }

    if (type.startsWith("A")) {
      param = "has";
    } else if (type.startsWith("B")) {
      param = "will be";
    }
    message = MessageFormat.format(template, policy.getPolicyNumber(), param, days);
    return message;
  }

  private PolicyNotificationTask buildPolicyNotificationTask(String subject, String text,
      String destination, String category, String type, Policy policy) {
    return PolicyNotificationTask.builder().text(text).subject(subject)
        .destination(destination).type(type)
        .category(category)
        .status(TaskStatusEnum.TODO.getValue()).failureNumber(0)
        .policyNumber(policy.getPolicyNumber())
        .scheduleTime(HealthDateUtils.getGMTDate(6)).createTime(new Date()).build();
  }

  @Override
  public boolean sendPolicyNotificationReminder() {
    List<PolicyNotificationTask> taskList = this.searchPolicyNotificationTask();
    Date currentTime = new Date();
    if (!CollectionUtils.isEmpty(taskList)) {
      taskList.parallelStream().forEach(task -> {
        if (this.checkScheduleTime(task.getScheduleTime(), currentTime)) {
          String messageId = this.sendNotification(task);
          if (StringUtils.isNotBlank(messageId) || NotificationType.NOTIFICATION.getValue()
              .equals(task.getType())) {
            task.setMessageId(messageId);
            task.setStatus(TaskStatusEnum.DONE.getValue());
          } else {
            int failureNumber = task.getFailureNumber() + 1;
            task.setFailureNumber(failureNumber);
          }
          task.setUpdateTime(new Date());
          this.updatePolicyNotificationTask(task);
        }
      });
    }

    return true;
  }

  @Override
  public boolean sendInstantNotificationTask(PolicyNotificationTask task) {
    SMSMessageRequest request = SMSMessageRequest.builder().from(GlobalConstant.APOLLO_GROUP)
        .to(task.getDestination())
        .text(task.getText())
        .serviceType(GlobalConstant.HEALTH_SERVICE_TYPE).build();
    String messageId = this.sendPolicySMSMessage(request);
    if (StringUtils.isNotBlank(messageId)) {
      task.setMessageId(messageId);
      task.setStatus(TaskStatusEnum.DONE.getValue());
    } else {
      task.setStatus(TaskStatusEnum.TODO.getValue());
      task.setFailureNumber(1);
    }
    task.setUpdateTime(new Date());
    return this.createPolicyNotificationTask(task);
  }

  private String sendNotification(PolicyNotificationTask task) {
    String messageId = null;
    if (NotificationType.SMS.getValue().equals(task.getType())) {
      SMSMessageRequest request = SMSMessageRequest.builder()
          .from(GlobalConstant.APOLLO_GROUP)
          .to(task.getDestination())
          .text(task.getText())
          .serviceType(GlobalConstant.HEALTH_SERVICE_TYPE).build();
      messageId = this.sendPolicySMSMessage(request);
    } else if (NotificationType.EMAIL.getValue().equals(task.getType())) {
      EmailRequest request = EmailRequest.builder().subject(task.getSubject())
          .emailAddress(task.getDestination()).text(task.getText()).build();
      messageId = this.sendEmail(request) ? "Mail Sent Successfully" : null;
    } else if (NotificationType.NOTIFICATION.getValue().equals(task.getType())) {
      SNSNotificationRequest request = SNSNotificationRequest.builder().cognitoId(
          task.getDestination()).message(task.getText()).build();
      messageId = snsService.sendSNSNotification(request);
      intermediaryMapper.insert(
          Notification.builder().superCustomerId(task.getDestination()).title(task.getSubject())
              .type(InsuranceEnum.HEALTH.getValue()).status("New").content(task.getSubject())
              .build());
    }
    return messageId;
  }

  @Override
  public boolean sendEmail(EmailRequest request) {
    return notificationRemote.sendEmail(request) != null;
  }

  @Override
  public boolean sendEmailAttachment(EmailAttachmentRequest request) {
    return notificationRemote.sendEmail(request) != null;
  }

  @Override
  public List<PolicyNotificationTask> searchPolicyNotificationTask() {
    return healthMapper.selectByType(
        Arrays.asList(TaskStatusEnum.PENDING.getValue(), TaskStatusEnum.TODO.getValue()));
  }

  @Override
  public boolean createPolicyNotificationTask(PolicyNotificationTask task) {
    return healthMapper.insert(task) == 1;
  }

  @Override
  public boolean updatePolicyNotificationTask(PolicyNotificationTask task) {
    return healthMapper.updateStatus(task) == 1;
  }

  @Override
  public boolean cancelPolicyNotificationTask(PolicyNotificationTask task) {
    List<PolicyNotificationTask> taskList = healthMapper
        .selectByTypeAndPolicyNumber(NotificationType.SMS.getValue(), task.getPolicyNumber());
    for (PolicyNotificationTask smsTask : taskList) {
      if (TaskStatusEnum.TODO.getValue().equals(smsTask.getStatus())) {
        smsTask.setStatus(TaskStatusEnum.CANCEL.getValue());
        smsTask.setUpdateTime(new Date());
        this.updatePolicyNotificationTask(smsTask);
      }
    }
    return true;
  }

  @Override
  public boolean deletePolicyNotificationTask(String taskId) {
    return healthMapper.delete(taskId) == 1;
  }

  private boolean checkScheduleTime(Date scheduleTime, Date currentTime) {
    if (scheduleTime == null || currentTime == null) {
      return false;
    }
    return currentTime.after(scheduleTime);
  }

}
