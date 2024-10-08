package ke.co.apollo.health.remote.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.Principal;
import ke.co.apollo.health.common.domain.model.remote.PaymentWalletRequest;
import ke.co.apollo.health.common.domain.model.remote.PaymentWalletResponse;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsRequest;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsResponse;
import ke.co.apollo.health.common.domain.model.request.CreditCardTransactionRequest;
import ke.co.apollo.health.common.domain.model.request.PaymentRequest;
import ke.co.apollo.health.common.domain.model.request.TransactionCreateRequest;
import ke.co.apollo.health.common.domain.model.request.TransactionValidateRequest;
import ke.co.apollo.health.common.domain.model.response.TransactionCreateResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionValidateResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.domain.request.CustomerSearchRequest;
import ke.co.apollo.health.domain.request.MpesaExpressRequest;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import ke.co.apollo.health.remote.AbstractRemote;
import ke.co.apollo.health.remote.PaymentRemote;
import ke.co.apollo.health.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

@Component
public class PaymentRemoteImpl extends AbstractRemote implements PaymentRemote {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${apa.health-apis-policy.url}")
  String healthApisPolicyBaseUrl;

  @Value("${apa.lms-apis-service.url}")
  String lmsApisServiceBaseUrl;

//  @Value("${apa.payment-wallet-apis.service.base-url}")
  String paymentWalletApisServiceBaseUrl = "https://8887-2405-201-e019-409d-0-4197-58f8-4da5.ngrok-free.app";

  @Autowired
  private CustomerService customerService;

  private ObjectMapper mapper = new ObjectMapper();

  @Override
  public PaymentWalletResponse paymentWalletRequest(MpesaExpressRequest paymentRequest) {
    PaymentWalletResponse paymentWalletResponse = null;
    String url = paymentWalletApisServiceBaseUrl + "/payment-wallet/mpesa-express/payment-request";
    logger.info(url);
    logger.info("paymentRequest: {}", paymentRequest);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      // create a map for post parameters
      //PaymentWalletRequest paymentWalletRequest = this.fulfillPaymentRequest(paymentRequest);
      Map<String, Object> map = new HashMap<>();
      map.put("accountReference", paymentRequest.getAccountReference());
      map.put("customerPhoneNumber", paymentRequest.getCustomerPhoneNumber());
      map.put("description", paymentRequest.getDescription());
      map.put("payableAmount", paymentRequest.getPayableAmount());
      map.put("serviceType", paymentRequest.getServiceType());
      map.put("kraPin", paymentRequest.getKraPin());

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
      ResponseEntity<DataWrapper> responseEntity = restTemplate
          .postForEntity(url, entity, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      if (body != null && body.getCode() == ReturnCode.OK.getValue()) {
        Object data = body.getData();
        logger.info("paymentWalletRequest result: {}", data);
        if (data != null) {
          paymentWalletResponse = mapper
              .convertValue(data, new TypeReference<PaymentWalletResponse>() {
              });
        }
      }
    } catch (Exception e) {
      logger.error("Payment exception: {}", e.getMessage());

    }
    return paymentWalletResponse;
  }

  @Override
  public TransactionDetailsResponse transactionDetails(
      TransactionDetailsRequest transactionDetailsRequest) {
    TransactionDetailsResponse transactionDetailsResponse = null;

    String url = healthApisPolicyBaseUrl + "/payment/wallet/detail";
    logger.debug(url);
    logger.debug("paymentWalletRequest: {}", transactionDetailsRequest);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      // create a map for post parameters
      Map<String, String> map = new HashMap<>();
      map.put("checkoutRequestId", transactionDetailsRequest.getCheckoutRequestId());

      HttpEntity<Map<String, String>> entity = new HttpEntity<>(map, headers);
      ResponseEntity<DataWrapper> responseEntity = restTemplate
          .postForEntity(url, entity, DataWrapper.class);
      DataWrapper body = responseEntity.getBody();
      if (body != null && body.getCode() == ReturnCode.OK.getValue()) {
        Object data = body.getData();
        logger.debug("transactionDetails result: {}", data);
        if (data != null) {
          transactionDetailsResponse = mapper
              .convertValue(data, new TypeReference<TransactionDetailsResponse>() {
              });
        }
      }
    } catch (Exception e) {
      logger.error("Transaction details exception: {}", e.getMessage());
    }

    return transactionDetailsResponse;
  }

  private PaymentWalletRequest fulfillPaymentRequest(PaymentRequest paymentRequest) {

    CustomerDetailResponse customer = customerService.getCustomer(
        CustomerSearchRequest.builder().customerId(paymentRequest.getCustomerId()).build());

    Principal principal = customer.getPrincipal();
    String description = "";
    if (paymentRequest.getRenewal().booleanValue()) {
      description = "renewal-" + paymentRequest.getPolicyNumber();
    } else {
      description = "rfq-" + paymentRequest.getPolicyNumber();
    }

    return PaymentWalletRequest.builder()
        .accountReference(principal.getIdNo())
        .customerPhoneNumber(paymentRequest.getPaymentPhoneNumber())
        .description(description).kraPin(principal.getKraPin())
        .payableAmount(paymentRequest.getAmount()).serviceType(GlobalConstant.HEALTH_SERVICE_TYPE)
        .build();
  }

  @Override
  public TransactionCreateResponse createTransaction(TransactionCreateRequest request) {
    String url = lmsApisServiceBaseUrl + "/life/api/transaction/create";
    return super.postForDataWrapper(new TypeReference<TransactionCreateResponse>() {
    }, url, request, "create payment transaction");
  }

  @Override
  public TransactionValidateResponse validateTransaction(TransactionValidateRequest request) {
    String url = lmsApisServiceBaseUrl + "/life/api/transaction/validation";
    return super.postForDataWrapper(new TypeReference<TransactionValidateResponse>() {
    }, url, request, "validate payment transaction");
  }

  @Override
  public HashMap<String, String> createCreditCardPaymentRequest(CreditCardTransactionRequest request) {
    HashMap<String, String> paymentWalletResponse = null;
    String url = paymentWalletApisServiceBaseUrl + "/payment-wallet/credit-card/payment-request";
    logger.info(url);
    logger.info("createCreditCardPaymentRequest: {}", request);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      Map<String, Object> map = new HashMap<>();
      map.put("amount", request.getAmount());
      map.put("referenceNumber", request.getReferenceNumber());
      map.put("billToForename", request.getBillToForename());
      map.put("billToSurname", request.getBillToSurname());
      map.put("billToEmail", request.getBillToEmail());
      map.put("orderId", request.getOrderId());
      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
      ResponseEntity<HashMap> responseEntity = restTemplate
              .postForEntity(url, entity, HashMap.class);
      HashMap<String, String> body = responseEntity.getBody();
      logger.info("createCreditCardPaymentRequest: {}",body);
      paymentWalletResponse = body;
    } catch (Exception e) {
      logger.error("createCreditCardPaymentRequest exception: {}", e.getMessage());

    }
    return paymentWalletResponse;
  }

}
