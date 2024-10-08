package ke.co.apollo.health.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import ke.co.apollo.health.repository.HealthStepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.request.CustomerAddPhoneRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerAddSuperIdRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerCreateRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerSuperIdRequest;
import ke.co.apollo.health.common.domain.model.request.GetCustomerByEntityIdRequest;
import ke.co.apollo.health.common.domain.model.request.GetCustomerByPhoneNoRequest;
import ke.co.apollo.health.common.domain.model.response.CustomerCreateResponse;
import ke.co.apollo.health.common.domain.model.response.GetCustomerInfoResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.domain.request.CustomerAddRequest;
import ke.co.apollo.health.domain.request.CustomerIdRequest;
import ke.co.apollo.health.domain.request.CustomerListRequest;
import ke.co.apollo.health.domain.request.CustomerSearchRequest;
import ke.co.apollo.health.domain.request.CustomerUpdateRequest;
import ke.co.apollo.health.domain.request.DependantAddRequest;
import ke.co.apollo.health.domain.request.DependantDeleteRequest;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import ke.co.apollo.health.service.CustomerService;



class CustomerControllerTest {

    @InjectMocks
    CustomerController customerController;

    @Mock
    CustomerService customerService;

    @Mock
    HealthStepRepository stepRepository;

    @BeforeEach
    void setUpMocks(){
        initMocks(this);
        }


    @Test
    void addCustomerTest() {
        when(customerService.addCustomer(any())).thenReturn("Added");
        ResponseEntity<DataWrapper> wrapper = customerController.addCustomer(CustomerAddRequest.builder().build());
        assertNotNull(wrapper);
        }
    
    @Test
    void addCustomerNullCustomerTest() {
        when(customerService.addCustomer(any())).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = customerController.addCustomer(CustomerAddRequest.builder().build());
        assertNotNull(wrapper);
        }
  
    @Test
    void addDependantTest() {
        when(customerService.addDependant(any())).thenReturn(1);
        ResponseEntity<DataWrapper> wrapper = customerController.addDependant(DependantAddRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void addDependantNotSuccessfulTest() {
        when(customerService.addDependant(any())).thenReturn(0);
        ResponseEntity<DataWrapper> wrapper = customerController.addDependant(DependantAddRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getCustomerTest() {
        when(customerService.getCustomer(any(CustomerSearchRequest.class))).thenReturn(CustomerDetailResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = customerController.getCustomer(CustomerSearchRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getCustomerFailTest() {
        when(customerService.getCustomer(any(CustomerSearchRequest.class))).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = customerController.getCustomer(CustomerSearchRequest.builder().build());
        assertNotNull(wrapper);
        }   

    @Test
    void getCustomerAndDependantsTest() {
        when(customerService.getCustomerAndDependants(anyString(), anyString())).thenReturn(Collections.singletonList(Customer.builder().build()));
        ResponseEntity<DataWrapper> wrapper = customerController.getCustomerAndDependants(CustomerSearchRequest.builder().build());
        assertNotNull(wrapper);
        }

    // @Test
    // void getCustomerAndDependantsEmptyTest() {
    //     List<Customer> customers = new ArrayList<Customer>();
    //     Customer cust = new Customer();
    //     cust.setAgentId("101");
    //     customers.add(cust);

    //     when(customerService.getCustomerAndDependants(anyString(), anyString())).thenReturn(customers);
    //     ResponseEntity<DataWrapper> wrapper = customerController.getCustomerAndDependants(CustomerSearchRequest.builder().build());
    //     assertNotNull(wrapper);
    //     }

    @Test
    void getCustomerInfoTest() {
        when(customerService.getCustomer(anyString())).thenReturn(Customer.builder().build());
        ResponseEntity<DataWrapper> wrapper = customerController.getCustomerInfo(CustomerIdRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void updateCustomerTest() {
        when(customerService.updateCustomer(any())).thenReturn(CustomerDetailResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = customerController.updateCustomer(CustomerUpdateRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void deleteCustomerTest() {
        when(customerService.deleteDependant(any())).thenReturn(1);
        ResponseEntity<DataWrapper> wrapper = customerController.deleteCustomer(DependantDeleteRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getClientListTest() {
        when(customerService.getCustomerList(any())).thenReturn(Collections.singletonList(Customer.builder().build()));
        ResponseEntity<DataWrapper> wrapper = customerController.getClientList(CustomerListRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getClientListEmptyTest() {
        when(customerService.getCustomerList(any())).thenReturn(Collections.emptyList());
        ResponseEntity<DataWrapper> wrapper = customerController.getClientList(CustomerListRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void createCustomerAndQuoteTest() {
        when(customerService.createCustomerAndQuote(any())).thenReturn(CustomerCreateResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = customerController.createCustomerAndQuote(CustomerCreateRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void addPhoneForCustomerTest() {
        when(customerService.addPhoneForCustomer(any())).thenReturn(true);
        when(stepRepository.save(any())).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = customerController.addPhoneForCustomer(CustomerAddPhoneRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void addSuperCustomerIdForCustomerTest() {
        when(customerService.addSuperCustomerIdForCustomer(any())).thenReturn(true);
        ResponseEntity<DataWrapper> wrapper = customerController.addSuperCustomerIdForCustomer(CustomerAddSuperIdRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void findBySuperIdTest() {
        when(customerService.findBySuperCustomerId(any())).thenReturn(Customer.builder().build());
        ResponseEntity<DataWrapper> wrapper = customerController.findBySuperId(CustomerSuperIdRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void findBySuperIdNullTest() {
        when(customerService.findBySuperCustomerId(any())).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = customerController.findBySuperId(CustomerSuperIdRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getCustomerByPhoneTest() {
        when(customerService.getCustomerByPhoneNumber(any())).thenReturn(GetCustomerInfoResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = customerController.getCustomerByPhone(GetCustomerByPhoneNoRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getCustomerByPhoneNullTest() {
        when(customerService.getCustomerByPhoneNumber(any())).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = customerController.getCustomerByPhone(GetCustomerByPhoneNoRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getCustomerByEntityIdTest() {
        when(customerService.getCustomerByEntityId(anyLong())).thenReturn(GetCustomerInfoResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = customerController.getCustomerByEntityId(GetCustomerByEntityIdRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getCustomerByEntityIdReturnNullTest() {
        when(customerService.getCustomerByEntityId(anyLong())).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = customerController.getCustomerByEntityId(GetCustomerByEntityIdRequest.builder().build());
        assertNotNull(wrapper);
        }

}
