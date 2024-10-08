package ke.co.apollo.health.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.request.CustomerAddPhoneRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerAddSuperIdRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerCreateRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerSuperIdRequest;
import ke.co.apollo.health.common.domain.model.request.GetCustomerByEntityIdRequest;
import ke.co.apollo.health.common.domain.model.request.GetCustomerByPhoneNoRequest;
import ke.co.apollo.health.common.domain.model.response.CustomerAddResponse;
import ke.co.apollo.health.common.domain.model.response.CustomerCreateResponse;
import ke.co.apollo.health.common.domain.model.response.GetCustomerInfoResponse;
import ke.co.apollo.health.common.domain.model.response.ResultResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.domain.request.CustomerAddRequest;
import ke.co.apollo.health.domain.request.CustomerIdRequest;
import ke.co.apollo.health.domain.request.CustomerListRequest;
import ke.co.apollo.health.domain.request.CustomerSearchRequest;
import ke.co.apollo.health.domain.request.CustomerUpdateRequest;
import ke.co.apollo.health.domain.request.DependantAddRequest;
import ke.co.apollo.health.domain.request.DependantDeleteRequest;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import ke.co.apollo.health.service.CustomerService;

/**
 * Customer controller
 *
 * @author Rick
 * @version 1.0
 * @see
 * @since 9/14/2020
 */
@RestController
@RequestMapping("/health/api")
@Api(tags = "Health App Customer API")
public class CustomerController {

  @Autowired
  private CustomerService customerService;

  @PostMapping("/customer/create")
  @ApiOperation("Add principal member")
  public ResponseEntity<DataWrapper> addCustomer(
      @ApiParam(name = "customer", value = "Add Customer Request Payload", required = true)
      @Valid @RequestBody CustomerAddRequest customer) {
      String customerId = customerService.addCustomer(customer);
      if (customerId == null) {
        return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
      }
      Map<String, String> result = new HashMap<>();
      result.put("customerId", customerId);
      return ResponseEntity.ok(new DataWrapper(result));
      }

  @PostMapping("/customer/addDependant")
  @ApiOperation("Add principal member related dependant")
  public ResponseEntity<DataWrapper> addDependant(
      @ApiParam(name = "dependant", value = "Add Customer's Dependant Request Payload", required = true)
      @Valid @RequestBody DependantAddRequest customer) {
    Integer success = customerService.addDependant(customer);
    if (success != 1) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    Map<String, String> result = new HashMap<>();
    result.put("customerId", customer.getCustomerId());
    return ResponseEntity.ok(new DataWrapper(result));
  }

  @PostMapping("/customer/detail")
  @ApiOperation("Get principal member and dependant")
  public ResponseEntity<DataWrapper> getCustomer(
      @ApiParam(name = "dependant", value = "Add Customer's Dependant Request Payload", required = true)
      @Valid @RequestBody CustomerSearchRequest customer) {
    CustomerDetailResponse result = customerService.getCustomer(customer);
    if (result == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(result));
  }

  @PostMapping("/customer/members")
  @ApiOperation("Get principal member and dependant")
  public ResponseEntity<DataWrapper> getCustomerAndDependants(
      @ApiParam(name = "dependant", value = "Add Customer's Dependant Request Payload", required = true)
      @Valid @RequestBody CustomerSearchRequest customer) {
    List<Customer> response = customerService
        .getCustomerAndDependants(customer.getCustomerId(), customer.getQuoteId());
    if (CollectionUtils.isEmpty(response)) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/customer/info")
  @ApiOperation("Get Customer Info")
  public ResponseEntity<DataWrapper> getCustomerInfo(
      @ApiParam(name = "dependant", value = "Get Customer Info Request Payload", required = true)
      @Valid @RequestBody CustomerIdRequest customer) {
    return ResponseEntity
        .ok(new DataWrapper(customerService.getCustomer(customer.getCustomerId())));
  }


  @PostMapping("/customer/updateDetail")
  @ApiOperation("Update principal member and add dependant")
  public ResponseEntity<DataWrapper> updateCustomer(
      @ApiParam(name = "dependant", value = "Update Customer and Dependant Request Payload", required = true)
      @Valid @RequestBody CustomerUpdateRequest customer) {
    CustomerDetailResponse result = customerService.updateCustomer(customer);
    Map<String, CustomerDetailResponse> response = new HashMap<>();
    response.put("result", result);
    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/customer/deleteDependant")
  @ApiOperation("Delete dependant")
  public ResponseEntity<DataWrapper> deleteCustomer(
      @ApiParam(name = "dependant", value = "Delete Dependant Request Payload", required = true)
      @Valid @RequestBody DependantDeleteRequest customer) {
    int result = customerService.deleteDependant(customer);
    Map<String, Integer> response = new HashMap<>();
    response.put("result", result);
    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/client/list")
  @ApiOperation("Get client list by phone number")
  public ResponseEntity<DataWrapper> getClientList(
      @Validated @RequestBody CustomerListRequest request) {

    List<Customer> list = customerService.getCustomerList(request);
    if (CollectionUtils.isEmpty(list)) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(list));
  }

  @PostMapping("/customer/quote/create")
  @ApiOperation("Create customer and quote by agent")
  public ResponseEntity<DataWrapper> createCustomerAndQuote(
      @Validated @RequestBody CustomerCreateRequest request) {

    CustomerCreateResponse response = customerService.createCustomerAndQuote(request);

    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/customer/quote/send")
  @ApiOperation("Add phone number for customer")
  public ResponseEntity<DataWrapper> addPhoneForCustomer(
      @Validated @RequestBody CustomerAddPhoneRequest request) {

    boolean result = customerService.addPhoneForCustomer(request);

    return ResponseEntity.ok(new DataWrapper(ResultResponse.builder().result(result).build()));
  }

  @PostMapping("/customer/updateSuperId")
  @ApiOperation("Add super customer Id for customer")
  public ResponseEntity<DataWrapper> addSuperCustomerIdForCustomer(
      @Validated @RequestBody CustomerAddSuperIdRequest request) {

    boolean result = customerService.addSuperCustomerIdForCustomer(request);

    return ResponseEntity.ok(new DataWrapper(ResultResponse.builder().result(result).build()));
  }

  @PostMapping("/customer/findBySuperId")
  @ApiOperation("get customer by findBySuperId")
  public ResponseEntity<DataWrapper> findBySuperId(
      @Validated @RequestBody CustomerSuperIdRequest request) {
    Customer result = customerService.findBySuperCustomerId(request);
    if (result != null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.OK, new CustomerAddResponse(result.getCustomerId())));
    }
    return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA, null));
  }

  @PostMapping("/customer/phoneNumber")
  @ApiOperation("Get customer by phone number")
  public ResponseEntity<DataWrapper> getCustomerByPhone(
      @Validated @RequestBody GetCustomerByPhoneNoRequest request) {

    GetCustomerInfoResponse result = customerService.getCustomerByPhoneNumber(request);
    if (result == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(result));
  }

  @PostMapping("/customer/entityId")
  @ApiOperation("Get customer by entity")
  public ResponseEntity<DataWrapper> getCustomerByEntityId(
      @Validated @RequestBody GetCustomerByEntityIdRequest request) {

    GetCustomerInfoResponse result = customerService.getCustomerByEntityId(request.getEntityId());
    if (result == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(result));
  }

}
