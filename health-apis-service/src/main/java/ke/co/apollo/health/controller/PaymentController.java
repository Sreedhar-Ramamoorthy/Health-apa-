package ke.co.apollo.health.controller;

import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.PaymentTransaction;
import ke.co.apollo.health.common.domain.model.remote.PaymentWalletResponse;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsRequest;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsResponse;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.TransactionCreateResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionValidateResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.config.NotificationMessageBuilder;
import ke.co.apollo.health.domain.request.MpesaExpressRequest;
import ke.co.apollo.health.domain.request.PaymentHistoryRequest;
import ke.co.apollo.health.domain.response.MainMpesaExpressResponse;
import ke.co.apollo.health.domain.response.PaymentHistoryResponse;
import ke.co.apollo.health.remote.NotificationRemote;
import ke.co.apollo.health.remote.PaymentRemote;
import ke.co.apollo.health.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Payment management
 *
 * @author Rick
 * @version 1.0
 * @see
 * @since 9/25/2020
 */
@RestController
@RequestMapping("/health/api")
@Api(tags = "Health App Payment API")
public class PaymentController {

  @Autowired
  private PaymentService paymentService;

  @PostMapping("/payment/pay")
  @ApiOperation("Pay premium for buying policy")
  public ResponseEntity<DataWrapper> paymentRequest(
      @ApiParam(name = "PaymentRequest", value = "Payment Wallet Request Payload", required = true)
      @Valid @RequestBody PaymentRequest paymentRequest) {

    Boolean pay = paymentService.paymentRequest(paymentRequest);
    Map<String, Boolean> result = new HashMap<>();
    result.put("result", pay);
    return ResponseEntity.ok(new DataWrapper(result));

  }

  @PostMapping("/payment/create")
  @ApiOperation("Create InterSwitch payment transaction")
  public ResponseEntity<DataWrapper> createPaymentTransaction(
      @ApiParam(name = "PaymentCreateRequest", value = "Payment Wallet Request Payload", required = true)
      @Valid @RequestBody PaymentCreateRequest request) {

    TransactionCreateResponse response = paymentService.createPaymentTransaction(request);
    return ResponseEntity.ok(new DataWrapper(response));

  }

  @PostMapping("/payment/stk")
  @ApiOperation("Create Mpesa Stk request")
  public ResponseEntity<DataWrapper> createMpesaStk(
      @ApiParam(name = "PaymentStkRequest", value = "Payment Wallet Stk Request Payload", required = true)
      @Valid @RequestBody MpesaExpressRequest request) {
    MainMpesaExpressResponse response = paymentService.requestStk(request);
    return ResponseEntity.ok(new DataWrapper(response));

  }

  @PostMapping("/payment/validation")
  @ApiOperation("Validate InterSwitch payment transaction")
  public ResponseEntity<DataWrapper> validatePaymentTransaction(
      @ApiParam(name = "PaymentCreateRequest", value = "Payment Wallet Request Payload", required = true)
      @Valid @RequestBody PaymentValidateRequest request) throws TemplateException, IOException {

    TransactionValidateResponse response = paymentService.validatePaymentTransaction(request);
    return ResponseEntity.ok(new DataWrapper(response));

  }


  @PostMapping("/payment/transaction")
  @ApiOperation("Get InterSwitch payment transaction")
  public ResponseEntity<DataWrapper> getTransactionListByPolicyNumber(
      @ApiParam(name = "PaymentCreateRequest", value = "Payment Wallet Request Payload", required = true)
      @Valid @RequestBody PolicyNumberRequest request) {

    List<PaymentTransaction> response = paymentService.getTransactionListByPolicyNumber(request);
    return ResponseEntity.ok(new DataWrapper(response));

  }

  @PostMapping("/payment/history")
  @ApiOperation("Get payment history")
  public ResponseEntity<DataWrapper> paymentHistory(
      @ApiParam(name = "PaymentRequest", value = "Payment Wallet Request Payload", required = true)
      @Valid @RequestBody String policyId) {

    return ResponseEntity.ok(new DataWrapper(paymentService.selectPHByPolicyId(policyId)));

  }

  @PostMapping("/policy/payment/history")
  @ApiOperation("Get payment transaction list")
  public ResponseEntity<DataWrapper> getPaymentTransactionList(
      @Valid @RequestBody PaymentHistoryRequest request) {

    List<PaymentHistoryResponse> response = paymentService
        .getPaymentTransactionList(request.getCustomerId(), request.getPolicyNumber());
    return ResponseEntity.ok(new DataWrapper(response));

  }


  @Autowired
  private PaymentRemote paymentRemote;

  @PostMapping("/payment/test1")
  @ApiOperation("Pay premium for buying policy")
  public ResponseEntity<DataWrapper> test1(
      @ApiParam(name = "PaymentRequest", value = "Payment Wallet Request Payload", required = true)
      @Valid @RequestBody MpesaExpressRequest paymentRequest) {

    PaymentWalletResponse pay = paymentRemote.paymentWalletRequest(paymentRequest);
    Map<String, PaymentWalletResponse> result = new HashMap<>();
    result.put("result1", pay);
    return ResponseEntity.ok(new DataWrapper(result));

  }

  @PostMapping("/payment/test2")
  @ApiOperation("Transaction detail")
  public ResponseEntity<DataWrapper> test2(
      @ApiParam(name = "PaymentRequest", value = "Payment Wallet Request Payload", required = true)
      @Valid @RequestBody TransactionDetailsRequest paymentRequest) {

    TransactionDetailsResponse pay = paymentRemote.transactionDetails(paymentRequest);
    Map<String, TransactionDetailsResponse> result = new HashMap<>();
    result.put("result2", pay);
    return ResponseEntity.ok(new DataWrapper(result));

  }


  @Autowired
  NotificationRemote notificationRemote;

  @Autowired
  NotificationMessageBuilder notificationMessageBuilder;

  @PostMapping("/payment/test3")
  @ApiOperation("test sms")
  public ResponseEntity<DataWrapper> testSMS(
      @Valid @RequestBody String phoneNumber) {
    boolean result = notificationRemote
        .sendSMSMessage(
            SMSMessageRequest.builder().from(GlobalConstant.APOLLO_GROUP)
                .to(phoneNumber)
                .text(notificationMessageBuilder.getMessage("SMS_MESSAGE_BANK_TRANSFER"))
                .serviceType(GlobalConstant.HEALTH_SERVICE_TYPE).build());

    return ResponseEntity
        .ok(new DataWrapper(result));
  }

  @PostMapping("/payment/creditcardrequest")
  @ApiOperation("Create creditcard request")
  public HashMap<String, String> creditcardrequest(
          @ApiParam(name = "Creditcardrequest", value = "Creditcardrequest Payload", required = true)
          @Valid @RequestBody CreditCardTransactionRequest request) {
    HashMap<String, String> response = paymentService.createCreditCardPaymentRequest(request);
    return response;

  }

}
