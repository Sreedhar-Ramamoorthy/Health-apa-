package ke.co.apollo.health.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ke.co.apollo.health.common.domain.model.Lead;
import ke.co.apollo.health.common.domain.model.SearchCondition;
import ke.co.apollo.health.common.domain.model.response.ResultResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.domain.request.LeadAddRequest;
import ke.co.apollo.health.domain.request.LeadDeleteRequest;
import ke.co.apollo.health.domain.request.LeadSearchRequest;
import ke.co.apollo.health.domain.request.LeadUpdateRequest;
import ke.co.apollo.health.domain.response.LeadIdResponse;
import ke.co.apollo.health.domain.response.LeadListResponse;
import ke.co.apollo.health.mapping.LeadMapping;
import ke.co.apollo.health.service.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health/api")
@Api(tags = "Health App Lead API")
public class LeadController {

  @Autowired
  LeadService leadService;

  @PostMapping("/lead/add")
  @ApiOperation("Create a new lead")
  public ResponseEntity<DataWrapper> addLead(
      @ApiParam(name = "leadAddRequest", value = "Add Lead Requset Payload", required = true)
      @Validated @RequestBody LeadAddRequest leadAddRequest) {
    Lead lead = LeadMapping.leadAddRequest2Entity(leadAddRequest);
    String leadId = leadService.createLead(lead, leadAddRequest.getAgentId());
    if (leadId == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(LeadIdResponse.builder().leadId(leadId).build()));
  }

  @PostMapping("/lead/update")
  @ApiOperation("Update a lead")
  public ResponseEntity<DataWrapper> updateLead(
      @ApiParam(name = "leadUpdateRequest", value = "Update Lead Requset Payload", required = true)
      @Validated @RequestBody LeadUpdateRequest leadUpdateRequest) {
    Lead lead = LeadMapping.leadUpdateRequest2Entity(leadUpdateRequest);
    String leadId = leadService.updateLead(lead, leadUpdateRequest.getAgentId());
    if (leadId == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(LeadIdResponse.builder().leadId(leadId).build()));
  }

  @PostMapping("/lead/delete")
  @ApiOperation("Delete a new lead")
  public ResponseEntity<DataWrapper> deleteLead(
      @ApiParam(name = "leadDeleteRequest", value = "Delete Lead Requset Payload", required = true)
      @Validated @RequestBody LeadDeleteRequest leadDeleteRequest) {
    boolean success = leadService
        .deleteLead(leadDeleteRequest.getLeadId(), leadDeleteRequest.getAgentId());
    return ResponseEntity.ok(new DataWrapper(ResultResponse.builder().result(success).build()));
  }

  @PostMapping("/lead/list")
  @ApiOperation("Search leads by name and filter(interest product)")
  public ResponseEntity<DataWrapper> getLeadList(
      @ApiParam(name = "leadSearchRequest", value = "Search Lead Condition Payload", required = true)
      @Validated @RequestBody LeadSearchRequest leadSearchRequest) {
    SearchCondition searchCondition = LeadMapping
        .leadSearchRequest2SearchCondition(leadSearchRequest);
    LeadListResponse leadsRespone = leadService
        .getLeadListAndCount(searchCondition, leadSearchRequest.getAgentId());
    if (leadsRespone == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(leadsRespone));
  }

}
