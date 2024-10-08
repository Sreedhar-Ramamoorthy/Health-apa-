package ke.co.apollo.health.policy.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import ke.co.apollo.health.common.domain.model.DependantDetail;
import ke.co.apollo.health.common.domain.model.EntityDetails;
import ke.co.apollo.health.common.domain.model.Principal;
import ke.co.apollo.health.common.domain.model.remote.PolicyIdRequest;
import ke.co.apollo.health.common.domain.model.request.AddClientEntityRequest;
import ke.co.apollo.health.common.domain.model.request.AddContactDetailsRequest;
import ke.co.apollo.health.common.domain.model.request.GetCustomerByPhoneNoRequest;
import ke.co.apollo.health.common.domain.model.request.GetCustomerByPolicyNoRequest;
import ke.co.apollo.health.common.domain.model.request.GetEntityDetailsRequest;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.policy.remote.EntityMaintenanceRemote;
import ke.co.apollo.health.policy.service.EntityMaintenanceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rick
 * @version 1.0
 * @see
 * @since 9/3/2020
 */
@RestController
@RequestMapping("/entityMaintenance")
@Api(tags = "Health APA EntityMaintenance Integration API")
public class EntityMaintenanceController {

  @Autowired
  EntityMaintenanceRemote entityMaintenanceRemote;

  @Autowired
  EntityMaintenanceService entityService;

  @PostMapping("/client")
  @ApiOperation("Add Client Entity")
  public ResponseEntity<DataWrapper> addClient(
      @Validated @RequestBody AddClientEntityRequest request) {
    return ResponseEntity.ok(new DataWrapper(entityMaintenanceRemote.addClientEntity(request)));
  }

  @PostMapping("/contact")
  @ApiOperation("Add Contact Detail")
  public ResponseEntity<DataWrapper> addContactDetails(
      @Validated @RequestBody AddContactDetailsRequest request) {
    return ResponseEntity.ok(new DataWrapper(entityMaintenanceRemote.addContactDetails(request)));
  }

  @PostMapping("/client/dependant")
  @ApiOperation("Add dependant for existing Client")
  public ResponseEntity<DataWrapper> addDependant(
      @Validated @RequestBody List<AddClientEntityRequest> request) {
    for (AddClientEntityRequest dependant : request) {
      boolean validate = true;
      if (dependant.getParentId() == null || dependant.getRelationshipDescription() == null) {
        validate = false;
      }
      if (!validate) {
        ResponseEntity.ok(new DataWrapper(ReturnCode.INVALID_PARAMETER));
      }
    }
    return ResponseEntity.ok(new DataWrapper(entityMaintenanceRemote.addDependant(request)));
  }

  @PostMapping("/entity/phoneNumber")
  @ApiOperation("Get client by phone number")
  public ResponseEntity<DataWrapper> getClientByPhoneNumber(
      @Validated @RequestBody GetCustomerByPhoneNoRequest request) {

    return ResponseEntity.ok(new DataWrapper(entityService.getEntityByPhoneNumber(request)));
  }

  @PostMapping("/entity/policyNumber")
  @ApiOperation("Get client by policy number")
  public ResponseEntity<DataWrapper> getClientByPolicyumber(
      @Validated @RequestBody GetCustomerByPolicyNoRequest request) {

    return ResponseEntity.ok(new DataWrapper(entityService.getEntityByPolicyNumber(request)));
  }

  @PostMapping("/entity/dependant")
  @ApiOperation("Get dependant list by policyId and effective date")
  public ResponseEntity<DataWrapper> getDependants(
      @Validated @RequestBody PolicyIdRequest request) {

    List<DependantDetail> result = entityService
        .getDependantsByPolicyIdAndEffectiveDate(request.getPolicyId(), request.getEffectiveDate());
    if (CollectionUtils.isEmpty(result)) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(result));
  }

  @PostMapping("/entity/detail")
  @ApiOperation("Get entity details by id")
  public ResponseEntity<DataWrapper> getEntityDetails(
      @Validated @RequestBody GetEntityDetailsRequest request) {

    EntityDetails result = entityMaintenanceRemote.getEntityDetailsById(request);
    if (result == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(result));
  }

  @PostMapping("/entity/principal/detail")
  @ApiOperation("Get entity details by id and plus customer info")
  public ResponseEntity<DataWrapper> getEntityCustomerDetails(
      @Validated @RequestBody GetEntityDetailsRequest request) {

    Principal result = entityService.getPrincipleByEntityId(request.getEntityId());
    if (result == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(result));
  }


}
