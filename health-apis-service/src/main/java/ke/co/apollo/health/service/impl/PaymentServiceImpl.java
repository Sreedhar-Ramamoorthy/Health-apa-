package ke.co.apollo.health.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import ke.co.apollo.health.common.domain.model.*;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.InAppNotificationMessageResponse;
import ke.co.apollo.health.common.utils.HealthDateUtils;
import ke.co.apollo.health.domain.entity.HealthStepEntity;
import ke.co.apollo.health.domain.request.MpesaExpressRequest;
import ke.co.apollo.health.domain.response.MainMpesaExpressResponse;
import ke.co.apollo.health.enums.HealthQuoteStepsEnum;
import ke.co.apollo.health.mapper.health.QuoteMapper;
import ke.co.apollo.health.repository.HealthAgentBranchRepository;
import ke.co.apollo.health.repository.HealthStepRepository;
import ke.co.apollo.health.service.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.remote.PaymentWalletResponse;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.response.PaymentHistoryListResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionCreateResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionValidateResponse;
import ke.co.apollo.health.common.enums.PolicyStatus;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.config.NotificationMessageBuilder;
import ke.co.apollo.health.domain.request.QuoteBalanceUpdateRequest;
import ke.co.apollo.health.domain.request.TransactionDetailTaskAddRequest;
import ke.co.apollo.health.domain.response.PaymentHistoryResponse;
import ke.co.apollo.health.enums.PaymentMethod;
import ke.co.apollo.health.mapper.health.PaymentHistoryMapper;
import ke.co.apollo.health.mapper.health.PaymentTransactionMapper;
import ke.co.apollo.health.remote.NotificationRemote;
import ke.co.apollo.health.remote.PaymentRemote;
import ke.co.apollo.health.remote.PolicyRemote;

