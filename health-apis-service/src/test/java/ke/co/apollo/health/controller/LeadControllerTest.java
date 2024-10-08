package ke.co.apollo.health.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;

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
import ke.co.apollo.health.mapping.LeadMapping;
import ke.co.apollo.health.service.LeadService;
import ke.co.apollo.health.domain.request.LeadAddRequest;
import ke.co.apollo.health.domain.request.LeadDeleteRequest;
import ke.co.apollo.health.domain.request.LeadSearchRequest;
import ke.co.apollo.health.domain.request.LeadUpdateRequest;
import ke.co.apollo.health.domain.request.LeadDeleteRequest;
import ke.co.apollo.health.domain.response.LeadListResponse;

class LeadControllerTest {

    @InjectMocks
    LeadController leadController;

    @Mock
    LeadService leadService;

    @BeforeEach
    void setUpMocks(){
        initMocks(this);
        }


    @Test
    void addLeadTest() {
        when(leadService.createLead(any(),any())).thenReturn("101");
        ResponseEntity<DataWrapper> wrapper = leadController.addLead(LeadAddRequest.builder().build());
        assertNotNull(wrapper);
        }
    
    @Test
    void addLeadReturnNullTest() {
        when(leadService.createLead(any(),any())).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = leadController.addLead(LeadAddRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void updateLeadTest() {
        when(leadService.updateLead(any(),any())).thenReturn("101");
        ResponseEntity<DataWrapper> wrapper = leadController.updateLead(LeadUpdateRequest.builder().build());
        assertNotNull(wrapper);
        }
    
    @Test
    void updateLeadReturnNullTest() {
        when(leadService.updateLead(any(),any())).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = leadController.updateLead(LeadUpdateRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void deleteLeadTest() {
        when(leadService.deleteLead(any(),any())).thenReturn(true);
        ResponseEntity<DataWrapper> wrapper = leadController.deleteLead(LeadDeleteRequest.builder().build());
        assertNotNull(wrapper);
        }


    @Test
    void getLeadListTest() {
        LeadListResponse list = LeadListResponse.builder()
            .total(10)
            .leads(Collections.emptyList())
            .build();
        
        when(leadService.getLeadListAndCount(any(),any())).thenReturn(list);
        ResponseEntity<DataWrapper> wrapper = leadController.getLeadList(LeadSearchRequest.builder().build());
        assertNotNull(wrapper);
        }


    @Test
    void getLeadListReturnNullTest() {
        when(leadService.getLeadListAndCount(any(),any())).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = leadController.getLeadList(LeadSearchRequest.builder().build());
        assertNotNull(wrapper);
        }
}
