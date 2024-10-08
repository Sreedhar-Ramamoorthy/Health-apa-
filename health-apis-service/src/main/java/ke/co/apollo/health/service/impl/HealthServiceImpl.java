package ke.co.apollo.health.service.impl;

import ke.co.apollo.health.domain.request.ClearDataRequest;
import ke.co.apollo.health.domain.request.DependantDeleteRequest;
import ke.co.apollo.health.domain.request.QuestionDeleteRequest;
import ke.co.apollo.health.service.CustomerService;
import ke.co.apollo.health.service.HealthService;
import ke.co.apollo.health.service.QuestionService;
import ke.co.apollo.health.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HealthServiceImpl implements HealthService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private CustomerService customerService;

  @Autowired
  private
  QuoteService quoteService;

  @Autowired
  private
  QuestionService questionService;

  @Override
  @Transactional("healthDataTransactionManager")
  public boolean clearClientData(ClearDataRequest request) {

    int customer = customerService
        .deleteDependant(DependantDeleteRequest.builder().agentId(request.getAgentId())
            .customerId(request.getCustomerId()).quoteId(request.getQuoteId()).build());
    
    boolean softDeletedQuote = quoteService
    .softDeleteQuoteByCustomerId(request.getCustomerId());

    boolean question = questionService
        .deleteQuestion(
            QuestionDeleteRequest.builder().agentId(request.getAgentId())
                .customerId(request.getCustomerId())
                .quoteId(request.getQuoteId())
                .build());

    logger.debug("Delete dependant:{}, delete quote:{}, delete question:{} ", customer, softDeletedQuote,
        question);

    return true;
  }
}