import static ke.co.apollo.health.common.enums.ProductEnum.JAMIIPLUS_SHARED;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

  @Autowired
  private PaymentRemote paymentRemote;

  @Autowired
  private  PolicyRemote policyRemote;

  @Autowired
  private  NotificationRemote notificationRemote;

  @Autowired
  private  PaymentHistoryMapper paymentHistoryMapper;

  @Autowired
  private  PaymentTransactionMapper paymentTransactionMapper;

  @Autowired
  private  QuoteService quoteService;

  @Autowired
  private  CustomerService customerService;

  @Autowired
  private  TransactionDetailService transactionDetailService;

  @Autowired
  private  NotificationMessageBuilder notificationMessageBuilder;

  @Autowired
  private  PolicyService policyService;

  @Autowired
  IntermediaryService intermediaryService;

  @Autowired
  HealthStepRepository stepRepository;

  @Autowired
  HealthAgentBranchRepository agentBranchRepository;

  @Autowired
  QuoteMapper quoteMapper;

  @Value("${business.email.recipient.address}")
  String destinationEmail;

  private final Configuration configuration;

  private final Gson g;


  public PaymentServiceImpl(Configuration configuration, Gson gson) {
    this.configuration = configuration;
    this.g = gson;
  }

  @Override
  @Transactional("healthDataTransactionManager")
  public TransactionCreateResponse createPaymentTransaction(PaymentCreateRequest request) {

    log.info("create payment transaction start, PaymentCreateRequest: {}", g.toJson(request));

    String quoteNumber = "";
    Integer policyId;
    String policyNumber;
    Date effectiveDate;
    String business;

    String quoteId = request.getQuoteId();
    String customerId = request.getCustomerId();
    Customer customer = customerService.getCustomer(customerId);
    if (customer == null) {
      throw new BusinessException("Can't find customer [" + customerId + "]");
    }
    if (request.isRenewal()) {
      if (StringUtils.isEmpty(request.getPolicyNumber())) {
        throw new BusinessException("Policy Number is mandatory");
      }
      if (request.getEffectiveDate() == null) {
        throw new BusinessException("Effective Date is mandatory");
      }
      policyNumber = request.getPolicyNumber();
      effectiveDate = request.getEffectiveDate();
      PolicyDetail policyDetail = policyRemote.getPolicyDetail(
          PolicyNumberRequest.builder().policyNumber(policyNumber).effectiveDate(effectiveDate)
              .build());
      if (policyDetail == null) {
        throw new BusinessException(
            "Can't find policy, policyNumber[" + policyNumber + "], effectiveDate[" + effectiveDate + "]");
      }

      if (!(PolicyStatus.LIVE.getValue().equals(policyDetail.getPolicyStatus())||PolicyStatus.LAPSE.getValue().equals(policyDetail.getPolicyStatus()))) {
        throw new BusinessException("Policy[" + policyNumber + "] has expired, policyStatus: " + policyDetail.getPolicyStatus());
      }

      policyId = policyDetail.getPolicyId();
      business = "renewal policy";
      updateStep(policyNumber, customer.getAgentId(), customerId, HealthQuoteStepsEnum.PAYMENT_DIALOG);
    } else {
      updateStep(quoteId, customer.getAgentId(), customerId, HealthQuoteStepsEnum.PAYMENT_DIALOG);
      if (StringUtils.isEmpty(request.getQuoteId())) {
        throw new BusinessException("Quote Id is mandatory");
      }
      //
      Quote quote = quoteService.getQuote(quoteId, customerId, null);
      this.checkQuote(quoteId, quote);

      quoteNumber = quote.getCode();
      policyId = quote.getExtPolicyId();
      policyNumber = quote.getExtPolicyNumber();
      effectiveDate = quote.getEffectiveDate();
      business = "buy new policy";
    }

    BigDecimal amount = new BigDecimal(request.getAmount());
    PaymentTransaction paymentTransaction = PaymentTransaction.builder()
        .customerId(customerId).status("New")
        .amount(amount).renewal(request.isRenewal())
        .quoteId(quoteId).quoteNumber(quoteNumber)
        .policyId(String.valueOf(policyId))
        .policyNumber(policyNumber).effectiveDate(effectiveDate)
        .createTime(new Date())
        .build();
    paymentTransactionMapper.insert(paymentTransaction);

    TransactionCreateRequest transactionCreateRequest = TransactionCreateRequest.builder()
            .amount(amount).applicationCustomerEmail(customer.getEmail())
            .applicationPolicyNumber(policyNumber)
            .applicationCustomerId(customer.getCustomerId())
            .applicationScene(business)
            .applicationType("health")
            .build();
    TransactionCreateResponse response = paymentRemote.createTransaction(transactionCreateRequest);
    paymentTransaction.setOrderId(response.getOrderId());
    paymentTransaction.setPaymentCustomerId(response.getCustomerId());
    paymentTransaction.setMerchantId(response.getMerchantId());
    paymentTransaction.setTerminalId(response.getTerminalId());
    paymentTransaction.setCurrency(response.getCurrency());
    paymentTransaction.setDomain(response.getDomain());
    paymentTransaction.setPreauth(response.getPreauth());
    paymentTransaction.setTransactionRef(response.getTransactionRef());
    paymentTransaction.setUpdateTime(new Date());
    paymentTransactionMapper.updateByPrimaryKey(paymentTransaction);
    return response;
  }

  public void updateStep(String quoteOrPolicy, String agentId, String customerId, HealthQuoteStepsEnum step){
    log.info("\n ==== created step {} : for quote/policy {} ==== \n " , step, quoteOrPolicy);
    stepRepository.save(HealthStepEntity
            .builder()
            .quoteOrPolicyId(quoteOrPolicy)
            .agentId(agentId)
            .customerId(customerId)
            .step(step)
            .build());
  }

  private void checkQuote(String quoteId, Quote quote) {
    if (quote == null) {
      throw new BusinessException(
          "Can't find quote[" + quoteId + "]");
    }

    if (PolicyStatus.NEW.getValue().equals(quote.getStatus()) || PolicyStatus.VIEWED.getValue()
        .equals(quote.getStatus())) {
      throw new BusinessException(
          " Quote[" + quoteId + "] is in process, can't create payment transaction");
    }
    if (StringUtils.isBlank(quote.getExtPolicyNumber()) || quote.getEffectiveDate() == null) {
      throw new BusinessException(
          " Can't find policy number for quote[" + quoteId + "]");
    }
  }

  @Override
  public TransactionValidateResponse validatePaymentTransaction(PaymentValidateRequest request) throws TemplateException, IOException {
    log.info("\n\n===== PAYMENT VALIDATION =====");
    log.info("\n validate payment transaction start, Validation Payload : {}", new Gson().toJson(request));
    PaymentTransaction transaction = paymentTransactionMapper.select(request.getCustomerId(), request.getOrderId(), request.getTransactionRef());
    if (transaction == null) {
      throw new BusinessException("Can't find the transaction [customerId:" + request.getCustomerId() + ", orderId:" + request.getOrderId() + ", transactionRef:" + request.getTransactionRef() + "]");
    }

    log.info("ORDER ID ::: {}", transaction.getOrderId());
    log.info("REQUEST ORDER ID ::: {}", request.getOrderId());
    log.info("CUSTOMER ID :::: {}", transaction.getCustomerId());
    log.info("MPESA ACCOUNT REF :::: {}", request.getMpesaAccountReference());
    log.info("MPESA CONFIRMATION CODE :::: {}", request.getMpesaRefOrCheckoutId());
    log.info("TRANSACTION REF :::: {}", request.getTransactionRef());

    TransactionValidateRequest transactionValidateRequest = TransactionValidateRequest.builder()
            .applicationType("health")
            .message(request.getMessage())
            .orderId(request.getOrderId())
            .mpesaRefOrCheckoutId(request.getMpesaRefOrCheckoutId())
            .transactionRef(request.getTransactionRef())
            .paymentMethod(request.getPaymentMethod())
            .paymentResponse(request.getPaymentResponse())
            .success(request.isSuccess())
            .resultExternalReference(request.getExternalReference())
            .mpesaAccountReference(request.getMpesaAccountReference())
            .build();
    TransactionValidateResponse response = paymentRemote.validateTransaction(transactionValidateRequest);
    String status = response.isSuccess() ? GlobalConstant.PAYMENT_SUCCESS : GlobalConstant.PAYMENT_FAILED;
    String msg = response.getMessage();
    if (StringUtils.isNotBlank(msg) && msg.length() > 1000) {
      msg = msg.substring(0, 1000);
    }

    transaction.setStatus(status);
    transaction.setPaymentMethod(request.getPaymentMethod());
    transaction.setPaymentMessage(msg);
    transaction.setClientResult(request.isSuccess());
    transaction.setClientMessage(request.getMessage());
    transaction.setUpdateTime(new Date());

    log.info("\n\n===== PAYMENT VALIDATION STATUS =====");
    log.info("MESSAGE ::: {}", response.getMessage());
    log.info("STATUS :::: {}", status);
    paymentTransactionMapper.update(transaction);
    String inAppMessage = notificationMessageBuilder.getMessage("INAPP_NOTIFICATION_MESSAGE_PAID_POLICY", transaction.getPolicyNumber());
    if (response.isSuccess()) {
      sendPaymentNotification(transaction);
      if(transaction.getQuoteId()!=null) {
        updateQuoteStartDate(transaction.getCustomerId(), transaction.getQuoteId());
      }
      sendPaymentInAppNotification(transaction, inAppMessage);
      try{
        this.updateBalance(transaction);
      }catch (Exception e){
        log.info("\n\n ==== Failed to do Update for balances before validation ==== {}", e.getMessage());
      }
      log.warn("Update transaction with {} : ", transaction);
    }
    log.debug("validate payment transaction end");
    return response;
  }

  public void sendPaymentNotification(PaymentTransaction transaction) throws IOException, TemplateException {
    log.warn("Sending notification  {} ", transaction);
    String customerId = transaction.getCustomerId();
    Customer customer  = customerService.getCustomer(customerId);
    List<Customer> dependants = customerService.getCustomerByParentId(customerId);
    StringWriter stringWriter = new StringWriter();
    Map<String, Object> model = new HashMap<>();
    model.put("holderName", customer.getFirstName().toUpperCase() + " " +customer.getLastName().toUpperCase()  );
    model.put("holderId", StringUtils.isNotBlank(customer.getIdNo())?customer.getIdNo():"N/A");
    model.put("holderPhoneNumber", customer.getPhoneNumber());
    model.put("holderEmail", customer.getEmail());
    model.put("holderGender", customer.getGender().toUpperCase());
    model.put("dependants", dependants);
    model.put("policyNumber", transaction.getPolicyNumber());
    model.put("username", "Apollo Group");
    model.put("premium", transaction.getAmount());
    model.put("benefit", customer.getBenefit());
    String travel = "NO";
    if(transaction.isRenewal()){
      HealthPolicy policy = policyService.getPolicy(transaction.getPolicyNumber(),transaction.getEffectiveDate());
      model.put("benefitValues", policy.getBenefit());
      if(policy.getBenefit().getTravelInsurance() != null  && policy.getBenefit().getTravelInsurance() > 0) {
        travel = "YES";
      }
      model.put("productType", "");
    }else{
      Quote quote = quoteService.getQuoteByPolicyNumber(customerId, transaction.getPolicyNumber());
      model.put("benefitValues", quote.getBenefit());
      if(quote.getBenefit().getTravelInsurance() != null && quote.getBenefit().getTravelInsurance() > 0) {
        travel = "YES";
      }
      model.put("productType", getProductTypeDescription(quote));
    }
    model.put("travel", travel);
    if(customer.getAgentId() != null) {
      Intermediary intermediary = intermediaryService.getIntermediary(customer.getAgentId());
      model.put("agentName", intermediary.getFirstName().toUpperCase()  + " " + intermediary.getLastName().toUpperCase() );
      model.put("agentEmail", intermediary.getEmail());
      model.put("agentPhoneNumber", intermediary.getPhoneNumber());
      model.put("agentStatus", intermediary.getStatus());
      model.put("isAgent", 1);
      model.put("agentBranch", intermediary.getBranchName().toUpperCase() );
    }else{
      model.put("isAgent", 0);
    }


    if(configuration != null) {

      Template temp = configuration.getTemplate("business_notification_email_template.ftlh");

      if(null != temp){
        temp.process(model, stringWriter);
        String template = stringWriter.getBuffer().toString();
        EmailAttachmentRequest req = new EmailAttachmentRequest();
        req.setEmailAddress(destinationEmail);
        req.setSubject("New Business - " + transaction.getPolicyNumber());

        if(transaction.isRenewal())
          req.setSubject("Renewal Business - " + transaction.getPolicyNumber());

        req.setText(template);
        req.setAttachmentPath("");
        req.setAttachmentName("No_Attachment");
        log.warn("Email Request  {} ", req);
        notificationRemote.sendEmailWithTemplate(req);
      }
    }
    log.warn("Sending notification end {} ", model);
  }
  public String getProductTypeDescription(Quote quote){
    if(quote.getProductId().equals(JAMIIPLUS_SHARED.getId())){
      return "Family Shared";
    }
    return "Individual";
  }

  private void updateBalance(PaymentTransaction transaction) {
    log.debug("update balance start");
    boolean result = false;
    String msg = "";
    try {
      if (transaction.isRenewal()) {
        result = policyService
            .updatePolicyRenewalBalance(transaction.getPolicyNumber(),
                transaction.getEffectiveDate(),
                transaction.getAmount());
      } else {
        result = quoteService.updateQuoteBalance(
            QuoteBalanceUpdateRequest.builder().quoteId(transaction.getQuoteId())
                .customerId(transaction.getCustomerId())
                .paidAmount(transaction.getAmount())
                .build());
      }
    } catch (Exception e) {
      msg = e.getMessage();
      log.error("update balance exception: {}", msg);
      if (StringUtils.isNotBlank(msg) && msg.length() > 1000) {
        msg = msg.substring(0, 1000);
      }
    }

    transaction.setBalanceResult(result);
    transaction.setBalanceMessage(msg);
    transaction.setUpdateTime(new Date());
    result = paymentTransactionMapper.update(transaction) == 1;
    log.debug("update balance end {}", result);
  }

  @Override
  public List<PaymentTransaction> getTransactionListByPolicyNumber(PolicyNumberRequest request) {
    return paymentTransactionMapper
        .selectList(request.getPolicyNumber(), request.getEffectiveDate());
  }

  @Override
  @Transactional("healthDataTransactionManager")
  public Boolean paymentRequest(PaymentRequest request) {
    boolean result = false;
    PaymentHistory paymentHistory = null;
    if (request.getRenewal().booleanValue()) {
      if (StringUtils.isEmpty(request.getPolicyNumber())) {
        throw new BusinessException("Policy Number is mandatory");
      }
      if (request.getEffectiveDate() == null) {
        throw new BusinessException("Effective Date is mandatory");
      }
      if (StringUtils.isEmpty(request.getPremium())) {
        throw new BusinessException("Premium is mandatory");
      }
      paymentHistory = PaymentHistory.builder()
          .paymentPhone(request.getPaymentPhoneNumber())
          .paymentType(request.getPaymentMethods()).amount(request.getAmount())
          .premium(request.getPremium()).renewal(request.getRenewal().booleanValue())
          .policyNumber(request.getPolicyNumber()).effectiveDate(request.getEffectiveDate())
          .customerId(request.getCustomerId()).build();
      paymentHistoryMapper.addPaymentHistoryRenewal(paymentHistory);
    } else {
      Quote quote = quoteService.getQuote(request.getQuoteId(), request.getCustomerId(), null);
      if (quote != null) {
        request.setPolicyNumber(quote.getExtPolicyNumber());
        paymentHistory = PaymentHistory.builder()
            .paymentPhone(request.getPaymentPhoneNumber())
            .paymentType(request.getPaymentMethods()).amount(request.getAmount())
            .premium(quote.getPremium().getTotalPremium().toPlainString())
            .renewal(request.getRenewal().booleanValue())
            .quoteNumber(request.getQuoteId())
            .customerId(request.getCustomerId()).build();
        paymentHistoryMapper.addPaymentHistory(paymentHistory);
      } else {
        throw new BusinessException(
            "Client[" + request.getCustomerId() + "] doesn't has quote[" + request.getQuoteId()
                + "]");
      }
    }
    MpesaExpressRequest re = MpesaExpressRequest.builder()
            .accountReference(request.getPolicyNumber())
            .customerPhoneNumber(request.getPaymentPhoneNumber())
            .description(request.getPolicyNumber())
            .payableAmount(Double.parseDouble(request.getAmount()))
            .transactionType("PAYMENT")
            .serviceType(GlobalConstant.HEALTH_SERVICE_TYPE)
            .build();
    switch (PaymentMethod.getPaymentMethod(request.getPaymentMethods())) {
      case MPESA:
        result = processMpesa(re, paymentHistory);
        break;
      case BANKTRANSFER:
        result = processBankTransfer(request);
        break;
      default:
    }

    return result;
  }

  public boolean processBankTransfer(PaymentRequest request) {
    boolean result;
    result = notificationRemote
        .sendSMSMessage(
            SMSMessageRequest.builder().from(GlobalConstant.APOLLO_GROUP)
                .to(request.getPaymentPhoneNumber())
                .text(notificationMessageBuilder.getMessage("SMS_MESSAGE_BANK_TRANSFER"))
                .serviceType(GlobalConstant.HEALTH_SERVICE_TYPE).build());
    //Due to we can not trace bank transfer response,therefore we assume bank transfer succeed and update balance directly after send SMS
    if (request.getRenewal().booleanValue()) {
      policyService
          .updatePolicyRenewalBalance(request.getPolicyNumber(), request.getEffectiveDate(),
              new BigDecimal(request.getAmount()));
    } else {
      quoteService.updateQuoteBalance(
          QuoteBalanceUpdateRequest.builder().quoteId(request.getQuoteId())
              .customerId(request.getCustomerId())
              .paidAmount(new BigDecimal(request.getAmount()))
              .build());
    }
    return result;
  }

  public boolean processMpesa(MpesaExpressRequest request,
      PaymentHistory paymentHistory) {
    boolean result = false;
    PaymentWalletResponse response = paymentRemote.paymentWalletRequest(request);
    if (response != null) {
      if (0 == response.getResponseCode()) {
        result = true;
        transactionDetailService.createTransactionDetailTask(
            TransactionDetailTaskAddRequest.builder()
                .checkoutRequestId(response.getCheckoutRequestId())
                .scheduleTime(
                    new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .build());
      }
      paymentHistoryMapper.appendMpesaResponse(
          PaymentHistory.builder().id(paymentHistory.getId())
              .merchantRequestId(response.getMerchantRequestId())
              .checkoutRequestId(response.getCheckoutRequestId())
              .customerMsg(response.getCustomerMessage())
              .responseCode(String.valueOf(response.getResponseCode()))
              .responseDesc(response.getResponseDescription()).build());
    }
    return result;
  }

  @Override
  public PaymentHistory selectPHByCheckoutRequestId(PaymentHistory paymentHistory) {
    List<PaymentHistory> paymentHistories = paymentHistoryMapper
        .selectByCheckoutRequestId(paymentHistory);
    PaymentHistory result = null;
    if (CollectionUtils.isNotEmpty(paymentHistories)) {
      result = paymentHistories.get(0);
    }
    return result;
  }

  @Override
  public List<PaymentHistoryListResponse> selectPHByPolicyId(String policyId) {

    List<PaymentHistoryListResponse> result = null;
    List<PaymentHistory> paymentHistories = paymentHistoryMapper.selectByPolicyId(policyId);
    if (CollectionUtils.isNotEmpty(paymentHistories)) {
      result = new ArrayList<>();
      BigDecimal balance = null;
      calculateBalance(result, paymentHistories, balance);
      result.sort((t1, t2) -> {
        if (t1.getCreateTime().getTime() == t2.getCreateTime().getTime()) {
          return 0;
        } else if (t1.getCreateTime().getTime() > (t2.getCreateTime().getTime())) {
          return 1;
        } else {
          return -1;
        }
      });

    }

    return result;
  }

  private void calculateBalance(List<PaymentHistoryListResponse> result,
      List<PaymentHistory> paymentHistories, BigDecimal balance) {
    String status;
    PaymentHistory ph;
    for (int i = 0; i < paymentHistories.size(); i++) {
      status = GlobalConstant.PAYMENT_FAILED;
      ph = paymentHistories.get(i);
      if (i == 0) {
        balance = new BigDecimal(ph.getBalance());
      }
      switch (PaymentMethod.getPaymentMethod(ph.getPaymentType())) {
        case MPESA:
          if ("0".equals(ph.getResponseCode())) {
            if (i > 0) {
              balance = balance.add(new BigDecimal(ph.getAmount()));
            }
            status = GlobalConstant.PAYMENT_SUCCESS;
          }
          break;
        case BANKTRANSFER:
          if (i > 0) {
            balance = balance.add(new BigDecimal(ph.getAmount()));
          }
          status = GlobalConstant.PAYMENT_SUCCESS;
          break;
        default:
          break;
      }
      PaymentHistoryListResponse phResponse = PaymentHistoryListResponse.builder()
          .amount(ph.getAmount()).responseCode(ph.getResponseCode()).balance(balance)
          .paymentType(ph.getPaymentType()).premium(new BigDecimal(ph.getPremium()))
          .status(status).createTime(ph.getCreateTime()).updateTime(ph.getUpdateTime())
          .paymentDate(DateFormatUtils.format(ph.getCreateTime(), GlobalConstant.YYYYMMDD_HHMMSS))
          .build();
      result.add(phResponse);
    }
  }

  @Override
  public List<PaymentHistoryResponse> getPaymentTransactionList(String customerId,
      String policyNumber) {
    List<PaymentHistoryResponse> responseList = new ArrayList<>();
    List<PaymentTransaction> paymentTransactionList = paymentTransactionMapper
        .selectByCustomerIdAndPolicyNumber(customerId, policyNumber, "Success");
    if (CollectionUtils.isNotEmpty(paymentTransactionList)) {
      paymentTransactionList.stream().forEach(t -> {
        PaymentHistoryResponse response = PaymentHistoryResponse.builder().date(t.getUpdateTime())
            .premium(t.getAmount()).paymentMethod(t.getPaymentMethod())
            .transactionRef(t.getTransactionRef()).build();
        responseList.add(response);
      });
    }
    return responseList;
  }

  @Override
  public MainMpesaExpressResponse requestStk(MpesaExpressRequest request) {
    log.info("Health M-pesa request : {}", request);
    PaymentWalletResponse paymentWalletResponse = paymentRemote.paymentWalletRequest(request);
    return MainMpesaExpressResponse.builder()
            .data(paymentWalletResponse)
            .success(true)
            .msg(paymentWalletResponse.getResponseDescription())
            .build();
  }

  public void updateQuoteStartDate(String customerId, String quoteId){
    Quote quote = quoteMapper.getQuote(quoteId, customerId, null);
    if(quote != null) {
      quote.setStartDate(new Date());
      quote.setEffectiveDate(new Date());
      quote.setRenewalDate(HealthDateUtils.nextYear(new Date()));
      quote.setUpdateTime(new Date());
      quoteMapper.update(quote);
    }
  }

  public void sendPaymentInAppNotification(PaymentTransaction transaction, String message) {
    Customer customer  = customerService.getCustomer(transaction.getCustomerId());
    if(customer != null) {
      notificationRemote.createInAppNotification(
              InAppNotificationCreateRequest.builder()
                      .notification(message)
                      .readStatus(GlobalConstant.READ_STATUS)
                      .phoneNumber(customer.getPhoneNumber())
                      .email(customer.getEmail())
                      .serviceType(GlobalConstant.HEALTH_SERVICE_TYPE)
                      .actionStatus(GlobalConstant.HEALTH_PAYMENT_NOTIFICATION_STATUS)
                      .notificationSubject(GlobalConstant.HEALTH_PAYMENT_NOTIFICATION_THANK_TITLE)
                      .policyNumber(transaction.getPolicyNumber())
                      .build());
      clearRenewalInAppNotification(customer.getPhoneNumber(), transaction.getPolicyNumber());
    }
  }

  public void clearRenewalInAppNotification(String phoneNumber, String policyNumber) {
    log.info("clearRenewalInAppNotification: {}", phoneNumber);
    List<InAppNotificationMessageResponse> responseList = notificationRemote.getAllInAppNotificationList(
            InAppNotificationMessageRequest.builder()
                    .pageSize(100)
                    .pageNumber(0)
                    .phoneNumber(phoneNumber)
                    .build());
    List<Integer> ids = responseList.stream()
            .filter(response ->
                    response.getPolicyNumber() != null && response.getPolicyNumber().equals(policyNumber) &&
                            response.getActionStatus() != null && response.getActionStatus().equals(GlobalConstant.HEALTH_RENEWAL_NOTIFICATION_STATUS))
            .map(InAppNotificationMessageResponse::getId)
            .map(Integer::parseInt)
            .collect(Collectors.toList());
    log.info("clearRenewalInAppNotification ids : {}", ids);
    boolean result = notificationRemote.clearInAppNotification(ids);
    log.info("clearRenewalInAppNotification Result : {}", result);
  }
  public HashMap<String, String> createCreditCardPaymentRequest(CreditCardTransactionRequest request) {
    return paymentRemote.createCreditCardPaymentRequest(request);
  }

}
