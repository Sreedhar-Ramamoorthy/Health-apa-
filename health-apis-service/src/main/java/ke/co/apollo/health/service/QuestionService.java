package ke.co.apollo.health.service;

import ke.co.apollo.health.common.domain.model.Question;
import ke.co.apollo.health.common.domain.model.QuoteQuestion;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.domain.request.QuestionDeleteRequest;
import ke.co.apollo.health.domain.request.QuoteBaseRequest;

public interface QuestionService {

  boolean submitQuestion(Question question);

  Question getQuestion(QuoteBaseRequest request);

  boolean deleteQuestion(QuestionDeleteRequest questionDeleteRequest);

  QuoteQuestion getQuoteQuestion(QuoteBaseRequest request);

  QuoteQuestion getPolicyQuestion(PolicyNumberRequest request);

}
