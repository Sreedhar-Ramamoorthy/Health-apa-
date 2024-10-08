package ke.co.apollo.health.policy.remote.impl;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.request.GetBenefitBreakDownRequest;
import ke.co.apollo.health.common.domain.model.response.GetBenefitBreakDownResponse;
import ke.co.apollo.health.policy.remote.ClaimsRemote;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * integrate Activus.Services.Claims
 *
 * @author Rick
 * @version 1.0
 * @see
 * @since 11/18/2020
 */
@Service
public class ClaimsRemoteImpl implements ClaimsRemote {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${apa.health-apis.service.url}")
  String healthApisBaseUrl;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  Gson gson;

  @Override
  public GetBenefitBreakDownResponse getBenefit(GetBenefitBreakDownRequest request) {
    GetBenefitBreakDownResponse response = null;
    try {
      String url = healthApisBaseUrl
          + "/ASClaimsService/api/claims/BenefitBreakdown/GetBreakdownBenefits/{policyId}/{targetDate}/{beneficiaryId}";

      // create a map for get parameters
      Map<String, Object> map = new HashMap<>();
      map.put("policyId", request.getPolicyId());
      map.put("targetDate", DateFormatUtils
          .format(request.getTargetDate(), GlobalConstant.YYYYMMDD));
      map.put("beneficiaryId", request.getBeneficiaryId());
      String params = gson.toJson(map);
      logger.debug("url: {} get getBenefit detail request: {}", url, params);

      ResponseEntity<GetBenefitBreakDownResponse> getBenefitBreakDownResponse = restTemplate
          .getForEntity(url, GetBenefitBreakDownResponse.class, map);

      if (HttpStatus.OK == getBenefitBreakDownResponse.getStatusCode()) {
        GetBenefitBreakDownResponse data = getBenefitBreakDownResponse.getBody();
        logger.debug("get getBenefit data: {}", data);
        if (data != null) {
          if (data.isSuccess()) {
            response = data;
          } else {
            logger.error("GetBenefit occurred error , errorMessage:{} ,errors:{}",
                data.getErrorMessage(), data.getErrors());
          }
        }
      }
      logger.debug("StatusCode: {}", getBenefitBreakDownResponse.getStatusCode());
    } catch (ResourceAccessException e) {
      logger.error("Resource Access Exception: {}", e.getMessage());
    } catch (HttpClientErrorException e) {
      logger.error("Client Error Exception: {}", e.getMessage());
    } catch (Exception e) {
      logger.error("Exception: {}", e.getMessage());
    }
    return response;
  }
}
