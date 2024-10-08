package ke.co.apollo.health.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import freemarker.template.TemplateException;
import ke.co.apollo.health.domain.request.*;
import ke.co.apollo.health.domain.response.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ke.co.apollo.health.common.domain.model.ApplicationQuote;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.common.domain.model.response.ApplicationQuoteListResponse;
import ke.co.apollo.health.common.domain.model.response.HealthQuoteListResponse;
import ke.co.apollo.health.common.domain.model.response.ResultResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.mapping.QuoteMapping;
import ke.co.apollo.health.service.QuoteService;

@RestController
@RequestMapping("/health/api")
@Api(tags = "Health App Quote API")
public class QuoteController {

  @Autowired
  private QuoteService quoteService;

  @PostMapping("/quote/benefit/update")
  @ApiOperation("Update Quote Benefit")
  public ResponseEntity<DataWrapper> updateQuoteBenefit(
      @ApiParam(name = "quote", value = "Update Quote Benefit Request Payload", required = true)
      @Valid @RequestBody QuoteBenefitUpdateRequest request) {
    Quote quote = quoteService.updateQuoteBenefit(request);
    return ResponseEntity
        .ok(new DataWrapper(
            QuoteIdResponse.builder().quoteId(quote.getId()).build()));
  }

  @PostMapping("/quote/startdate/update")
  @ApiOperation("Update Quote Start Date")
  public ResponseEntity<DataWrapper> updateQuoteStartDate(
      @ApiParam(name = "quote", value = "Update Quote Start Date Request Payload", required = true)
      @Valid @RequestBody QuoteStartDateUpdateRequest request) {
    Quote quote = quoteService.updateQuoteStartDate(request);
    return ResponseEntity
        .ok(new DataWrapper(
            QuoteIdResponse.builder().quoteId(quote.getId()).build()));
  }

  @PostMapping("/quote/finish")
  @ApiOperation("Finish Quote")
  public ResponseEntity<DataWrapper> updateQuote(
      @ApiParam(name = "quote", value = "Finish Quote Request Payload", required = true)
      @Valid @RequestBody QuoteFinishRequest request) {
    Quote quote = quoteService.finishQuote(request);
    return ResponseEntity
        .ok(new DataWrapper(
            QuoteIdResponse.builder().quoteId(quote.getId()).build()));
  }

  @PostMapping("/quote/addIntermediary")
  @ApiOperation("Add Intermediary To Policy")
  public ResponseEntity<DataWrapper> addIntermediaryToPolicy(
      @ApiParam(name = "quote", value = "Add Intermediary To Policy Payload", required = true)
      @Valid @RequestBody QuoteBaseRequest request) {

    boolean result = quoteService
        .addIntermediaryToPolicy(request.getQuoteId(), request.getCustomerId(),
            request.getAgentId());
    return ResponseEntity.ok(new DataWrapper(result));
  }

  @PostMapping("/quote/list")
  @ApiOperation("Quote list")
  public ResponseEntity<DataWrapper> getQuoteList(
      @ApiParam(name = "quote", value = "Get Quote List Request Payload", required = true)
      @Valid @RequestBody QuoteListRequest quoteListRequest) {
    List<Quote> quoteList = quoteService.getQuoteList(quoteListRequest);
    if (CollectionUtils.isEmpty(quoteList)) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }

