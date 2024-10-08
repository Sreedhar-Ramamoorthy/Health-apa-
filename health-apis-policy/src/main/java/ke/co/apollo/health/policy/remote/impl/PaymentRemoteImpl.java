package ke.co.apollo.health.policy.remote.impl;

import com.google.gson.Gson;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import ke.co.apollo.health.common.domain.model.remote.PaymentWalletRequest;
import ke.co.apollo.health.common.domain.model.remote.PaymentWalletResponse;
import ke.co.apollo.health.common.domain.model.remote.PaymentWalletResult;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsRequest;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsResponse;
import ke.co.apollo.health.common.domain.model.remote.TransactionDetailsResult;
import ke.co.apollo.health.common.utils.GsonUtils;
import ke.co.apollo.health.policy.remote.PaymentRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentRemoteImpl implements PaymentRemote {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${apa.payment-wallet-apis.service.base-url}")
  String paymentWalletApisServiceBaseUrl;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  Gson gson;

  @Override
  public PaymentWalletResponse paymentWallet(PaymentWalletRequest paymentWalletRequest) {
    PaymentWalletResponse paymentWalletResponse = null;
    try {
      String url =
          paymentWalletApisServiceBaseUrl + "/payment-wallet/mpesa-express/payment-request";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      // create a map for post parameters
      Map<String, Object> map = new HashMap<>();
      map.put("accountReference", paymentWalletRequest.getAccountReference());
      map.put("customerPhoneNumber", paymentWalletRequest.getCustomerPhoneNumber());
      map.put("description", paymentWalletRequest.getDescription());
      map.put("payableAmount", paymentWalletRequest.getPayableAmount());
      map.put("serviceType", paymentWalletRequest.getServiceType());
      if (paymentWalletRequest.getKraPin() != null) {
        map.put("kraPin", paymentWalletRequest.getKraPin());
      }
      String params = gson.toJson(map);
      logger.debug("url: {} payment wallet request: {}", url, params);
      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

      ResponseEntity<String> stringResponseEntity = restTemplate
          .postForEntity(url, entity, String.class);
      if (HttpStatus.OK.equals(stringResponseEntity.getStatusCode())) {
        String jsonString = stringResponseEntity.getBody();
        logger.debug("payment wallet response: {}", jsonString);
        PaymentWalletResult paymentWalletResult = GsonUtils.createGson()
            .fromJson(jsonString, PaymentWalletResult.class);
        logger.debug("paymentWalletResult: {}", paymentWalletResult);
        paymentWalletResponse = paymentWalletResult.getData();
      }
      logger.debug("StatusCode: {}", stringResponseEntity.getStatusCode());
    } catch (ResourceAccessException e) {
      logger.error("Resource Access Exception: {}", e.getMessage());
    } catch (HttpClientErrorException e) {
      logger.error("Client Error Exception: {}", e.getMessage());
    } catch (Exception e) {
      logger.error("Exception: {}", e.getMessage());
    }

    return paymentWalletResponse;
  }

  @Override
  public TransactionDetailsResponse transactionDetails(
      TransactionDetailsRequest transactionDetailsRequest) {

    TransactionDetailsResponse transactionDetailsResponse = null;
    try {
      String url =
          paymentWalletApisServiceBaseUrl
              + "/payment-wallet/mpesa-express/transaction-details?checkoutRequestId={checkoutRequestId}";

      // create a map for post parameters0
      Map<String, Object> map = new HashMap<>();
      map.put("checkoutRequestId", transactionDetailsRequest.getCheckoutRequestId());
      String params = gson.toJson(map);
      logger.debug("url: {} transaction detail request: {}", url, params);

      ResponseEntity<String> stringResponseEntity = restTemplate
          .getForEntity(url, String.class, map);
      if (HttpStatus.OK.equals(stringResponseEntity.getStatusCode())) {
        String jsonString = stringResponseEntity.getBody();
        logger.debug("transaction detail response: {}", jsonString);
        TransactionDetailsResult transactionDetailsResult = GsonUtils.createGson()
            .fromJson(jsonString, TransactionDetailsResult.class);
        logger.debug("transactionDetailsResult: {}", transactionDetailsResult);
        transactionDetailsResponse = transactionDetailsResult.getData();
      }
      logger.debug("StatusCode: {}", stringResponseEntity.getStatusCode());
    } catch (ResourceAccessException e) {
      logger.error("Resource Access Exception: {}", e.getMessage());
    } catch (HttpClientErrorException e) {
      logger.error("Client Error Exception: {}", e.getMessage());
    } catch (Exception e) {
      logger.error("Exception: {}", e.getMessage());
    }

    return transactionDetailsResponse;
  }


}
