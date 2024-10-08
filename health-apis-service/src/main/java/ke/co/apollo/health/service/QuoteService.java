package ke.co.apollo.health.service;

import java.io.IOException;
import java.util.List;

import freemarker.template.TemplateException;
import ke.co.apollo.health.common.domain.model.ApplicationQuote;
import ke.co.apollo.health.common.domain.model.PolicyNotificationTask;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.common.domain.model.response.ApplicationQuoteListResponse;
import ke.co.apollo.health.common.domain.model.response.HealthQuoteListResponse;
import ke.co.apollo.health.domain.request.*;
import ke.co.apollo.health.domain.response.IdAndHideList;
import ke.co.apollo.health.domain.response.QuoteStepResponse;

public interface QuoteService {

  boolean updateQuoteStatus(QuoteStatusUpdateRequest quote);

  boolean updateQuoteBalance(QuoteBalanceUpdateRequest quote);

  Quote updateQuoteBenefit(QuoteBenefitUpdateRequest quote);

  boolean updateQuotePremiumByCustomer(String quoteId, String customerId, String agentId);

  Quote updateQuoteStartDate(QuoteStartDateUpdateRequest quote);

  Quote finishQuote(QuoteFinishRequest quote);

  boolean sendPolicySMSNotification(PolicyNotificationTask task);

  boolean addIntermediaryToPolicy(String quoteId, String customerId, String agentId);

  boolean addIntermediaryToPolicy(Quote quote);
  boolean addIntermediaryBranchDetails(Quote quote, Integer entityId);

  List<Quote> getQuoteList(QuoteListRequest quote);

  HealthQuoteListResponse searchQuoteList(QuoteListSearchRequest quote);

  List<Quote> getCustomerQuoteList(QuoteListRequest quote);

  Quote getQuote(String quoteId, String customerId, String agentId);

  Quote getQuoteNoThrowException(String quoteId, String customerId, String agentId);

  Quote getQuoteByPolicyNumber(String customerId, String policyNumber);

  boolean deleteQuote(QuoteDeleteRequest quote);

  List<IdAndHideList> getIdAndHideResult(IdsRequest quote);

  boolean hideQuote(QuoteAgentHideRequest quote);

  boolean deleteQuoteByCustomerId(String customerId);

  List<Quote> createInitQuote(String customerId, String agentId, String quoteId, boolean isChildrenOnly, int productId);

  ApplicationQuoteListResponse searchApplicationQuoteList(
      ApplicationQuoteListSearchRequest request);

  ApplicationQuote getApplicationQuote(QuoteBaseRequest request);

  boolean archiveApplicationQuote(QuoteBaseRequest request);

  String searchQuoteByPolicyId(Integer getIdAndHideResult, String policyNumber);

  boolean createActivedQuoteNotificationTask();

  boolean addBeneficiaryUWQuestions(Quote quote);

  boolean testAddingBeneficiaryUWQuestions(QuoteBaseRequest request);

  QuoteStepResponse getQuoteStep(QuoteStepRequest request);

  boolean softDeleteQuoteByCustomerId(String customerId);

  boolean softDeleteQuoteByAgent(SoftDeleteQuoteByAgentRequest quote);

  byte[] downloadQuote(HealthQuoteDownloadRequest request) throws IOException, TemplateException;

}
