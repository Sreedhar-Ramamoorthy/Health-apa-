package ke.co.apollo.health.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.domain.request.ClearDataRequest;
import ke.co.apollo.health.service.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health/api")
@Api(tags = "Health App Common API")
@Validated
@EnableAsync
public class HealthController {

  @Autowired
  private HealthService healthService;

  @GetMapping("/home")
  public String home() {
    return "hello health";
  }

  @PostMapping("/clear")
  @ApiOperation("Delete client's quote/dependant/question data")
  public ResponseEntity<DataWrapper> deleteCustomer(
      @ApiParam(name = "client", value = "Clear data Request Payload", required = true)
      @Valid @RequestBody ClearDataRequest customer) {
    boolean result = healthService.clearClientData(customer);
    Map<String, Boolean> response = new HashMap<>();
    response.put("result", result);
    return ResponseEntity.ok(new DataWrapper(response));
  }

}
