package ke.co.apollo.health.service;

import ke.co.apollo.health.common.domain.model.TransactionDetailTask;
import ke.co.apollo.health.domain.request.TransactionDetailTaskAddRequest;

public interface TransactionDetailService {

  boolean createTransactionDetailTask(TransactionDetailTaskAddRequest task);

  boolean updateTransactionDetailTask(TransactionDetailTask task);

  boolean cancelTransactionDetailTask(TransactionDetailTask task);

  void processTransactionDetailTask();

}
