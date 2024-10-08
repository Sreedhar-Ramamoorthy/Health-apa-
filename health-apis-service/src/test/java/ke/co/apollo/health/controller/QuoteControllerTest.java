package ke.co.apollo.health.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import freemarker.template.TemplateException;
import ke.co.apollo.health.common.CommonQuote;
import ke.co.apollo.health.domain.request.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ke.co.apollo.health.common.domain.model.ApplicationQuote;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.common.domain.model.response.ApplicationQuoteListResponse;
import ke.co.apollo.health.common.domain.model.response.HealthQuoteListResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.domain.response.IdAndHideList;
import ke.co.apollo.health.domain.response.QuoteStepResponse;
import ke.co.apollo.health.service.QuoteService;

import javax.xml.crypto.Data;

class QuoteControllerTest {

    @InjectMocks
    QuoteController quoteController;

    @Mock
    QuoteService quoteService;

    @BeforeEach
    void setUpMocks(){
        initMocks(this);
    }

    @Test
    void testQuoteStepController(){
        when(quoteService.getQuoteStep(any())).thenReturn(QuoteStepResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = quoteController.getQuoteStep(QuoteStepRequest.builder().build());
        assertNotNull(wrapper);
        }

    @Test
    void testUpdateQuoteBenefit(){
        Quote quote = Quote.builder()
                .agentId("agentId")
                .build();
        when(quoteService.updateQuoteBenefit(any())).thenReturn(quote) ;
        QuoteBenefitUpdateRequest updateQuoteBenefit = QuoteBenefitUpdateRequest.builder()
                .agentId("agentId")
                .customerId("customerId")
                .premium(BigDecimal.ZERO)
                .build();
        ResponseEntity<DataWrapper> wrapper = quoteController.updateQuoteBenefit(updateQuoteBenefit);
        assertNotNull(wrapper);
        }

        @Test
        void testUpdateQuoteStartDate(){
            Quote quote = Quote.builder()
                .agentId("agentId")
                .build();
            when(quoteService.updateQuoteStartDate(any())).thenReturn(quote) ;
            QuoteStartDateUpdateRequest updateQuote = QuoteStartDateUpdateRequest.builder()
                    .agentId("agentId")
                    .build();
            ResponseEntity<DataWrapper> wrapper = quoteController.updateQuoteStartDate(updateQuote);
            assertNotNull(wrapper);
            }

        @Test
        void testUpdateQuote(){
            Quote quote = Quote.builder()
                .agentId("agentId")
                .build();
            when(quoteService.finishQuote(any())).thenReturn(quote) ;
            QuoteFinishRequest updateQuote = QuoteFinishRequest.builder()
                    .agentId("agentId")
                    .build();
            ResponseEntity<DataWrapper> wrapper = quoteController.updateQuote(updateQuote);
            assertNotNull(wrapper);
            }

        @Test
        void testAddIntermediaryToPolicy(){
            QuoteBaseRequest req = QuoteBaseRequest.builder()
                        .agentId("agentId")
                        .build();

            when(quoteService.addIntermediaryToPolicy(anyString(),anyString(), anyString())).thenReturn(true) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.addIntermediaryToPolicy(req);
            assertNotNull(wrapper);
        }


        @Test
        void testDeleteQuote(){
            QuoteDeleteRequest req = QuoteDeleteRequest.builder()
                    .agentId("agentId")
                    .build();
            when(quoteService.deleteQuote(any())).thenReturn(true) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.deleteQuote(req);
            assertNotNull(wrapper);
            }

        @Test
        void testGetIdAndHideResult(){
            IdsRequest req = IdsRequest.builder()
                    .build();
            when(quoteService.getIdAndHideResult(any())).thenReturn(Collections.singletonList(IdAndHideList.builder().build())) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.getIdAndHideResult(req);
            assertNotNull(wrapper);
            }

        @Test
        void testArchiveApplicationQuote(){
            QuoteBaseRequest req = QuoteBaseRequest.builder()
                        .agentId("agentId")
                        .build();
            when(quoteService.archiveApplicationQuote(any())).thenReturn(true) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.archiveApplicationQuote(req);
            assertNotNull(wrapper);
            }  
            
        @Test
        void testAddingBeneficiaryUWQuestions(){
            QuoteBaseRequest req = QuoteBaseRequest.builder().agentId("agentId").build();
            when(quoteService.testAddingBeneficiaryUWQuestions(any())).thenReturn(true) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.testAddingBeneficiaryUWQuestions(req);
            assertNotNull(wrapper);
            }
        

        @Test
        void testUpdateQuoteBalance(){
            QuoteBalanceUpdateRequest req = QuoteBalanceUpdateRequest.builder()
                        .agentId("agentId")
                        .build();
            when(quoteService.updateQuoteBalance(any())).thenReturn(true) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.updateQuoteBalance(req);
            assertNotNull(wrapper);
        }

        

        @Test
        void testGetApplicationQuote(){
            when(quoteService.getApplicationQuote(any())).thenReturn(ApplicationQuote.builder().build()) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.getApplicationQuote(QuoteBaseRequest.builder().build());
            assertNotNull(wrapper);
            }
        
        @Test
        void testGetApplicationQuoteNullResponse(){
            when(quoteService.getApplicationQuote(any())).thenReturn(null) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.getApplicationQuote(QuoteBaseRequest.builder().build());
            assertNotNull(wrapper);
            }


        @Test
        void testSearchQuoteList(){
            when(quoteService.searchQuoteList(any())).thenReturn(HealthQuoteListResponse.builder().build()) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.searchQuoteList(QuoteListSearchRequest.builder().build());
            assertNotNull(wrapper);
            }

        @Test
        void testSearchQuoteListNullResponse(){
            when(quoteService.searchQuoteList(any())).thenReturn(null) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.searchQuoteList(QuoteListSearchRequest.builder().build());
            assertNotNull(wrapper);
            }


        @Test
        void testSearchApplicationQuoteList(){
            when(quoteService.searchApplicationQuoteList(any())).thenReturn(ApplicationQuoteListResponse.builder().build()) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.searchApplicationQuoteList(ApplicationQuoteListSearchRequest.builder().build());
            assertNotNull(wrapper);
            }

        @Test
        void testSearchApplicationQuoteListNullResponse(){
            when(quoteService.searchApplicationQuoteList(any())).thenReturn(null) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.searchApplicationQuoteList(ApplicationQuoteListSearchRequest.builder().build());
            assertNotNull(wrapper);
            }

        @Test
        void hideQuoteTest(){
            when(quoteService.getQuote(anyString(), anyString(), anyString())).thenReturn(Quote.builder().build()) ;
            when(quoteService.hideQuote(any())).thenReturn(true);
            ResponseEntity<DataWrapper> wrapper = quoteController.hideQuote(QuoteBaseRequest.builder().build());
            assertNotNull(wrapper);
            }
    


        @Test
        void getQuoteListTest(){
            when(quoteService.getQuoteList(any())).thenReturn(Collections.singletonList(Quote.builder().build())) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.getQuoteList(QuoteListRequest.builder().build());
            assertNotNull(wrapper);
            }


        @Test
        void getQuoteListEmptyListTest(){
            when(quoteService.getQuoteList(any())).thenReturn(Collections.emptyList()) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.getQuoteList(QuoteListRequest.builder().build());
            assertNotNull(wrapper);
            }


        @Test
        void getPolicyListTest(){
            when(quoteService.getCustomerQuoteList(any())).thenReturn(Collections.singletonList(Quote.builder().build())) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.getPolicyList(QuoteListRequest.builder().build());
            assertNotNull(wrapper);
            }

        @Test
        void getPolicyListEmptyListTest(){
            when(quoteService.getCustomerQuoteList(any())).thenReturn(Collections.emptyList()) ;
            ResponseEntity<DataWrapper> wrapper = quoteController.getPolicyList(QuoteListRequest.builder().build());
            assertNotNull(wrapper);
            }

        @Test
        void testSoftDeleteQuoteByAgent() {
            when(quoteService.softDeleteQuoteByAgent(
                    SoftDeleteQuoteByAgentRequest.builder().quoteId(anyString()).build()))
                    .thenReturn(true);
            ResponseEntity<DataWrapper> wrapper = quoteController.softDeleteQuote(SoftDeleteQuoteByAgentRequest.builder().quoteId(anyString()).build());
            assertNotNull(wrapper);
        }

    @Test
    void testDownloadHealthQuote() throws TemplateException, IOException {
        byte[] expectedData = "weqeqweqweqw".getBytes(StandardCharsets.UTF_8);
        when(quoteService.downloadQuote(any()))
                .thenReturn(expectedData);
        ResponseEntity<DataWrapper> response = quoteController.downloadQuote(CommonQuote.getHealthQuoteDownloadRequest());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDownloadHealthQuoteResponseNull() throws TemplateException, IOException {
        when(quoteService.downloadQuote(any()))
                .thenReturn(null);
        ResponseEntity<DataWrapper> response = quoteController.downloadQuote(CommonQuote.getHealthQuoteDownloadRequest());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().getData());
    }
}
