package ke.co.apollo.health.remote;

import ke.co.apollo.health.common.domain.model.remote.PaymentWalletResponse;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsRequest;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsResponse;
import ke.co.apollo.health.common.domain.model.request.CreditCardTransactionRequest;
import ke.co.apollo.health.common.domain.model.request.PaymentRequest;
import ke.co.apollo.health.common.domain.model.request.TransactionCreateRequest;
import ke.co.apollo.health.common.domain.model.request.TransactionValidateRequest;
import ke.co.apollo.health.common.domain.model.response.TransactionCreateResponse;
import ke.co.apollo.health.common.domain.model.response.TransactionValidateResponse;
import ke.co.apollo.health.domain.request.MpesaExpressRequest;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public interface PaymentRemote {

  PaymentWalletResponse paymentWalletRequest(MpesaExpressRequest paymentRequest);

  TransactionDetailsResponse transactionDetails(TransactionDetailsRequest paymentWalletRequest);

  TransactionCreateResponse createTransaction(TransactionCreateRequest request);

  TransactionValidateResponse validateTransaction(TransactionValidateRequest request);

  HashMap<String, String> createCreditCardPaymentRequest(CreditCardTransactionRequest request);

}
