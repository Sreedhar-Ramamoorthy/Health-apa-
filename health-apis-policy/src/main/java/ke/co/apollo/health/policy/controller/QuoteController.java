package ke.co.apollo.health.policy.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import ke.co.apollo.health.common.domain.model.request.QuoteBenefitRequest;
import ke.co.apollo.health.common.domain.model.response.QuoteBenefitResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.policy.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health/api")
@Api(tags = "Health Quote Integration API")
public class QuoteController {

  @Autowired
  private QuoteService quoteService;

  @PostMapping("/quote/benefit/travel")
  @ApiOperation("Get Quote Travel Benefit")
  public ResponseEntity<DataWrapper> getTravelBenefit(
      @ApiParam(name = "quote", value = "Get Quote Travel Benefit Payload", required = true)
      @Valid @RequestBody QuoteBenefitRequest request) {
    int travelBenefit = quoteService
        .getTravelBenefitLimit(request.getPolicyId(), request.getEffectiveDate());
    return ResponseEntity.ok(new DataWrapper(
        QuoteBenefitResponse.builder().travelBenefit(travelBenefit).build()));
  }

}
