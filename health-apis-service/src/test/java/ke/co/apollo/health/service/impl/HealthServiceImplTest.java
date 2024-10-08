package ke.co.apollo.health.service.impl;
import static org.junit.jupiter.api.Assertions.assertEquals;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import ke.co.apollo.health.service.CustomerService;
import ke.co.apollo.health.service.QuestionService;
import ke.co.apollo.health.service.QuoteService;
import ke.co.apollo.health.domain.request.ClearDataRequest;


class HealthServiceImplTest {

  @Mock
  private CustomerService customerService;

  @Mock
  private QuoteService quoteService;

  @Mock
  private QuestionService questionService;

  @InjectMocks
  private HealthServiceImpl healthService;

  @BeforeEach
  void setUp() {
    initMocks(this);
  }

  @Test
  void testClearClientData() {
    ClearDataRequest request = ClearDataRequest.builder()
        .agentId("1")
        .customerId("2")
        .quoteId("3")
        .build();
    when(customerService.deleteDependant(any())).thenReturn(1);
    when(quoteService.softDeleteQuoteByCustomerId(anyString())).thenReturn(true);
    when(questionService.deleteQuestion(any())).thenReturn(true);

    // When
    boolean result = healthService.clearClientData(request);

    // Then
    verify(customerService).deleteDependant(any());
    verify(quoteService).softDeleteQuoteByCustomerId(anyString());
    verify(questionService).deleteQuestion(any());
    assertEquals(true, result);
  }

}