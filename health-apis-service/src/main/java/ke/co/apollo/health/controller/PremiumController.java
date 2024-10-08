package ke.co.apollo.health.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import javax.validation.Valid;
import ke.co.apollo.health.common.domain.model.remote.PolicyIdRequest;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.domain.PolicyBenefitPremium;
import ke.co.apollo.health.domain.entity.PolicyPremiumEntity;
import ke.co.apollo.health.domain.request.QuoteBaseRequest;
import ke.co.apollo.health.service.PremiumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health/api")
@Api(tags = "Health App Premium API")
public class PremiumController {

  @Autowired
  private PremiumService premiumService;

  @PostMapping("/benefit/quote/premium")
  @ApiOperation("Get Quote Benefit Premium")
  public ResponseEntity<DataWrapper> getQuotePremium(
      @ApiParam(name = "request", value = "Get Quote Benefit Premium Request Payload", required = true)
      @Valid @RequestBody QuoteBaseRequest request) {
    List<PolicyPremiumEntity> response = premiumService.getQuotePremium(request);
    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/benefit/policy/premium")
  @ApiOperation("Get Policy Benefit Premium")
  public ResponseEntity<DataWrapper> getPolicyPremium(
      @ApiParam(name = "request", value = "Get Policy Benefit Premium Request Payload", required = true)
      @Valid @RequestBody PolicyIdRequest request) {
    List<PolicyPremiumEntity> response = premiumService.getPolicyPremium(request);
    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/quote/benefit/premium")
  @ApiOperation("Get Quote Benefit Premium")
  public ResponseEntity<DataWrapper> getQuoteBenefitPremium(
      @ApiParam(name = "request", value = "Get Quote Benefit Premium Request Payload", required = true)
      @Valid @RequestBody QuoteBaseRequest request) {
    List<PolicyBenefitPremium> response = premiumService.getQuoteBenefitPremium(request);
    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/policy/benefit/premium")
  @ApiOperation("Get Policy Benefit Premium")
  public ResponseEntity<DataWrapper> getPolicyBenefitPremium(
      @ApiParam(name = "request", value = "Get Policy Benefit Premium Request Payload", required = true)
      @Valid @RequestBody PolicyIdRequest request) {
    List<PolicyBenefitPremium> response = premiumService.getPolicyBenefitPremium(request);
    return ResponseEntity.ok(new DataWrapper(response));
  }
}
