package ke.co.apollo.health.service;

import java.util.List;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.request.CustomerAddPhoneRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerAddSuperIdRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerCreateRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerSuperIdRequest;
import ke.co.apollo.health.common.domain.model.request.GetCustomerByPhoneNoRequest;
import ke.co.apollo.health.common.domain.model.response.CustomerCreateResponse;
import ke.co.apollo.health.common.domain.model.response.GetCustomerInfoResponse;
import ke.co.apollo.health.domain.request.CustomerAddRequest;
import ke.co.apollo.health.domain.request.CustomerListRequest;
import ke.co.apollo.health.domain.request.CustomerSearchRequest;
import ke.co.apollo.health.domain.request.CustomerUpdateRequest;
import ke.co.apollo.health.domain.request.DependantAddRequest;
import ke.co.apollo.health.domain.request.DependantDeleteRequest;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;

public interface CustomerService {

  String addCustomer(CustomerAddRequest request);

  Integer addDependant(DependantAddRequest request);

  CustomerDetailResponse getCustomer(CustomerSearchRequest request);

  CustomerDetailResponse updateCustomer(CustomerUpdateRequest request);

  int deleteDependant(DependantDeleteRequest request);

  Long addClientAndDependantToBase(String customerId, String quoteId);

  List<Customer> getCustomerAndDependants(String customerId, String quoteId);

  Customer getCustomer(String customerId);

  List<Customer> getCustomerByParentId(String customerId);

  List<Customer> getCustomerList(CustomerListRequest request);

  CustomerCreateResponse createCustomerAndQuote(CustomerCreateRequest request);

  boolean addPhoneForCustomer(CustomerAddPhoneRequest request);

  boolean addSuperCustomerIdForCustomer(CustomerAddSuperIdRequest request);

  GetCustomerInfoResponse getCustomerByPhoneNumber(GetCustomerByPhoneNoRequest request);

  GetCustomerInfoResponse getCustomerByEntityId(Long entityId);

  Customer findBySuperCustomerId(CustomerSuperIdRequest request);
}
