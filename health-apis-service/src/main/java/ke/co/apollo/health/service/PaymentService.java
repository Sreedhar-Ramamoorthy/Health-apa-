package ke.co.apollo.health.service;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import freemarker.template.TemplateException;
import ke.co.apollo.health.common.domain.model.PaymentHistory;
import ke.co.apollo.health.common.domain.model.PaymentTransaction;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.request.CreditCardTransactionRequest;
import ke.co.apollo.health.common.domain.model.request.PaymentCreateRequest;
import ke.co.apollo.health.common.domain.model.request.PaymentRequest;
import ke.co.apollo.health.common.domain.model.request.PaymentValidateRequest;
import ke.co.apollo.health.common.domain.model.response.PaymentHistoryListResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionCreateResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionValidateResponse;
import ke.co.apollo.health.domain.request.MpesaExpressRequest;
import ke.co.apollo.health.domain.response.MainMpesaExpressResponse;
import ke.co.apollo.health.domain.response.PaymentHistoryResponse;

public interface PaymentService {

  TransactionCreateResponse createPaymentTransaction(PaymentCreateRequest request);

  TransactionValidateResponse validatePaymentTransaction(PaymentValidateRequest request) throws TemplateException, IOException;

  List<PaymentTransaction> getTransactionListByPolicyNumber(PolicyNumberRequest request);

  Boolean paymentRequest(PaymentRequest paymentRequest);

  PaymentHistory selectPHByCheckoutRequestId(PaymentHistory paymentHistory);

  List<PaymentHistoryListResponse> selectPHByPolicyId(String policyId);

  List<PaymentHistoryResponse> getPaymentTransactionList(String customerId, String policyNumber);

  MainMpesaExpressResponse requestStk(MpesaExpressRequest request);

  HashMap<String, String> createCreditCardPaymentRequest(CreditCardTransactionRequest request);

}
