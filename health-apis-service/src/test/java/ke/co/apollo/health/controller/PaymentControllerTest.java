package ke.co.apollo.health.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

import freemarker.template.TemplateException;
import ke.co.apollo.health.common.domain.model.request.CreditCardTransactionRequest;
import ke.co.apollo.health.domain.request.MpesaExpressRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import ke.co.apollo.health.common.domain.model.PaymentTransaction;
import ke.co.apollo.health.common.domain.model.remote.PaymentWalletResponse;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsRequest;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsResponse;
import ke.co.apollo.health.common.domain.model.request.PaymentCreateRequest;
import ke.co.apollo.health.common.domain.model.request.PaymentRequest;
import ke.co.apollo.health.common.domain.model.request.PaymentValidateRequest;
import ke.co.apollo.health.common.domain.model.response.PaymentHistoryListResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionCreateResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionValidateResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.config.NotificationMessageBuilder;
import ke.co.apollo.health.domain.request.PaymentHistoryRequest;
import ke.co.apollo.health.domain.response.PaymentHistoryResponse;
import ke.co.apollo.health.remote.NotificationRemote;
import ke.co.apollo.health.remote.PaymentRemote;
import ke.co.apollo.health.service.PaymentService;

class PaymentControllerTest {

    @InjectMocks
    PaymentController paymentController;

    @Mock
    PaymentService paymentService;

    @Mock
    NotificationRemote notificationRemote;

    @Mock
    PaymentRemote paymentRemote;

    @Mock
    NotificationMessageBuilder notificationMessageBuilder;


    @BeforeEach
    void setUpMocks() {
        initMocks(this);
    }

    @Test
    void testTestSMS() {
        when(notificationMessageBuilder.getMessage(anyString())).thenReturn("SENT");

        when(notificationRemote.sendSMSMessage(any())).thenReturn(true);
        ResponseEntity<DataWrapper> wrapper = paymentController.testSMS("999");
        assertNotNull(wrapper);
    }

    @Test
    void testPaymentRequest() {
        when(paymentService.paymentRequest(any())).thenReturn(true);
        ResponseEntity<DataWrapper> wrapper = paymentController.paymentRequest(PaymentRequest.builder().build());
        assertNotNull(wrapper);
    }

    @Test
    void testGetPaymentTransactionList() {
        when(paymentService.getPaymentTransactionList(anyString(), anyString())).thenReturn(Collections.singletonList(PaymentHistoryResponse.builder().build()));
        ResponseEntity<DataWrapper> wrapper = paymentController.getPaymentTransactionList(PaymentHistoryRequest.builder().build());
        assertNotNull(wrapper);
    }


    @Test
    void testCreatePaymentTransaction() {
        when(paymentService.createPaymentTransaction(any())).thenReturn(TransactionCreateResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = paymentController.createPaymentTransaction(PaymentCreateRequest.builder().build());
        assertNotNull(wrapper);
    }

    @Test
    void testValidatePaymentTransaction() throws TemplateException, IOException {
        when(paymentService.validatePaymentTransaction(any())).thenReturn(TransactionValidateResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = paymentController.validatePaymentTransaction(PaymentValidateRequest.builder().build());
        assertNotNull(wrapper);
    }

    @Test
    void testGetTransactionListByPolicyNumber() {
        when(paymentService.getTransactionListByPolicyNumber(any())).thenReturn(Collections.singletonList(PaymentTransaction.builder().build()));
        ResponseEntity<DataWrapper> wrapper = paymentController.getTransactionListByPolicyNumber(PolicyNumberRequest.builder().build());
        assertNotNull(wrapper);
    }

    @Test
    void testPaymentHistory() {
        when(paymentService.selectPHByPolicyId(anyString())).thenReturn(Collections.singletonList(PaymentHistoryListResponse.builder().build()));
        ResponseEntity<DataWrapper> wrapper = paymentController.paymentHistory("001");
        assertNotNull(wrapper);
    }

    @Test
    void testTest1() {
        when(paymentRemote.paymentWalletRequest(any())).thenReturn(PaymentWalletResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = paymentController.test1(MpesaExpressRequest.builder().build());
        assertNotNull(wrapper);
    }

    @Test
    void testTest2() {
        when(paymentRemote.transactionDetails(any())).thenReturn(TransactionDetailsResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = paymentController.test2(TransactionDetailsRequest.builder().build());
        assertNotNull(wrapper);
    }

    @Test
    void PaymentReceipt() {
        CreditCardTransactionRequest cr = new CreditCardTransactionRequest();
        HashMap<String, String> hashMap = paymentController.creditcardrequest(cr);
        when(paymentService.createCreditCardPaymentRequest(cr)).thenReturn(hashMap);
        assertNotNull(hashMap);
    }

}