    List<QuoteListResponse> quotes = new ArrayList<>();
    for (Quote quote : quoteList) {
      quotes.add(QuoteMapping.quote2QuoteListResponse(quote));
    }
    return ResponseEntity.ok(new DataWrapper(quotes));
  }

  @PostMapping("/quotes/search")
  @ApiOperation("Search Quote list")
  public ResponseEntity<DataWrapper> searchQuoteList(
      @ApiParam(name = "quote", value = "Search Quote List Request Payload", required = true)
      @Valid @RequestBody QuoteListSearchRequest quoteListSearchRequest) {
    HealthQuoteListResponse response = quoteService.searchQuoteList(quoteListSearchRequest);
    if (response == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }

    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/quote/delete")
  @ApiOperation("Quote delete")
  public ResponseEntity<DataWrapper> deleteQuote(
      @ApiParam(name = "quote", value = "Delete Quote Request Payload", required = true)
      @Valid @RequestBody QuoteDeleteRequest quote) {
    boolean result = quoteService.deleteQuote(quote);
    return ResponseEntity.ok(new DataWrapper(ResultResponse.builder().result(result).build()));
  }

  @PostMapping("/quote/ids")
  @ApiOperation("Quote ids")
  public ResponseEntity<DataWrapper> getIdAndHideResult(
      @ApiParam(name = "quote", value = "query ids Quote Request Payload", required = true)
      @Valid @RequestBody IdsRequest quote) {
    List<IdAndHideList>  result = quoteService.getIdAndHideResult(quote);
    IdAndHideResponse idAndHideResponse = new IdAndHideResponse();
    idAndHideResponse.setIdMap(result.stream().collect(Collectors.toMap(IdAndHideList::getId,IdAndHideList::isHide)));
    return ResponseEntity.ok(new DataWrapper(idAndHideResponse));
  }

  @PostMapping("/quote/agent/hide")
  @ApiOperation("Quote hide")
  public ResponseEntity<DataWrapper> hideQuote(
      @ApiParam(name = "quote", value = "hide Quote Request Payload", required = true)
      @Valid @RequestBody QuoteBaseRequest request) {
    Quote quote = quoteService.getQuote(request.getQuoteId(), request.getCustomerId(), request.getAgentId());
    if (quote != null) {
      boolean result = quoteService.hideQuote(new QuoteAgentHideRequest(request.getQuoteId()));
      return ResponseEntity.ok(new DataWrapper(ResultResponse.builder().result(result).build()));
    }else {
      DataWrapper dataWrapper = new DataWrapper(new ResultResponse(false));
      dataWrapper.setMessage("can not find the quote");
      return ResponseEntity.ok(dataWrapper);
    }
  }

  @PostMapping("/quote/agent/softDelete")
  @ApiOperation("Quote soft delete")
  public ResponseEntity<DataWrapper> softDeleteQuote(
          @ApiParam(name = "quote", value = "Delete Quote Request Payload", required = true)
          @Valid @RequestBody SoftDeleteQuoteByAgentRequest quote) {
    boolean result = quoteService.softDeleteQuoteByAgent(quote);
    return ResponseEntity.ok(new DataWrapper(ResultResponse.builder().result(result).build()));
  }


  @PostMapping("/quote/customer/list")
  @ApiOperation("Customer Quote List")
  public ResponseEntity<DataWrapper> getPolicyList(
      @ApiParam(name = "quote", value = "Get Quote List Request Payload", required = true)
      @Valid @RequestBody QuoteListRequest quoteListRequest) {
    List<Quote> quoteList = quoteService.getCustomerQuoteList(quoteListRequest);
    if (CollectionUtils.isEmpty(quoteList)) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    List<CustomerQuoteResponse> quotes = new ArrayList<>();
    quoteList.stream()
        .forEach(quote -> quotes.add(QuoteMapping.quote2CustomerQuoteResponse(quote)));
    return ResponseEntity.ok(new DataWrapper(quotes));
  }


  @PostMapping("/application/quote/list")
  @ApiOperation("Search Application Quote list")
  public ResponseEntity<DataWrapper> searchApplicationQuoteList(
      @ApiParam(name = "quote", value = "Search Quote List Request Payload", required = true)
      @Valid @RequestBody ApplicationQuoteListSearchRequest request) {
    ApplicationQuoteListResponse response = quoteService.searchApplicationQuoteList(request);
    if (response == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }

    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/application/quote/detail")
  @ApiOperation("Get Application Quote Detail")
  public ResponseEntity<DataWrapper> getApplicationQuote(
      @ApiParam(name = "quote", value = "Get Quote Detail Request Payload", required = true)
      @Valid @RequestBody QuoteBaseRequest request) {
    ApplicationQuote response = quoteService.getApplicationQuote(request);
    if (response == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }

    return ResponseEntity.ok(new DataWrapper(response));
  }

  @PostMapping("/application/quote/archive")
  @ApiOperation("Archive Application Quote")
  public ResponseEntity<DataWrapper> archiveApplicationQuote(
      @ApiParam(name = "request", value = "Archive Quote Request Payload", required = true)
      @Valid @RequestBody QuoteBaseRequest request) {
    boolean response = quoteService.archiveApplicationQuote(request);
    return ResponseEntity.ok(new DataWrapper(ResultResponse.builder().result(response).build()));
  }

  @PostMapping("/quote/questions/test")
  @ApiOperation("Test Adding Beneficiary UW Questions")
  public ResponseEntity<DataWrapper> testAddingBeneficiaryUWQuestions(
      @ApiParam(name = "quote", value = "Test Adding Beneficiary UW Questions Request Payload", required = true)
      @Valid @RequestBody QuoteBaseRequest request) {
    boolean result = quoteService.testAddingBeneficiaryUWQuestions(request);
    return ResponseEntity.ok(new DataWrapper(ResultResponse.builder().result(result).build()));
  }

  @PostMapping("/quote/updatebalance/test")
  @ApiOperation("Test Quote Balance Update")
  public ResponseEntity<DataWrapper> updateQuoteBalance(
      @Valid @RequestBody QuoteBalanceUpdateRequest request) {
    boolean result = quoteService.updateQuoteBalance(request);
    return ResponseEntity.ok(new DataWrapper(ResultResponse.builder().result(result).build()));
  }

  @PostMapping("/quote/step")
  @ApiOperation("Get Quote step")
  public ResponseEntity<DataWrapper> getQuoteStep(
          @Valid @RequestBody QuoteStepRequest request) {
    QuoteStepResponse result = quoteService.getQuoteStep(request);
    return ResponseEntity.ok(new DataWrapper(result));
  }

  @PostMapping("/quote/downloadQuote")
  @ApiOperation("Download Quote Pdf")
  public ResponseEntity<DataWrapper> downloadQuote(
          @Valid @RequestBody HealthQuoteDownloadRequest request) throws TemplateException, IOException {
    byte[] downloadQuote = quoteService.downloadQuote(request);
    if (downloadQuote == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity
            .ok(new DataWrapper(new String(downloadQuote)));
  }

}
