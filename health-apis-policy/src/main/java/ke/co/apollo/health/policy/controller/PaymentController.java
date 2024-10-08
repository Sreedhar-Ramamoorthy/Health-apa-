package ke.co.apollo.health.policy.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import ke.co.apollo.health.common.domain.model.remote.PaymentWalletRequest;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsRequest;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.policy.remote.PaymentRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@Api(tags = "Health Payment Integration API")
public class PaymentController {

  @Autowired
  PaymentRemote paymentRemote;

  @PostMapping("/wallet/request")
  @ApiOperation("Pay premium for buying policy")
  public ResponseEntity<DataWrapper> paymentWalletRequest(
      @ApiParam(name = "PaymentRequest", value = "Payment Wallet Request Payload", required = true)
      @Valid @RequestBody PaymentWalletRequest paymentWalletRequest) {
    return ResponseEntity.ok(new DataWrapper(paymentRemote.paymentWallet(paymentWalletRequest)));
  }

  @PostMapping("/wallet/detail")
  @ApiOperation("Get transaction details")
  public ResponseEntity<DataWrapper> transactionDetailRequest(
      @ApiParam(name = "TransactionDetailsRequest", value = "Transaction Detail Request Payload", required = true)
      @Valid @RequestBody TransactionDetailsRequest transactionDetailsRequest) {
    return ResponseEntity
        .ok(new DataWrapper(paymentRemote.transactionDetails(transactionDetailsRequest)));
  }

}
