package ke.co.apollo.health.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ke.co.apollo.health.common.domain.model.ApplicationRenewalPolicy;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.ApplicationRenewalPolicyListResponse;
import ke.co.apollo.health.common.domain.model.response.PolicyOverComingResponseDto;
import ke.co.apollo.health.common.domain.model.response.PolicyRenewalResponse;
import ke.co.apollo.health.common.domain.model.response.ResultResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.domain.request.ApplicationPolicyListSearchRequest;
import ke.co.apollo.health.domain.request.CustomerIdRequest;
import ke.co.apollo.health.domain.request.PolicyComplaintRequest;
import ke.co.apollo.health.domain.response.HealthPolicyListResponse;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.service.ComplaintService;
import ke.co.apollo.health.service.PolicyService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/health/api")
@Api(tags = "Health App Policy API")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private PolicyRemote policyRemote;

    @Autowired
    private ComplaintService complaintService;

    @PostMapping("/policy/list")
    @ApiOperation("Policy list")
    public ResponseEntity<DataWrapper> getCustomerPolicyList(
            @ApiParam(name = "CustomerPolicyListRequest", value = "Get Policy List Request Payload", required = true)
            @Valid @RequestBody CustomerPolicyListRequest request) {
        List<HealthPolicyListResponse> policyList = policyService.getCustomerPolicyList(request);
        if (CollectionUtils.isEmpty(policyList)) {
            return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
        }

        return ResponseEntity.ok(new DataWrapper(policyList));
    }

    @PostMapping("/customer/policy/list")
    @ApiOperation("Policy list")
    public ResponseEntity<DataWrapper> getCustomerPolicyList(
            @ApiParam(name = "EntityPolicyListRequest", value = "Customer Policy Entity Request Payload", required = true)
            @Valid @RequestBody EntityPolicyListRequest request) {
        List<HealthPolicyListResponse> policyList = policyService.getCustomerPolicyList(request);
        if (CollectionUtils.isEmpty(policyList)) {
            return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
        }

        return ResponseEntity.ok(new DataWrapper(policyList));
    }

    @PostMapping("/customer/policy/cache")
    @ApiOperation("Create Customer Policy Cache")
    public ResponseEntity<DataWrapper> createCustomerPolicyCache(
            @ApiParam(name = "EntityPolicyListRequest", value = "Customer Policy Entity Request Payload", required = true)
            @Valid @RequestBody CustomerIdRequest request) {
        boolean result = policyService.createCustomerPolicyCache(request);
        return ResponseEntity.ok(new DataWrapper(ResultResponse.builder().result(result).build()));
    }

    @PostMapping("/customer/policy/renewal")
    @ApiOperation("Customer Policy Renewal Premium Calculation")
    public ResponseEntity<DataWrapper> renewalPolicy(
            @ApiParam(name = "PolicyRenewalRequest", value = "Customer Policy Renewal Request Payload", required = true)
            @Valid @RequestBody PolicyRenewalRequest request) {
        PolicyRenewalResponse result = policyService.renewalPolicy(request);
        return ResponseEntity.ok(new DataWrapper(result));
    }

    @PostMapping("/customer/policy/balance")
    @ApiOperation("Update Customer Policy Renewal Balance")
    public ResponseEntity<DataWrapper> updatePolicyRenewalBalance(
            @ApiParam(name = "PolicyRenewalBalanceRequest", value = "Customer Policy Renewal Balance Request Payload", required = true)
            @Valid @RequestBody PolicyRenewalBalanceRequest request) {
        boolean result = policyService
                .updatePolicyRenewalBalance(request.getPolicyNumber(), request.getEffectiveDate(),
                        request.getPaidAmount());
        return ResponseEntity.ok(new DataWrapper(result));
    }

    @PostMapping("/application/policy/list")
    @ApiOperation("Search Application Renewal Policy list")
    public ResponseEntity<DataWrapper> searchApplicationRenewalPolicyList(
            @ApiParam(name = "request", value = "Search Renewal Policy List Request Payload", required = true)
            @Valid @RequestBody ApplicationPolicyListSearchRequest request) {
        ApplicationRenewalPolicyListResponse response = policyService
                .searchApplicationRenewalPolicyList(request);
        if (response == null) {
            return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
        }

        return ResponseEntity.ok(new DataWrapper(response));
    }

    @PostMapping("/application/policy/detail")
    @ApiOperation("Get Application Renewal Policy")
    public ResponseEntity<DataWrapper> getApplicationRenewalPolicy(
            @ApiParam(name = "request", value = "Get Renewal Policy Request Payload", required = true)
            @Valid @RequestBody PolicyNumberRequest request) {
        ApplicationRenewalPolicy response = policyService.getApplicationRenewalPolicy(request);
        return ResponseEntity.ok(new DataWrapper(response));
    }

    @PostMapping("/application/policy/archive")
    @ApiOperation("Archive Application Renewal Policy")
    public ResponseEntity<DataWrapper> archiveApplicationRenewalPolicy(
            @ApiParam(name = "request", value = "Archive Renewal Policy List Request Payload", required = true)
            @Valid @RequestBody PolicyNumberRequest request) {
        boolean response = policyService.archiveApplicationRenewalPolicy(request);
        return ResponseEntity.ok(new DataWrapper(ResultResponse.builder().result(response).build()));
    }

    @PostMapping("/policy/complaint/submit")
    @ApiOperation("Submit Policy Complaint")
    public ResponseEntity<DataWrapper> submitComplaint(
            @ApiParam(name = "request", value = "Submit Policy Complaint Request Payload", required = true)
            @Valid @RequestBody PolicyComplaintRequest request) {
        boolean response = complaintService.submitComplaint(request);
        return ResponseEntity.ok(new DataWrapper(ResultResponse.builder().result(response).build()));
    }

    @PostMapping("/policy/coming/Policylist")
    @ApiOperation("coming Policylist")
    public ResponseEntity<DataWrapper> comingPolicyList(
            @ApiParam(name = "request", value = "coming/Policylist", required = true)
            @Valid @RequestBody ComingPolicyListRequest request) {
        PolicyOverComingResponseDto comingResponseDto = policyService.policyUpdateDetails(request);
        return ResponseEntity.ok(new DataWrapper(comingResponseDto));
    }

    @PostMapping("/policy/coming/PolicyListInExcel")
    @ApiOperation("coming PolicylistInExcel")
    public ResponseEntity<DataWrapper> comingPolicyListInExcel(
            @ApiParam(name = "request", value = "coming/PolicylistInExcel", required = true)
            @Valid @RequestBody ComingPolicyListRequestInExcel request) {
        byte[] comingPolicyListInExcel = policyService.comingPolicyListInExcel(request);
        return ResponseEntity.ok(new DataWrapper(comingPolicyListInExcel));
    }
}
