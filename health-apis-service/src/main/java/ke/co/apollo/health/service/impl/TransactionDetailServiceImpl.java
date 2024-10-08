package ke.co.apollo.health.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import ke.co.apollo.health.common.domain.model.PaymentHistory;
import ke.co.apollo.health.common.domain.model.TransactionDetailTask;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsRequest;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsResponse;
import ke.co.apollo.health.common.enums.PaymentStatus;
import ke.co.apollo.health.domain.request.QuoteBalanceUpdateRequest;
import ke.co.apollo.health.domain.request.TransactionDetailTaskAddRequest;
import ke.co.apollo.health.mapper.health.TransactionDetailTaskMapper;
import ke.co.apollo.health.remote.PaymentRemote;
import ke.co.apollo.health.service.PaymentService;
import ke.co.apollo.health.service.PolicyService;
import ke.co.apollo.health.service.QuoteService;
import ke.co.apollo.health.service.TransactionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionDetailServiceImpl implements TransactionDetailService {

  @Autowired
  TransactionDetailTaskMapper transactionDetailTaskMapper;

  @Autowired
  PaymentRemote paymentRemote;

  @Autowired
  private PaymentService paymentService;

  @Autowired
  private QuoteService quoteService;

  @Autowired
  private PolicyService policyService;

  @Override
  public boolean createTransactionDetailTask(TransactionDetailTaskAddRequest task) {
    return transactionDetailTaskMapper.initTransactionDetail(
        TransactionDetailTask.builder().checkoutRequestId(task.getCheckoutRequestId())
            .scheduleTime(task.getScheduleTime()).build()) == 1;
  }

  @Override
  public boolean updateTransactionDetailTask(TransactionDetailTask task) {
    return transactionDetailTaskMapper.updateByPrimaryKey(task) == 1;
  }

  @Override
  public boolean cancelTransactionDetailTask(TransactionDetailTask task) {
    transactionDetailTaskMapper.deleteByPrimaryKey(task.getTaskId());
    return false;
  }

  @Override
  public void processTransactionDetailTask() {

    List<TransactionDetailTask> tasks = transactionDetailTaskMapper
        .selectScheduledTasks(new Date(System.currentTimeMillis()));

    tasks.parallelStream().forEach(task -> {
      switch (PaymentStatus.getPaymentStatus(task.getPaymentStatus())) {

        case SUCCESSFUL:
        case FAILED:
          break;
        case PENDING:
        default:
          TransactionDetailsResponse response = paymentRemote.transactionDetails(
              TransactionDetailsRequest.builder().checkoutRequestId(task.getCheckoutRequestId())
                  .build());
          if (response != null && response.getResponseCode() != null) {
            transactionDetailTaskMapper
                .syncTransactionResponse(TransactionDetailTask.builder().taskId(task.getTaskId())
                    .checkoutRequestId(task.getCheckoutRequestId()).type(response.getRequestType())
                    .amount(String.valueOf(response.getAmount()))
                    .paymentStatus(response.getPaymentStatus())
                    .responseCode(response.getResponseCode())
                    .responseDesc(response.getResponseDescription())
                    .resultCode(response.getResultCode())
                    .resultDesc(response.getResultDescription())
                    .build());
            if (response.getResultCode() == 0) {
              PaymentHistory paymentHistory = paymentService.selectPHByCheckoutRequestId(
                  PaymentHistory.builder().checkoutRequestId(response.getCheckoutRequestId())
                      .build());
              updateBalance(paymentHistory);
            }
          }
      }

    });
  }

  public void updateBalance(PaymentHistory paymentHistory) {
    if (paymentHistory != null) {
      if (paymentHistory.isRenewal()) {
        policyService.updatePolicyRenewalBalance(paymentHistory.getPolicyNumber(),
                paymentHistory.getEffectiveDate(),
                new BigDecimal(paymentHistory.getAmount()));

      } else {
        quoteService.updateQuoteBalance(
            QuoteBalanceUpdateRequest.builder().quoteId(paymentHistory.getQuoteNumber())
                .customerId(paymentHistory.getCustomerId())
                .paidAmount(new BigDecimal(paymentHistory.getAmount()))
                .build());
      }
    }
  }


}
