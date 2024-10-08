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
import ke.co.apollo.health.common.domain.model.Question;
import ke.co.apollo.health.common.domain.model.QuoteQuestion;
import ke.co.apollo.health.service.QuestionService;
import ke.co.apollo.health.domain.request.QuestionListRequest;
import ke.co.apollo.health.domain.request.QuoteBaseRequest;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;

class QuestionControllerTest {

    @InjectMocks
    QuestionController questionController;

    @Mock
    QuestionService questionService;

    @BeforeEach
    void setUpMocks(){
        initMocks(this);
        }

    @Test
    void submitQuestionTest() {
        when(questionService.submitQuestion(any())).thenReturn(true);
        ResponseEntity<DataWrapper> wrapper = questionController.submitQuestion(Question.builder().build());
        assertNotNull(wrapper);
        }
    
    @Test
    void getQuoteListTest() {
        when(questionService.getQuestion(any())).thenReturn(Question.builder().build());
        ResponseEntity<DataWrapper> wrapper = questionController.getQuoteList(QuestionListRequest.builder().build());
        assertNotNull(wrapper);
        }

     @Test
    void getQuoteListReturnNullTest() {
        when(questionService.getQuestion(any())).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = questionController.getQuoteList(QuestionListRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getQuoteQuestionTest() {
        when(questionService.getQuoteQuestion(any())).thenReturn(QuoteQuestion.builder().build());
        ResponseEntity<DataWrapper> wrapper = questionController.getQuoteQuestion(QuoteBaseRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getQuoteQuestionReturnNullTest() {
        when(questionService.getQuoteQuestion(any())).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = questionController.getQuoteQuestion(QuoteBaseRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void getPolicyQuestionTest() {
        when(questionService.getPolicyQuestion(any())).thenReturn(QuoteQuestion.builder().build());
        ResponseEntity<DataWrapper> wrapper = questionController.getPolicyQuestion(PolicyNumberRequest.builder().build());
        assertNotNull(wrapper);
        }


    @Test
    void getPolicyQuestionReturnNullTest() {
        when(questionService.getPolicyQuestion(any())).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = questionController.getPolicyQuestion(PolicyNumberRequest.builder().build());
        assertNotNull(wrapper);
        }

}
