package ke.co.apollo.health.service.impl;


import java.util.Date;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import ke.co.apollo.health.domain.request.TransactionDetailTaskAddRequest;
import ke.co.apollo.health.mapper.health.TransactionDetailTaskMapper;
import ke.co.apollo.health.common.domain.model.TransactionDetailTask;
import ke.co.apollo.health.common.domain.model.PaymentHistory;
import ke.co.apollo.health.service.PolicyService;
import ke.co.apollo.health.service.QuoteService;

class TransactionDetailServiceImplTest {

        @InjectMocks
        TransactionDetailServiceImpl transactionDetailService;

        @Mock
        TransactionDetailTaskMapper transactionDetailTaskMapper;

        @Mock
        PolicyService policyService;

        @Mock
        QuoteService quoteService;

        @BeforeEach
        void setUpMocks(){
                initMocks(this);
        }
  

        @Test
        void createTransactionDetailTaskTest(){
                when(transactionDetailTaskMapper.initTransactionDetail(any())).thenReturn(1);
                TransactionDetailTaskAddRequest obj = TransactionDetailTaskAddRequest.builder()
                        .checkoutRequestId("checkoutRequestId")
                        .scheduleTime(new Date())
                        .build();
                boolean b = transactionDetailService.createTransactionDetailTask(obj);
                assertTrue(b);
        }


        @Test
        void createTransactionDetailTaskZeroTest(){
                when(transactionDetailTaskMapper.initTransactionDetail(any())).thenReturn(0);
                TransactionDetailTaskAddRequest obj = TransactionDetailTaskAddRequest.builder()
                        .checkoutRequestId("checkoutRequestId")
                        .scheduleTime(new Date())
                        .build();
                boolean b = transactionDetailService.createTransactionDetailTask(obj);
                assertFalse(b);
        }

        @Test
        void updateTransactionDetailTaskTest(){
                when(transactionDetailTaskMapper.updateByPrimaryKey(any())).thenReturn(1);
                TransactionDetailTask obj = TransactionDetailTask.builder()
                        .taskId("taskId")
                        .build();
                boolean b = transactionDetailService.updateTransactionDetailTask(obj);
                assertTrue(b);
                }

        @Test
        void updateTransactionDetailTaskZeroTest(){
                when(transactionDetailTaskMapper.updateByPrimaryKey(any())).thenReturn(0);
                TransactionDetailTask obj = TransactionDetailTask.builder()
                        .taskId("taskId")
                        .build();
                boolean b = transactionDetailService.updateTransactionDetailTask(obj);
                assertFalse(b);
                }

        @Test
        void cancelTransactionDetailTaskTest(){

                //doNothing().when(transactionDetailTaskMapper).deleteByPrimaryKey(any());
                when(transactionDetailTaskMapper.deleteByPrimaryKey(any())).thenReturn(1);

                TransactionDetailTask obj = TransactionDetailTask.builder()
                        .taskId("taskId")
                        .build();
                
                boolean b = transactionDetailService.cancelTransactionDetailTask(obj);
                assertFalse(b);
        }

        @Test
        void updateBalanceRenewalTrueTest(){
                when(policyService.updatePolicyRenewalBalance(any(),any(),any())).thenReturn(true);
                PaymentHistory obj = PaymentHistory.builder()
                                .renewal(true)
                                .policyNumber("xyz")
                                .amount("0")
                                .effectiveDate(new Date())
                        .build();
                transactionDetailService.updateBalance(obj);
                Mockito.verify(policyService, Mockito.times(1)).updatePolicyRenewalBalance(any(), any(), any());
        }

        @Test
        void updateBalanceRenewalFalseTest(){
                when(quoteService.updateQuoteBalance(any())).thenReturn(true);
                PaymentHistory obj = PaymentHistory.builder()
                                .renewal(false)
                                .policyNumber("xyz")
                                .amount("0")
                                .quoteNumber("123")
                                .customerId("customerId")
                                .effectiveDate(new Date())
                        .build();
                transactionDetailService.updateBalance(obj);
                Mockito.verify(quoteService, Mockito.times(1)).updateQuoteBalance(any());
        }

        @Test
        void updateBalanceRenewalPaymentIsNullTest(){
                when(quoteService.updateQuoteBalance(any())).thenReturn(true);
                PaymentHistory obj = null;
                transactionDetailService.updateBalance(obj);
                Mockito.verify(quoteService, Mockito.times(0)).updateQuoteBalance(any());
        }

}
