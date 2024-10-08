package ke.co.apollo.health.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ke.co.apollo.health.common.domain.model.Question;
import ke.co.apollo.health.common.domain.model.QuoteQuestion;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.response.ResultResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.domain.request.QuestionListRequest;
import ke.co.apollo.health.domain.request.QuoteBaseRequest;
import ke.co.apollo.health.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health/api")
@Api(tags = "Health App Question API")
public class QuestionController {

  @Autowired
  private QuestionService questionService;

  @PostMapping("/question/submit")
  @ApiOperation("Submit Question")
  public ResponseEntity<DataWrapper> submitQuestion(
      @ApiParam(name = "question", value = "Update Question Request Payload", required = true)
      @Validated @RequestBody Question question) {
    boolean result = questionService.submitQuestion(question);
    return ResponseEntity
        .ok(new DataWrapper(ResultResponse.builder().result(result).build()));
  }

  @PostMapping("/question/list")
  @ApiOperation("Question list")
  public ResponseEntity<DataWrapper> getQuoteList(
      @ApiParam(name = "question", value = "Get Question List Request Payload", required = true)
      @Validated @RequestBody QuestionListRequest questionListRequest) {
    Question question = questionService.getQuestion(questionListRequest);
    if (question == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }

    return ResponseEntity.ok(new DataWrapper(question));
  }

  @PostMapping("/quote/question")
  @ApiOperation("Question list")
  public ResponseEntity<DataWrapper> getQuoteQuestion(
      @ApiParam(name = "request)", value = "Get Quote Question Request Payload", required = true)
      @Validated @RequestBody QuoteBaseRequest request) {
    QuoteQuestion question = questionService.getQuoteQuestion(request);
    if (question == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }

    return ResponseEntity.ok(new DataWrapper(question));
  }

  @PostMapping("/policy/question")
  @ApiOperation("Question list")
  public ResponseEntity<DataWrapper> getPolicyQuestion(
      @ApiParam(name = "request)", value = "Get Policy Question Request Payload", required = true)
      @Validated @RequestBody PolicyNumberRequest request) {
    QuoteQuestion question = questionService.getPolicyQuestion(request);
    if (question == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }

    return ResponseEntity.ok(new DataWrapper(question));
  }
}
