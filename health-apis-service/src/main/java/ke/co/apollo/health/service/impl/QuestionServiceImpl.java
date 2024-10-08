package ke.co.apollo.health.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ke.co.apollo.health.common.domain.model.HealthPolicy;
import ke.co.apollo.health.common.domain.model.Question;
import ke.co.apollo.health.common.domain.model.QuestionAnswer;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.common.domain.model.QuoteQuestion;
import ke.co.apollo.health.common.domain.model.QuoteQuestion.Members;
import ke.co.apollo.health.common.domain.model.QuoteQuestion.Members.Questions;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.response.GetCustomerInfoResponse;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.domain.request.QuestionDeleteRequest;
import ke.co.apollo.health.domain.request.QuestionListRequest;
import ke.co.apollo.health.domain.request.QuoteBaseRequest;
import ke.co.apollo.health.domain.request.QuoteListRequest;
import ke.co.apollo.health.mapper.health.QuestionMapper;
import ke.co.apollo.health.service.CustomerService;
import ke.co.apollo.health.service.PolicyService;
import ke.co.apollo.health.service.QuestionService;
import ke.co.apollo.health.service.QuoteService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl implements QuestionService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  QuestionMapper questionMapper;

  @Autowired
  QuoteService quoteService;

  @Autowired
  PolicyService policyService;

  @Autowired
  CustomerService customerService;

  @Override
  public boolean submitQuestion(Question question) {
    if (question == null) {
      throw new BusinessException("submit question exception!");
    }
    boolean result = false;
    logger.debug("submit question: {}", question);
    QuestionListRequest questionListRequest = QuestionListRequest.builder()
        .agentId(question.getAgentId())
        .customerId(question.getCustomerId())
        .quoteId(question.getQuoteId())
        .build();
    Question questionList = this.getQuestion(questionListRequest);
    if (questionList == null) {
      QuoteListRequest quoteListRequest = QuoteListRequest.builder().agentId(question.getAgentId())
          .customerId(question.getCustomerId()).build();

      List<Quote> quoteList = quoteService.getQuoteList(quoteListRequest);
      for (Quote quote : quoteList) {
        question.setQuoteId(quote.getId());
        result = questionMapper.insert(question) > 0;
      }

    } else {
      result = questionMapper.update(question) > 0;
    }
    return result;
  }

  @Override
  public Question getQuestion(QuoteBaseRequest request) {
    return questionMapper
        .getQuestion(request.getQuoteId(), request.getCustomerId(), request.getAgentId());
  }

  @Override
  public boolean deleteQuestion(QuestionDeleteRequest question) {
    if (question == null) {
      throw new BusinessException("delete quote exception!");
    }
    logger.debug("delete question: {}", question);
    return questionMapper
        .delete(question.getQuoteId(), question.getCustomerId(), question.getAgentId()) > 0;
  }

  @Override
  public QuoteQuestion getQuoteQuestion(QuoteBaseRequest request) {
    Question question = this.getQuestion(request);
    if (question == null) {
      logger.debug("can't find the question, request: {}", request);
      return null;
    }
    QuoteQuestion quoteQuestion = QuoteQuestion.builder().quoteId(question.getQuoteId())
        .customerId(question.getCustomerId())
        .agentId(question.getAgentId()).build();
    List<QuestionAnswer> questionAnswerList = new ArrayList<>();
    question.getAnswers().stream().forEach(t -> {
      if (t.isAnswer() && CollectionUtils.isNotEmpty(t.getDetail())) {
        t.getDetail().stream().forEach(detail -> {
          QuestionAnswer questionAnswer = QuestionAnswer.builder().code(detail.getCode())
              .name(detail.getName())
              .questionId(t.getQuestionId())
              .content(detail.getContent()).doctorName(detail.getDoctorName()).build();
          questionAnswerList.add(questionAnswer);
        });
      }
    });
    List<Members> members = new ArrayList<>();
    Map<String, List<QuestionAnswer>> map = questionAnswerList.stream()
        .collect(Collectors.groupingBy(QuestionAnswer::getCode));
    map.forEach((key, value) -> {
      Members member = Members.builder().code(key)
          .name(value.stream().findFirst().get().getName()).build();
      List<Questions> questions = value.stream()
          .map(v -> Questions.builder().questionId(v.getQuestionId()).content(v.getContent())
              .doctorName(v.getDoctorName()).build()).collect(Collectors.toList());
      member.setQuestions(questions);
      members.add(member);
    });
    quoteQuestion.setMembers(members);
    return quoteQuestion;
  }

  @Override
  public QuoteQuestion getPolicyQuestion(PolicyNumberRequest request) {
    QuoteQuestion quoteQuestion = null;
    HealthPolicy policy = policyService
        .getPolicy(request.getPolicyNumber(), request.getEffectiveDate());
    if (policy == null) {
      throw new BusinessException("can't find the policy");
    }
    String quoteId = policy.getQuoteId();
    GetCustomerInfoResponse customer = customerService
        .getCustomerByEntityId(policy.getPolicyHolderId());
    if (customer != null) {
      quoteQuestion = this.getQuoteQuestion(
          QuoteBaseRequest.builder().customerId(customer.getCustomerId()).quoteId(quoteId).build());
    } else {
      logger.error("can't find the customer by entity id");
    }
    return quoteQuestion;
  }
}
