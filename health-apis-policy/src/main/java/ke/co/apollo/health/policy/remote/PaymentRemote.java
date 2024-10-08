package ke.co.apollo.health.policy.remote;

import ke.co.apollo.health.common.domain.model.remote.PaymentWalletRequest;
import ke.co.apollo.health.common.domain.model.remote.PaymentWalletResponse;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsRequest;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsResponse;

public interface PaymentRemote {

  PaymentWalletResponse paymentWallet(PaymentWalletRequest paymentWalletRequest);

  TransactionDetailsResponse transactionDetails(TransactionDetailsRequest paymentWalletRequest);

}
