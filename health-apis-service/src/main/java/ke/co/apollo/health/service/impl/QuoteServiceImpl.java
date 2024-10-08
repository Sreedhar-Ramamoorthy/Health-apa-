package ke.co.apollo.health.service.impl;

import com.github.pagehelper.page.PageMethod;
import com.google.gson.Gson;
import com.itextpdf.html2pdf.HtmlConverter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.*;
import ke.co.apollo.health.common.domain.model.BenefitCategoryMap.OptionalBenefits;
import ke.co.apollo.health.common.domain.model.QuoteQuestion.Members;
import ke.co.apollo.health.common.domain.model.QuoteQuestion.Members.Questions;
import ke.co.apollo.health.common.domain.model.remote.AddBenefitsToPolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.AddBenefitsToPolicyRequest.BenefitCategoriesBean;
import ke.co.apollo.health.common.domain.model.remote.AddBenefitsToPolicyRequest.BenefitCategoriesBean.BenefitsBean;
import ke.co.apollo.health.common.domain.model.remote.ApiResponse;
import ke.co.apollo.health.common.domain.model.remote.ApiResponse.ErrorsBean;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyResponse;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.request.AddBusinessSourceToIndividualPolicyRequest.BusinessSourceBean;
import ke.co.apollo.health.common.domain.model.request.AddBusinessSourceToIndividualPolicyRequest.BusinessSourceBean.InterestedPartiesBean;
import ke.co.apollo.health.common.domain.model.request.AddBusinessSourceToIndividualPolicyRequest.BusinessSourceBean.InterestedPartiesBean.CommissionBean;
import ke.co.apollo.health.common.domain.model.request.AddIndividualPolicyBeneficiaryUWQuestionsRequest.BeneficiaryUWQuestions;
import ke.co.apollo.health.common.domain.model.request.AddIndividualPolicyBeneficiaryUWQuestionsRequest.BeneficiaryUWQuestions.UnderwritingQuestions;
import ke.co.apollo.health.common.domain.model.response.ApplicationQuoteListResponse;
import ke.co.apollo.health.common.domain.model.response.HealthQuoteListResponse;
import ke.co.apollo.health.common.enums.*;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.common.utils.EncodeUtils;
import ke.co.apollo.health.common.utils.HealthDateUtils;
import ke.co.apollo.health.config.MdcConfig;
import ke.co.apollo.health.config.NotificationMessageBuilder;
import ke.co.apollo.health.config.QuestionConfig;
import ke.co.apollo.health.domain.ApplicationQuoteListSearchFilter;
import ke.co.apollo.health.domain.PolicyBeneficiary;
import ke.co.apollo.health.domain.PolicyBeneficiary.Beneficiary;
import ke.co.apollo.health.domain.QuoteListSearchFilter;
import ke.co.apollo.health.domain.entity.AgentBranchEntity;
import ke.co.apollo.health.domain.entity.HealthStepEntity;
import ke.co.apollo.health.domain.request.PolicyAdditionalInfoList;
import ke.co.apollo.health.domain.request.PolicyAdditionalInfoRequest;
import ke.co.apollo.health.domain.request.*;
import ke.co.apollo.health.domain.response.*;
import ke.co.apollo.health.enums.HealthQuoteStepsEnum;
import ke.co.apollo.health.enums.QuoteStatusEnum;
import ke.co.apollo.health.event.PolicyNotificationEventPublisher;
import ke.co.apollo.health.event.ReminderEventPublisher;
import ke.co.apollo.health.mapper.health.QuoteMapper;
import ke.co.apollo.health.remote.NotificationRemote;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.repository.HealthAgentBranchRepository;
import ke.co.apollo.health.repository.HealthStepRepository;
import ke.co.apollo.health.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@EnableAsync
public class QuoteServiceImpl implements QuoteService {

  public static final String BEFORE_PREMIUM = "before premium:{}";
  public static final String TRAVEL_INSURANCE_PREMIUM = "travelInsurancePremium : {}";
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static final Map<String, String> sortTypeMap = Map.of("createdDate", "create_time", "name", "name");

  private static final Map<String, String> applicationQuoteSortTypeMap = Map.of("createdTime", "quote.create_time", "policyNumber", "quote.ext_policy_number");

  public static final String  AGENT_NAME = "agentName";
  public static final String  AGENT_BRANCH="agentBranch";
  @Autowired
  PolicyRemote policyRemote;

  @Autowired
  NotificationRemote notificationRemote;

  @Autowired
  BenefitCategoryMap benefitCategoryMap;

  @Autowired
  QuoteMapper quoteMapper;

  @Autowired
  ProductService productService;

  @Autowired
  CustomerService customerService;

  @Autowired
  BeneficiaryService beneficiaryService;

  @Autowired
  QuestionService questionService;

  @Autowired
  IntermediaryService intermediaryService;

  @Autowired
  PolicyNotificationEventPublisher policySMSEventPublisher;

  @Autowired
  ReminderEventPublisher reminderEventPublisher;

  @Autowired
  NotificationMessageBuilder notificationMessageBuilder;

  @Autowired
  PremiumService premiumService;

  @Autowired
  QuestionConfig questionConfig;

  @Autowired
  HealthStepRepository stepRepository;

  @Autowired
  HealthAgentBranchRepository agentBranchRepository;

  private final Configuration configuration;

  public QuoteServiceImpl(Configuration configuration) {
    this.configuration = configuration;
  }
  @Override
  public boolean updateQuoteStatus(QuoteStatusUpdateRequest request) {
    String customerId = request.getCustomerId();
    String agentId = request.getAgentId();
    String quoteId = request.getQuoteId();
    logger.debug("update quote status, QuoteStatusUpdateRequest: {}", request);

    Quote quote = getQuote(quoteId, customerId, agentId);

    if (!PolicyStatus.APPLICATION.getValue().equals(quote.getStatus())) {
      throw new BusinessException("can't update the quote status, status: " + quote.getStatus());
    }

    quote.setStatus(request.getStatus());
    quote.setUpdateTime(new Date());

    return quoteMapper.update(quote) != 1;
  }

  @Override
  public boolean updateQuoteBalance(QuoteBalanceUpdateRequest request) {
    boolean result = false;
    String customerId = request.getCustomerId();
    String agentId = request.getAgentId();
    String quoteId = request.getQuoteId();
    logger.debug("update quote balance, QuoteStatusUpdateRequest: {}", request);

    Quote quote = getQuote(quoteId, customerId, agentId);

    if (!PolicyStatus.APPLICATION.getValue().equals(quote.getStatus())) {
      throw new BusinessException("can't update the quote balance, status: " + quote.getStatus());
    }

    BigDecimal balance = quote.getBalance().subtract(request.getPaidAmount());
    if (balance.intValue() <= 0) {
      quote.setStatus(PolicyStatus.UNDERWRITING.getValue());
      ReminderRequest reminderRequest = ReminderRequest.builder().customerId(customerId)
              .quoteId(quoteId).policyNumber(quote.getExtPolicyNumber())
              .effectiveDate(DateFormatUtils.format(quote.getEffectiveDate(), GlobalConstant.YYYYMMDD))
              .type("health_quote")
              .build();
      logger.debug("send quote reminder");
      reminderEventPublisher.publishReminder(reminderRequest);
    }

    quote.setBalance(balance);
    quote.setUpdateTime(new Date());

    result = quoteMapper.update(quote) == 1;
    if (balance.intValue() <= 0 && result) {
      Customer customer = customerService.getCustomer(customerId);
      if (customer != null) {
        String text = notificationMessageBuilder
                .getMessage("SMS_MESSAGE_POLICY_PAID", quote.getCode());
        Date today = new Date();
        PolicyNotificationTask task = PolicyNotificationTask.builder().text(text)
                .destination(customer.getPhoneNumber()).type(NotificationType.SMS.getValue())
                .subtype(SMSTaskTypeEnum.PAY.getValue())
                .status(TaskStatusEnum.PENDING.getValue()).failureNumber(0)
                .policyNumber(quote.getExtPolicyNumber())
                .scheduleTime(today).createTime(today).build();
        policySMSEventPublisher.publishTask(task);
        PolicyNotificationTask nextTask = PolicyNotificationTask.builder().build();
        BeanUtils.copyProperties(task, nextTask);
        task.setStatus(TaskStatusEnum.CANCEL.getValue());
        policySMSEventPublisher.publishTask(nextTask);
      }
    }
    return result;


  }

  private CreatePolicyResponse createBasePolicy(Quote quote) {
    String policyStartDate = DateFormatUtils.format(quote.getStartDate(), GlobalConstant.YYYYMMDD);
    String policyRenewalDate = DateFormatUtils
            .format(HealthDateUtils.nextYear(quote.getStartDate()), GlobalConstant.YYYYMMDD);
    Integer productId = quote.getProductId();
    logger.info("====== Product id is {} ========", productId);
    if (quote.getProductId() == 52) {
      logger.info("Change product ID to 49 to create Shared Policy in Actisure ");
      productId = ProductEnum.JAMIIPLUS.getId();
    }
    logger.debug("====== Product id is now {} ========", quote.getProductId());
    CreatePolicyRequest createPolicyRequest = CreatePolicyRequest.builder()
            .productName(ProductEnum.getById(productId).getValue())
            .policyHolderEntityId(quote.getEntityId())
            .policyStartDate(policyStartDate)
            .policyEffectiveDate(policyStartDate)
            .policyRenewalDate(policyRenewalDate)
            .policyStatus(quote.getStatus())
            .policyAmount(BigDecimal.ZERO)
            .externalRef(quote.getCode())
            .build();
    CreatePolicyResponse createPolicyResponse = policyRemote.createPolicy(createPolicyRequest);
    if (createPolicyResponse != null) {
      logger.debug("create new policy response: {}", createPolicyResponse);
      if (!createPolicyResponse.isSuccess()) {
        List<String> msg = new ArrayList<>();
        createPolicyResponse.getErrors().stream().forEach(t -> {
                  logger.error("create new policy error detail: {}", t.getDescription());
                  msg.add(t.getDescription());
                }
        );
        throw new BusinessException("create policy exception: " + msg.toString());
      }
    }
    return createPolicyResponse;
  }

  private void addBenefitToPolicy(Quote quote, Integer policyId) {

    if (quote.getBenefit() != null) {
      AddBenefitsToPolicyRequest addBenefitsToPolicyRequest = AddBenefitsToPolicyRequest.builder()
              .policyId(policyId)
              .policyEffectiveDate(
                      DateFormatUtils.format(quote.getStartDate(), GlobalConstant.YYYYMMDD))
              .benefitCategories(this.buildBenefitRequest(quote.getBenefit(), quote.getProductId(), quote.isChildrenOnly()))
              .build();
      ApiResponse response = policyRemote.addBenefitsToPolicy(addBenefitsToPolicyRequest);
      String msg = this.checkApiResponse(response);
      if (StringUtils.isNotBlank(msg)) {
        throw new BusinessException("add benefit to policy exception: " + msg);
      }
    }
  }

  private String checkApiResponse(ApiResponse response) {
    StringBuilder msg = new StringBuilder();
    if (response == null) {
      msg = msg.append("response is empty");
    } else {
      if (!response.isSuccess()) {
        for (ErrorsBean errorsBean : response.getErrors()) {
          msg = msg.append(errorsBean.getDescription());
        }
      }
    }
    return msg.toString();
  }

  private void addBusinessSourceToPolicy(Quote quote, Integer agentEntityId) {

    if (quote.getBenefit() != null) {
      List<InterestedPartiesBean> interestedPartiesBeanList = new ArrayList<>();
      InterestedPartiesBean interestedParty1 = this
              .buildInterestedPartiesBean("", agentEntityId, "10");
      InterestedPartiesBean interestedParty2 = this
              .buildInterestedPartiesBean("WHT", agentEntityId, "1");
      InterestedPartiesBean interestedParty3 = this
              .buildInterestedPartiesBean("Africa Reinsurance", 1108, "20");
      InterestedPartiesBean interestedParty4 = this
              .buildInterestedPartiesBean("Africa Reinsurance Commission", 1108, "4.455");
      InterestedPartiesBean interestedParty5 = this
              .buildInterestedPartiesBean("Underwriter", 190, "BALANCE");
      InterestedPartiesBean interestedParty6 = this
              .buildInterestedPartiesBean("East Africa Reinsurance", 1110, "2");
      InterestedPartiesBean interestedParty7 = this
              .buildInterestedPartiesBean("East Africa Reinsurance Commission", 1110, "0.4455");
      InterestedPartiesBean interestedParty8 = this
              .buildInterestedPartiesBean("Kenya Reinsurance", 1109, "10");
      InterestedPartiesBean interestedParty9 = this
              .buildInterestedPartiesBean("Kenya Reinsurance Commission", 1109, "2.2275");
      InterestedPartiesBean interestedParty10 = this
              .buildInterestedPartiesBean("PTA Reinsurance", 1111, "8");
      InterestedPartiesBean interestedParty11 = this
              .buildInterestedPartiesBean("PTA Reinsurance Commission", 1111, "1.782");

      interestedPartiesBeanList.add(interestedParty1);
      interestedPartiesBeanList.add(interestedParty2);
      interestedPartiesBeanList.add(interestedParty3);
      interestedPartiesBeanList.add(interestedParty4);
      interestedPartiesBeanList.add(interestedParty5);
      interestedPartiesBeanList.add(interestedParty6);
      interestedPartiesBeanList.add(interestedParty7);
      interestedPartiesBeanList.add(interestedParty8);
      interestedPartiesBeanList.add(interestedParty9);
      interestedPartiesBeanList.add(interestedParty10);
      interestedPartiesBeanList.add(interestedParty11);
      BusinessSourceBean businessSourceBean = BusinessSourceBean.builder()
              .policyId(quote.getExtPolicyId())
              .policyEffectiveDate(DateFormatUtils.format(quote.getStartDate(), GlobalConstant.YYYYMMDD))
              .binderDetailWebName(String.valueOf(HealthDateUtils.getCurrentYear()))
              .disbursementBasis("Payment Received")
              .interestedParties(interestedPartiesBeanList)
              .build();
      AddBusinessSourceToIndividualPolicyRequest policyRequest = AddBusinessSourceToIndividualPolicyRequest
              .builder()
              .businessSource(businessSourceBean)
              .build();
      ApiResponse response = policyRemote.addBusinessSourceToIndividualPolicy(policyRequest);
      String msg = this.checkApiResponse(response);
      if (StringUtils.isNotBlank(msg)) {
        throw new BusinessException("add business source to policy exception: " + msg);
      }
    }
  }

  private InterestedPartiesBean buildInterestedPartiesBean(String role, Integer entityId,
                                                           String amount) {
    CommissionBean commission = CommissionBean.builder().applySalesTax(true).amount(amount)
            .fixed(true).indemnity(true).build();
    return InterestedPartiesBean.builder().role(role)
            .entityId(entityId).initialCommission(commission
            ).renewalCommission(commission).build();
  }

  private List<BenefitCategoriesBean> buildBenefitRequest(Benefit benefit, Integer productId, boolean isChildrenOnly) {
    List<BenefitCategoriesBean> benefitCategoriesBeans = new ArrayList<>();
    Integer inpatientLimit = benefit.getInpatientLimit();
    if (inpatientLimit != null && inpatientLimit > 0) {
      BenefitCategoriesBean inpatientBenefit = getInpatientBenefit(inpatientLimit, productId, isChildrenOnly);
      benefitCategoriesBeans.add(inpatientBenefit);
      BenefitCategoriesBean optionalBenefit = getOptionalBenefit(benefit, productId, isChildrenOnly);
      if (optionalBenefit != null) {
        benefitCategoriesBeans.add(optionalBenefit);
      }
    }

    return benefitCategoriesBeans;
  }

  private BenefitCategoriesBean getInpatientBenefit(Integer inpatientLimit, Integer productId, boolean isChildrenOnly) {
    BenefitsBean benefitsBean = BenefitsBean.builder()
            .webName(getWebName(BenefitEnum.INPATIENT.getValue(), "", inpatientLimit, productId, isChildrenOnly)).build();
    return BenefitCategoriesBean.builder()
            .webName(BenefitEnum.INPATIENT.getValue()).benefits(Arrays.asList(benefitsBean)).build();
  }

  private BenefitCategoriesBean getOptionalBenefit(Benefit coverLimit, Integer productId, boolean isChildrenOnly) {
    BenefitCategoriesBean benefitCategoriesBean = null;
    List<BenefitsBean> benefitsBeans = new ArrayList<>();
    Integer outpatientLimit = coverLimit.getOutpatientLimit();
    if (outpatientLimit != null && outpatientLimit > 0) {
      BenefitsBean benefitsBean = BenefitsBean.builder()
              .webName(
                      getWebName(BenefitEnum.OPTIONALBENEFITS.getValue(), BenefitEnum.OUTPATIENT.getValue(),
                              outpatientLimit, productId, isChildrenOnly)).build();
      benefitsBeans.add(benefitsBean);
    }
    Integer detalLimit = coverLimit.getDentalLimit();
    if (detalLimit != null && detalLimit > 0) {
      BenefitsBean benefitsBean = BenefitsBean.builder()
              .webName(
                      getWebName(BenefitEnum.OPTIONALBENEFITS.getValue(), BenefitEnum.DENTAL.getValue(),
                              detalLimit, productId, isChildrenOnly))
              .build();
      benefitsBeans.add(benefitsBean);
    }
    Integer maternityLimit = coverLimit.getMaternityLimit();
    if (maternityLimit != null && maternityLimit > 0) {
      BenefitsBean benefitsBean = BenefitsBean.builder()
              .webName(
                      getWebName(BenefitEnum.OPTIONALBENEFITS.getValue(), BenefitEnum.MATERNITY.getValue(),
                              maternityLimit, productId, false))
              .build();
      benefitsBeans.add(benefitsBean);
    }
    Integer opticalLimit = coverLimit.getOpticalLimit();
    if (opticalLimit != null && opticalLimit > 0) {
      BenefitsBean benefitsBean = BenefitsBean.builder()
              .webName(
                      getWebName(BenefitEnum.OPTIONALBENEFITS.getValue(), BenefitEnum.OPTICAL.getValue(),
                              opticalLimit, productId, isChildrenOnly))
              .build();
      benefitsBeans.add(benefitsBean);
    }

    if (!CollectionUtils.isEmpty(benefitsBeans)) {
      benefitCategoriesBean = BenefitCategoriesBean.builder()
              .webName(BenefitEnum.OPTIONALBENEFITS.getValue()).benefits(benefitsBeans).build();
    }

    return benefitCategoriesBean;

  }

  private String getWebName(String type, String subType, int limit, Integer productId, boolean isChildrenOnly) {
    String name = "";
    if (BenefitEnum.INPATIENT.getValue().equals(type)) {
      if (productId == 52) {
        name = benefitCategoryMap.getInpatient().getFamily().get(limit);
      } else if (productId == 49) {
        if (isChildrenOnly)
          name = benefitCategoryMap.getInpatient().getChildOnly().get(limit);
        else
          name = benefitCategoryMap.getInpatient().getPerson().get(limit);
      }
    }
    else
      name = checkOptionalBenefits(type, subType, limit, isChildrenOnly);
    return name;
  }

  private String checkOptionalBenefits(String type, String subType, int limit, boolean isChildrenOnly) {
    String name = "";
    if (BenefitEnum.OPTIONALBENEFITS.getValue().equals(type)) {
      OptionalBenefits optionalBenefits = benefitCategoryMap.getOptionalBenefits();
      if (BenefitEnum.DENTAL.getValue().equals(subType)) {
        name = optionalBenefits.getDental().get(limit);
      } else if (BenefitEnum.OPTICAL.getValue().equals(subType)) {
        name = optionalBenefits.getOptical().get(limit);
      } else if (BenefitEnum.MATERNITY.getValue().equals(subType)) {
        name = optionalBenefits.getMaternity().get(limit);
      } else if (BenefitEnum.OUTPATIENT.getValue().equals(subType)) {
        if (isChildrenOnly)
          name = optionalBenefits.getOutpatient().getChildOnly().get(limit);
        else
          name = optionalBenefits.getOutpatient().getPerson().get(limit);
      }
    }
    return name;
  }

  private Quote getAndValidateQuote(String quoteId, String customerId, String agentId) {
    Quote quote = this.getQuote(quoteId, customerId, agentId);
    if (!PolicyStatus.NEW.getValue().equals(quote.getStatus())
            && !PolicyStatus.VIEWED.getValue().equals(quote.getStatus())
            && !PolicyStatus.ENQUIRY.getValue().equals(quote.getStatus())) {
      throw new BusinessException("can't update finished quote");
    }
    if (PolicyStatus.NEW.getValue().equals(quote.getStatus())) {
      quote.setStatus(PolicyStatus.ENQUIRY.getValue());
    }

    return quote;
  }

  private PolicyBeneficiary getPolicyBeneficiary(List<Customer> customerList, String quoteId) {
    PolicyBeneficiary policyBeneficiary = PolicyBeneficiary.builder().id(quoteId).build();
    List<Beneficiary> list = new ArrayList<>();
    Date startDate = new Date();
    for (Customer dependant : customerList) {
      Date dob = dependant.getDateOfBirth();
      String gender = dependant.getGender();
      String relationship = dependant.getRelationshipDesc();
      String name = dependant.getFirstName() + " " + dependant.getLastName();
      if (DependantRelationship.POLICY_HOLDER.getValue().equals(relationship)) {
        Beneficiary beneficiary = Beneficiary.builder()
                .customerId(dependant.getCustomerId()).entityId(dependant.getEntityId())
                .name(name).relationship(relationship)
                .age(HealthDateUtils.calculateAge(dob, startDate)).gender(gender).build();
        policyBeneficiary.setPrincipal(beneficiary);
      } else if (DependantRelationship.SPOUSE.getValue().equals(relationship)
              || DependantRelationship.PARTNER.getValue().equals(relationship)) {
        Beneficiary beneficiary = Beneficiary.builder()
                .customerId(dependant.getCustomerId()).entityId(dependant.getEntityId())
                .name(name).relationship(relationship)
                .age(HealthDateUtils.calculateAge(dob, startDate)).gender(gender).build();
        policyBeneficiary.setSpouse(beneficiary);
      } else if (
              DependantRelationship.UNMARRIED_CHILD.getValue().equals(relationship)
                      || DependantRelationship.MARRIED_CHILD.getValue()
                      .equals(relationship)) {
        Beneficiary beneficiary = Beneficiary.builder()
                .customerId(dependant.getCustomerId()).entityId(dependant.getEntityId())
                .name(name).relationship(relationship)
                .age(HealthDateUtils.calculateAge(dob, startDate)).gender(gender).build();
        list.add(beneficiary);
      }
    }
    policyBeneficiary.setChildren(list);
    return policyBeneficiary;

  }

  private PolicyBeneficiary getPolicyBeneficiary(CustomerDetailResponse customerDetail,
                                                 boolean isChildrenOnly) {
    Beneficiary principal = null;
    Beneficiary spouse = null;
    if (!isChildrenOnly) {
      principal = this
              .getBeneficiary(customerDetail, DependantRelationship.POLICY_HOLDER);
      spouse = this.getBeneficiary(customerDetail, DependantRelationship.SPOUSE);
    }
    List<Beneficiary> children = this.getChildrenBeneficiary(customerDetail);
    return PolicyBeneficiary.builder().id(customerDetail.getCustomerId()).principal(principal)
            .spouse(spouse).children(children)
            .build();

  }

  private Beneficiary getBeneficiary(CustomerDetailResponse customerDetail,
                                     DependantRelationship dependantRelationship) {
    Date dob = null;
    String gender = "";
    String relationship = dependantRelationship.getValue();
    String name = "";
    String customerId = "";
    Long entityId = null;
    if (DependantRelationship.POLICY_HOLDER.getValue().equals(relationship)) {
      Principal principal = customerDetail.getPrincipal();
      if (principal == null) {
        return null;
      }
      entityId = principal.getEntityId();
      customerId = principal.getCustomerId();
      dob = principal.getDateOfBirth();
      gender = principal.getGender();
      name = principal.getFirstName() + " " + principal.getLastName();
    } else if (DependantRelationship.SPOUSE.getValue().equals(relationship)) {
      Dependant spouse = customerDetail.getSpouse();
      if (spouse == null) {
        return null;
      }
      entityId = spouse.getEntityId();
      customerId = spouse.getDependantCode();
      dob = spouse.getDateOfBirth();
      gender = spouse.getGender();
      name = spouse.getFirstName() + " " + spouse.getLastName();
    }
    int age = HealthDateUtils.calculateAge(dob, new Date());
    return Beneficiary.builder().customerId(customerId).entityId(entityId)
            .name(name).relationship(relationship).age(age).gender(gender).build();
  }

  private List<Beneficiary> getChildrenBeneficiary(CustomerDetailResponse customerDetail) {
    List<Beneficiary> list = new ArrayList<>();

    Children children = customerDetail.getChildren();
    if (children == null) {
      return list;
    }

    int childNumber = children.getCount();
    List<Dependant> childrenDetail = children.getDetail();
    int childrenDetailSize = CollectionUtils.size(childrenDetail);
    if (childNumber > 0) {
      for (int i = 0; i < childNumber; i++) {
        Date dob = DateUtils.addYears(new Date(), -1);
        String customerId = "";
        Long entityId = null;
        String name = "";
        if (childrenDetailSize > i) {
          Dependant dependant = childrenDetail.get(i);
          dob = dependant.getDateOfBirth();
          customerId = dependant.getDependantCode();
          entityId = dependant.getEntityId();
          name = dependant.getFirstName() + " " + dependant.getLastName();
        }
        int age = HealthDateUtils.calculateAge(dob, new Date());
        list.add(
                Beneficiary.builder().customerId(customerDetail.getCustomerId()).customerId(customerId)
                        .entityId(entityId).name(name)
                        .relationship(DependantRelationship.UNMARRIED_CHILD.getValue()).age(age)
                        .build());
      }
    }
    return list;
  }

  @Override
  public Quote updateQuoteBenefit(QuoteBenefitUpdateRequest request) {
    String customerId = request.getCustomerId();
    String agentId = request.getAgentId();
    String quoteId = request.getQuoteId();
    Benefit benefit = request.getBenefit();
    logger.debug("update quote benefit, QuoteBenefitUpdateRequest: {}", request);

    Quote quote = this.getAndValidateQuote(quoteId, customerId, agentId);
    List<Customer> customerList = beneficiaryService.getQuoteBeneficiary(customerId, quoteId);
    logger.debug("update quote benefit, customerList: {}", customerList);
    PolicyBeneficiary policyBeneficiary = this.getPolicyBeneficiary(customerList, quoteId);

    if(null != policyBeneficiary.getSpouse()){
      if(policyBeneficiary.getPrincipal().getGender().equalsIgnoreCase("Male"))
        policyBeneficiary.getSpouse().setGender("Female");

      if(policyBeneficiary.getPrincipal().getGender().equalsIgnoreCase("Female"))
        policyBeneficiary.getSpouse().setGender("Male");
    }

    BigDecimal newPremium = productService.calculatePremium(policyBeneficiary, quote.getProductId(), benefit, false);
    // itl and phcf
    Premium premium = productService.calculateTotalPremium(newPremium, true);

    if(ProductEnum.JAMIIPLUS.getId() == quote.getProductId()){
      BigDecimal travelInsurancePremium =  productService.getTravelInsurancePremium(policyBeneficiary,benefit);
      logger.info(TRAVEL_INSURANCE_PREMIUM, travelInsurancePremium);
      premium.setPremium(premium.getPremium().add(travelInsurancePremium));
      premium.setTotalPremium(premium.getTotalPremium().add(travelInsurancePremium));
      logger.info("TotalPremium include travelInsurancePremium :{}", premium.getTotalPremium());
    }

    logger.info("ProductId: {}", quote.getProductId());
    String log = new Gson().toJson(premium);
    logger.info(BEFORE_PREMIUM, log);

    quote.setBenefit(benefit);
    quote.setPremium(premium);
    quote.setBalance(premium.getTotalPremium());

    quote.setUpdateTime(new Date());

    if (!this.updateQuotePremium(quote, policyBeneficiary)) {
      throw new BusinessException("Update quote benefit failed!");
    }

    stepRepository.save(HealthStepEntity
            .builder()
            .step(HealthQuoteStepsEnum.UPDATE_QUOTE_BENEFIT)
            .quoteOrPolicyId(quoteId)
            .agentId(agentId)
            .customerId(customerId)
            .build());

    return quote;
  }

  @Override
  public boolean updateQuotePremiumByCustomer(String quoteId, String customerId, String agentId) {
    Quote quote = this.getAndValidateQuote(quoteId, customerId, agentId);
    List<Customer> customerList = beneficiaryService.getQuoteBeneficiary(customerId, quoteId);
    PolicyBeneficiary policyBeneficiary = this.getPolicyBeneficiary(customerList, quoteId);
    policyBeneficiary.setId(quoteId);
    BigDecimal newPremium = productService.calculatePremium(policyBeneficiary, quote.getProductId(), quote.getBenefit(), false);
    Premium premium = productService.calculateTotalPremium(newPremium, true);
    String log = new Gson().toJson(premium);
    logger.info(BEFORE_PREMIUM, log);

    if(ProductEnum.JAMIIPLUS.getId() == quote.getProductId()){
      BigDecimal travelInsurancePremium =  productService.getTravelInsurancePremium(policyBeneficiary,quote.getBenefit());
      logger.info(TRAVEL_INSURANCE_PREMIUM, travelInsurancePremium);
      premium.setPremium(premium.getPremium().add(travelInsurancePremium));
      premium.setTotalPremium(premium.getTotalPremium().add(travelInsurancePremium));
    }
    String logPremium = new Gson().toJson(premium);
    logger.info("totalPremium (balance) {}", logPremium);
    logger.info("ProductId {}", quote.getProductId());

    quote.setPremium(premium);
    quote.setBalance(premium.getTotalPremium());
    quote.setUpdateTime(new Date());

    return this.updateQuotePremium(quote, policyBeneficiary);
  }

  @Override
  public Quote updateQuoteStartDate(QuoteStartDateUpdateRequest request) {
    String customerId = request.getCustomerId();
    String agentId = request.getAgentId();
    String quoteId = request.getQuoteId();
    Date startDate = request.getStartDate();
    logger.debug("update quote, QuoteStartDateUpdateRequest: {}", request);

    Quote quote = this.getAndValidateQuote(quoteId, customerId, agentId);
    quote.setStartDate(startDate);
    quote.setEffectiveDate(startDate);
    quote.setRenewalDate(HealthDateUtils.nextYear(startDate));
    quote.setUpdateTime(new Date());

    if (quoteMapper.update(quote) != 1) {
      throw new BusinessException("Update quote start date failed!");
    }
    return quote;
  }

  @Override
  @Transactional("healthDataTransactionManager")
  public Quote finishQuote(QuoteFinishRequest request) {
    String customerId = request.getCustomerId();
    String agentId = request.getAgentId();
    String quoteId = request.getQuoteId();
    logger.info("finish quote, QuoteFinishRequest: {}", request);

    Quote quote = this.getQuote(quoteId, customerId, agentId);
    if (!PolicyStatus.NEW.getValue().equals(quote.getStatus()) && !PolicyStatus.VIEWED.getValue()
            .equals(quote.getStatus()) && StringUtils.isNotBlank(quote.getExtPolicyNumber())
            && quote.getExtPolicyId() != null) {
      return quote;
    }

    long startTime = System.currentTimeMillis();
    Long entityId = customerService.addClientAndDependantToBase(quote.getCustomerId(), quote.getId());
    logger.info("Entity id is {}", entityId);
    long endTime = System.currentTimeMillis();
    logger.info("0. create customer duration: {} ms", endTime - startTime);

    if (entityId == null) {
      throw new BusinessException("create customer failed, customer entity id is null!");
    }

    if ("1".equals(request.getStep())) {
      logger.info("finish quote step 1 done, quoteId: {}", quote.getId());
      return quote;
    }
    quote.setStatus(PolicyStatus.APPLICATION.getValue());
    quote.setEntityId(entityId);

    // create policy
    this.createPolicy(quote);
    quote.setUpdateTime(new Date());

    long endTime1 = System.currentTimeMillis();
    String fromTimeStr = DateFormatUtils
            .format(new Date(endTime), GlobalConstant.YYYYMMDD_HHSSMMSSS);
    String endTimeStr = DateFormatUtils
            .format(new Date(endTime1), GlobalConstant.YYYYMMDD_HHSSMMSSS);
    logger
            .info("finish quote done, start_time: {}, end_time:{}, duration: {} ms", fromTimeStr,
                    endTimeStr, endTime1 - endTime);

    if (quoteMapper.update(quote) != 1) {
      throw new BusinessException("Finish quote failed!");
    }

    Customer customer = customerService.getCustomer(customerId);
    if (customer != null) {
      Date today = new Date();
      String text = notificationMessageBuilder
              .getMessage("SMS_MESSAGE_POLICY_NO_PAY", quote.getCode());
      PolicyNotificationTask task = PolicyNotificationTask.builder().text(text)
              .destination(customer.getPhoneNumber()).type(NotificationType.SMS.getValue())
              .subtype(SMSTaskTypeEnum.NO_PAY.getValue())
              .status(TaskStatusEnum.PENDING.getValue()).failureNumber(0)
              .policyNumber(quote.getExtPolicyNumber())
              .scheduleTime(today).createTime(today).build();
      policySMSEventPublisher.publishTask(task);
      PolicyNotificationTask nextTask = PolicyNotificationTask.builder().build();
      BeanUtils.copyProperties(task, nextTask);
      nextTask.setStatus(TaskStatusEnum.TODO.getValue());
      nextTask.setScheduleTime(HealthDateUtils.nextDay(today));
      policySMSEventPublisher.publishTask(nextTask);
    }
    logger.info("\n ==== created step {} : for quote {} ==== \n " , HealthQuoteStepsEnum.PAYMENT_SUMMARY, quoteId);
    stepRepository.save(HealthStepEntity
            .builder()
            .quoteOrPolicyId(quoteId)
            .agentId(agentId)
            .customerId(customerId)
            .step(HealthQuoteStepsEnum.PAYMENT_SUMMARY)
            .build());
    logger.info("finish quote done, quoteId: {}", quote.getId());
    return quote;
  }

  @Override
  public boolean sendPolicySMSNotification(PolicyNotificationTask task) {
    boolean result = false;
    if (TaskStatusEnum.PENDING.getValue().equals(task.getStatus())) {
      result = notificationRemote.sendPolicyInstantSMSNotification(task);
    } else if (TaskStatusEnum.TODO.getValue().equals(task.getStatus())) {
      result = notificationRemote.createPolicySMSTask(task);
    } else if (TaskStatusEnum.CANCEL.getValue().equals(task.getStatus())) {
      result = notificationRemote.cancelPolicySMSTask(task);
    }
    return result;
  }

  private void createPolicy(Quote quote) {

    long startTime = System.currentTimeMillis();
    // 1. create policy
    CreatePolicyResponse createPolicyResponse = this.createBasePolicy(quote);
    logger.debug("create base policy, createPolicyResponse: {}", createPolicyResponse);
    long endTime2 = System.currentTimeMillis();
    logger.info("1. create base policy duration: {} ms", endTime2 - startTime);

    if (createPolicyResponse != null) {
      quote.setExtPolicyId(createPolicyResponse.getPolicyId());
      quote.setExtPolicyNumber(createPolicyResponse.getPolicyNumber());
      // 2. add benefit to policy
      this.addBenefitToPolicy(quote, createPolicyResponse.getPolicyId());
      long endTime3 = System.currentTimeMillis();
      logger.info("2. add benefit to policy duration: {} ms", endTime3 - endTime2);
      // 3. add intermediary to policy
      this.addIntermediaryToPolicy(quote);
      long endTime4 = System.currentTimeMillis();
      logger.info("3. add intermediary to policy duration: {} ms", endTime4 - endTime3);

      // 4. add beneficiary to policy
      boolean success = beneficiaryService
              .addBeneficiaryToBase(quote.getCustomerId(), quote.getId(),
                      quote.getExtPolicyId(),
                      DateFormatUtils.format(quote.getEffectiveDate(), GlobalConstant.YYYYMMDD));
      if (!success) {
        throw new BusinessException("add beneficiary to policy exception!");
      }
      long endTime5 = System.currentTimeMillis();
      logger.info("4. add beneficiary to policy duration: {} ms", endTime5 - endTime4);

      // 5. add beneficiary questions to policy
      CompletableFuture.runAsync(MdcConfig.withMdc(() -> this.addBeneficiaryUWQuestions(quote)));
      long endTime6 = System.currentTimeMillis();
      logger.info("5. add beneficiary questions to policy duration: {} ms", endTime6 - endTime5);

    } else {
      throw new BusinessException("create policy exception!");
    }
  }

  @Override
  public boolean testAddingBeneficiaryUWQuestions(QuoteBaseRequest request) {
    String customerId = request.getCustomerId();
    String agentId = request.getAgentId();
    String quoteId = request.getQuoteId();
    Quote quote = this.getQuote(quoteId, customerId, agentId);
    return this.addBeneficiaryUWQuestions(quote);
  }

  @Override
  public QuoteStepResponse getQuoteStep(QuoteStepRequest request) {
    HealthStepEntity step = stepRepository
            .findById(request.getQuoteOrPolicy())
            .orElse(new HealthStepEntity());
    return QuoteStepResponse
            .builder()
            .quoteOrPolicy(step.getQuoteOrPolicyId())
            .customerId(step.getCustomerId())
            .agentId(step.getAgentId())
            .step(step.getStep())
            .quoteOrPolicy(step.getQuoteOrPolicyId())
            .build();
  }

  @Override
  public boolean addBeneficiaryUWQuestions(Quote quote) {
    String customerId = quote.getCustomerId();
    String quoteId = quote.getId();
    List<Customer> customerList = customerService
            .getCustomerAndDependants(customerId, quoteId);
    if (CollectionUtils.isNotEmpty(customerList)) {
      Map<String, Long> entityMap = customerList.stream().collect(Collectors.toMap(
              Customer::getCustomerId, Customer::getEntityId));
      QuoteBaseRequest quoteBaseRequest = QuoteBaseRequest.builder()
              .customerId(customerId).quoteId(quoteId).build();
      QuoteQuestion quoteQuestion = questionService.getQuoteQuestion(quoteBaseRequest);
      if (quoteQuestion != null) {
        List<Members> membersList = quoteQuestion.getMembers();
        if (CollectionUtils.isEmpty(membersList)) {
          logger.warn("can't find the question for quote: {}", quoteId);
          return false;
        }
        for (Members members : membersList) {
          Long entityId = entityMap.get(members.getCode());
          if (entityId != null) {
            this.sendBeneficiaryUWQuestions(entityId, quote, members.getQuestions());
          }
        }
      }
    }

    return true;
  }

  private boolean sendBeneficiaryUWQuestions(Long entityId, Quote quote,
                                             List<Questions> memberQuestionList) {
    List<UnderwritingQuestions> questionsList = new ArrayList<>();
    Map<String, String> questionMap = questionConfig.getQuestionMap();
    for (Map.Entry<String, String> entry : questionMap.entrySet()) {
      UnderwritingQuestions questions = UnderwritingQuestions.builder()
              .Question(entry.getValue()).Answer("No").DiscountApplicable(false).FreeFormatAnswer("")
              .build();
      for (Questions question : memberQuestionList) {
        if (entry.getKey().equals(question.getQuestionId())) {
          questions.setAnswer("Yes");
          String content = question.getContent();
          if (StringUtils.isNotBlank(question.getDoctorName())) {
            content = content + "- " + question.getDoctorName();
          }
          questions.setFreeFormatAnswer(content);
          break;
        }
      }
      questionsList.add(questions);
    }
    BeneficiaryUWQuestions beneficiaryUWQuestions = BeneficiaryUWQuestions.builder()
            .PolicyId(Long.valueOf(quote.getExtPolicyId())).PolicyEffectiveDate(
                    DateFormatUtils.format(quote.getEffectiveDate(), GlobalConstant.YYYYMMDD))
            .EntityId(entityId).UnderwritingQuestions(questionsList)
            .build();
    AddIndividualPolicyBeneficiaryUWQuestionsRequest questionsRequest = AddIndividualPolicyBeneficiaryUWQuestionsRequest
            .builder().BeneficiaryUnderwritingQuestions(beneficiaryUWQuestions).build();
    ApiResponse response = policyRemote.addBeneficiaryUWQuestions(questionsRequest);
    String msg = this.checkApiResponse(response);
    if (StringUtils.isNotBlank(msg)) {
      throw new BusinessException("add beneficiary questions to policy exception: " + msg);
    } else {
      logger.info("add beneficiary questions to policy successfully");
    }
    return true;
  }

  @Override
  public boolean addIntermediaryToPolicy(String quoteId, String customerId, String agentId) {
    Quote quote = this.getQuote(quoteId, customerId, agentId);
    return this.addIntermediaryToPolicy(quote);
  }

  @Override
  public boolean addIntermediaryToPolicy(Quote quote) {
    if (StringUtils.isNotEmpty(quote.getAgentId())) {
      Integer agentEntityId = intermediaryService.getIntermediaryEntityId(quote.getAgentId());
      if (agentEntityId != null) {
        this.addBusinessSourceToPolicy(quote, agentEntityId);
        this.addIntermediaryBranchDetails(quote,agentEntityId);
      } else {
        logger.warn("No agent entity id");
      }
    }

    return true;
  }
  @Override
  public boolean addIntermediaryBranchDetails(Quote quote, Integer entityId ) {
    AgentDetailsRequest request = AgentDetailsRequest.builder().entityId(entityId).build();
    AgentBranchDetailsResponse response = policyRemote.getAgentBranchDetails(request);
    List<PolicyAdditionalInfoList>  list = new ArrayList<>();
    PolicyAdditionalInfoList policyAdditionalInfoLists = PolicyAdditionalInfoList
            .builder()
            .key("APA Branch")
            .value(response.getBranch())
            .build();
    list.add(policyAdditionalInfoLists);
    AgentBranchEntity agentBranchEntity = AgentBranchEntity
            .builder()
            .agentId(quote.getAgentId())
            .entityId(entityId)
            .branchName(response.getBranch())
            .build();
    agentBranchRepository.save(agentBranchEntity);
    PolicyAdditionalInfoRequest policyAdditionalInfoRequest =
            PolicyAdditionalInfoRequest
                    .builder()
                    .policyId(quote.getExtPolicyId())
                    .policyEffectiveDate(DateFormatUtils.format(quote.getEffectiveDate(),"yyyy-MM-dd"))
                    .policyAdditionalInfoList(list)
                    .build();
    ActisurePolicyBranchDetailsResponse actisurePolicyBranchDetailsResponse = policyRemote.addPolicyAdditionalBranchDetails(policyAdditionalInfoRequest);
    return actisurePolicyBranchDetailsResponse.isSuccess();
  }

  @Override
  public List<Quote> getQuoteList(QuoteListRequest quoteListRequest) {
    return quoteMapper.getQuotes(quoteListRequest.getCustomerId(), quoteListRequest.getAgentId(), quoteListRequest.getProductId());
  }

  @Override
  public HealthQuoteListResponse searchQuoteList(QuoteListSearchRequest quote) {
    if (null ==  quote.getQuoteStatus()){
      quote.setQuoteStatus(QuoteStatusEnum.ACTIVE);
    }
    String agentId = quote.getAgentId();
    List<String> agentIds = new ArrayList<>();
    agentIds.add(agentId);
    Intermediary intermediary = intermediaryService.getIntermediary(agentId);
    if (IntermediaryRole.ADMIN.getValue().equals(intermediary.getRole())) {
      List<Intermediary> intermediaryList = intermediaryService.getUserList(agentId);
      if (CollectionUtils.isNotEmpty(intermediaryList)) {
        agentIds.addAll(
                intermediaryList.stream().map(Intermediary::getAgentId).collect(Collectors.toList()));
      }
    }

    String sortColumn = "";
    String sortType = quote.getSortType();
    if (StringUtils.isNotBlank(sortType)) {
      sortColumn = sortTypeMap.get(sortType);
      if (sortColumn == null) {
        throw new BusinessException("invalid sort type");
      }
    }

    String sort = quote.getSort();
    if (StringUtils.isNotBlank(sort) && !"asc".equalsIgnoreCase(sort) && !"desc"
            .equalsIgnoreCase(sort)) {
      throw new BusinessException("invalid sort value, should be 'asc' or 'desc'");
    }

    /*
     * Frontend paging from page 1... which is indexed to 0
     * */
    if(quote.getIndex() > 0){
      quote.setIndex(quote.getIndex()-1);
    }

    quote.setIndex(quote.getIndex()*quote.getLimit());
    logger.info("----> Page is {}", quote.getIndex());

    QuoteListSearchFilter filter = QuoteListSearchFilter.builder().agentIds(agentIds)
            .filter(quote.getFilter()).sortColumn(sortColumn).quoteStatus(quote.getQuoteStatus().getValue())
            .sort(sort).index(quote.getIndex()).limit(quote.getLimit()).hide(quote.getHide()).build();

    logger.debug("QuoteListSearchFilter {}", filter);
    List<HealthQuote> quotes = quoteMapper.searchQuotes(filter);
    Integer count = quoteMapper.searchQuotesCount(filter);
    logger.debug("Quotes --->  {}", quotes);
    return HealthQuoteListResponse.builder().list(quotes).total(count).build();
  }

  @Override
  public List<Quote> getCustomerQuoteList(QuoteListRequest quote) {
    return quoteMapper.getCustomerQuotes(quote.getCustomerId(), quote.getAgentId(),
            PolicyStatus.APPLICATION.getValue());
  }


  @Override
  public Quote getQuote(String quoteId, String customerId, String agentId) {
    Quote quote = getQuoteNoThrowException(quoteId, customerId, agentId);
    if (quote == null) {
      throw new BusinessException("can't find the quote");
    }
    return quote;
  }

  @Override
  public Quote getQuoteNoThrowException(String quoteId, String customerId, String agentId) {
    return quoteMapper.getQuote(quoteId, customerId, agentId);
  }

  @Override
  public Quote getQuoteByPolicyNumber(String customerId, String policyNumber) {
    Quote quote = quoteMapper.getQuoteByPolicyNumber(customerId, policyNumber);
    if (quote == null) {
      throw new BusinessException("can't find the policy");
    }
    return quote;
  }

  @Override
  public boolean deleteQuote(QuoteDeleteRequest quote) {
    if (quote == null) {
      throw new BusinessException("delete quote exception!");
    }

    return quoteMapper.delete(quote.getQuoteId(), quote.getCustomerId(), quote.getAgentId()) > 0;
  }

  @Override
  public boolean softDeleteQuoteByAgent(SoftDeleteQuoteByAgentRequest quote) {
    if (quote == null) {
      throw new BusinessException("Quote cannot be null!");
    }
    return quoteMapper.updateQuotStatusToDeleted(quote) > 0;
  }

  @Override
  public List<IdAndHideList> getIdAndHideResult(IdsRequest quote) {
    return quoteMapper.getIdAndHideResult(quote.getQuoteIds());
  }

  @Override
  public boolean hideQuote(QuoteAgentHideRequest quote) {
    if (quote == null) {
      throw new BusinessException("hide quote exception!");
    }
    return quoteMapper.hideQuote(true, quote.getQuoteId()) > 0;
  }

  @Override
  public boolean deleteQuoteByCustomerId(String customerId) {
    if (customerId == null) {
      throw new BusinessException("customer is null!");
    }
    return quoteMapper.deleteQuoteByCustomerId(customerId) > 0;
  }


  @Override
  public boolean softDeleteQuoteByCustomerId(String customerId) {
    if (customerId == null) {
      throw new BusinessException("Customer is null!!");
    }
    return quoteMapper.softDeleteQuoteByCustomerId(customerId) > 0;
  }

  @Override
  @Transactional("healthDataTransactionManager")
  public List<Quote> createInitQuote(String customerId, String agentId, String quoteId, boolean isChildrenOnly,int productId) {
    List<Quote> quoteList = new ArrayList<>();

    CustomerDetailResponse customerDetail = customerService.getCustomer(
            CustomerSearchRequest.builder()
                    .customerId(customerId)
                    .build());
    PolicyBeneficiary policyBeneficiary = this.getPolicyBeneficiary(customerDetail, isChildrenOnly);

    if(null != policyBeneficiary.getSpouse()) {
      if (policyBeneficiary.getPrincipal().getGender().equalsIgnoreCase("Male"))
        policyBeneficiary.getSpouse().setGender("Female");

      if (policyBeneficiary.getPrincipal().getGender().equalsIgnoreCase("Female"))
        policyBeneficiary.getSpouse().setGender("Male");
    }

    if(!customerId.isEmpty()){
      quoteMapper.deleteDependentBenefits(customerId,agentId);
    }
    // Jamii Plus
    // Whether it's shared on individual is determined by
    // product id submitted. Default is 49 (Individual)
    logger.info("===== product by id {} is {}====", productId, ProductEnum.getById(productId));
    Benefit jpBenefit = productService.createDefaultBenefit(customerDetail, ProductEnum.getById(productId));
    BigDecimal jpPremium = productService.calculatePremium(policyBeneficiary, productId, jpBenefit, false);
    Premium premium = productService.calculateTotalPremium(jpPremium, true);

    BigDecimal travelInsurancePremium =  productService.getTravelInsurancePremium(policyBeneficiary,jpBenefit);

    logger.info(TRAVEL_INSURANCE_PREMIUM, travelInsurancePremium);

    premium.setPremium(premium.getPremium().add(travelInsurancePremium));
    premium.setTotalPremium(premium.getTotalPremium().add(travelInsurancePremium));
    String logP = new Gson().toJson(premium);
    logger.info("totalPremium balance add travelInsurancePremium: {}", logP);
    Date startDate = HealthDateUtils.nextDay(new Date());
    Quote jpQuote = Quote.builder()
            .id(quoteId)
            .agentId(agentId)
            .customerId(customerId)
            .productId(productId)
            .startDate(startDate)
            .effectiveDate(startDate)
            .renewalDate(HealthDateUtils.nextYear(startDate))
            .benefit(jpBenefit)
            .premium(premium)
            .balance(premium.getTotalPremium())
            .isChildrenOnly(isChildrenOnly)
            .status(PolicyStatus.NEW.getValue())
            .createTime(new Date())
            .build();
    int count = 0;

    if (quoteId.isEmpty()){
      count = quoteMapper.insert(jpQuote);
    }else {
      count = quoteMapper.update(jpQuote);
    }
    if (count > 0) {
      jpQuote.setCode(this.generateQuoteNumber(jpQuote.getProductId(), jpQuote.getId()));
      quoteMapper.update(jpQuote);
      jpQuote.setId(jpQuote.getId());
      quoteList.add(jpQuote);
    }
    logger.info("\n ==== created step {} : for quote {} ==== \n " , HealthQuoteStepsEnum.CREATE_QUOTE, jpQuote.getId());
    stepRepository.save(HealthStepEntity
            .builder()
            .quoteOrPolicyId(jpQuote.getId())
            .agentId(agentId)
            .customerId(customerId)
            .step(HealthQuoteStepsEnum.CREATE_QUOTE)
            .build());
    // AfyaNafuu
    Benefit afBenefit = productService.createDefaultBenefit(customerDetail, ProductEnum.AFYANAFUU);

    BigDecimal afPremium = productService.calculatePremium(policyBeneficiary, ProductEnum.AFYANAFUU.getId(), afBenefit, false);

    Premium premium1 = productService.calculateTotalPremium(afPremium, true);

    Quote afQuote = Quote.builder()
            .agentId(agentId)
            .customerId(customerId)
            .productId(ProductEnum.AFYANAFUU.getId())
            .benefit(afBenefit)
            .premium(premium1)
            .balance(premium1.getTotalPremium())
            .status(PolicyStatus.NEW.getValue())
            .isChildrenOnly(isChildrenOnly)
            .createTime(new Date())
            .build();
    count = quoteMapper.insert(afQuote);
    if (count > 0) {
      afQuote.setCode(this.generateQuoteNumber(afQuote.getProductId(), afQuote.getId()));
      quoteMapper.update(afQuote);
      afQuote.setId(afQuote.getId());
      quoteList.add(afQuote);
    }

    //Femina
    Benefit faBenefit = productService.createDefaultBenefit(customerDetail, ProductEnum.FEMINA);
    BigDecimal faPremium = productService.calculatePremium(policyBeneficiary, ProductEnum.FEMINA.getId(), faBenefit, false);
    Premium premium2 = productService.calculateTotalPremium(faPremium, true);
    Quote feminaQuote = Quote.builder()
            .agentId(agentId)
            .customerId(customerId)
            .productId(ProductEnum.FEMINA.getId())
            .benefit(faBenefit)
            .premium(premium2)
            .balance(premium2.getTotalPremium())
            .status(PolicyStatus.NEW.getValue())
            .isChildrenOnly(isChildrenOnly)
            .createTime(new Date())
            .build();
    count = quoteMapper.insert(feminaQuote);
    if (count > 0) {
      feminaQuote
              .setCode(this.generateQuoteNumber(feminaQuote.getProductId(), feminaQuote.getId()));
      quoteMapper.update(feminaQuote);
      feminaQuote.setId(feminaQuote.getId());
      quoteList.add(feminaQuote);
    }

    return quoteList;
  }

  private String getQuoteNumberPrefix(int productId) {
    String result = "";

    switch (productId) {
      case 49:
        result = QuoteNumberEnum.HJ.getValue();
        break;
      case 50:
        result = QuoteNumberEnum.HA.getValue();
        break;
      case 51:
        result = QuoteNumberEnum.HF.getValue();
        break;
      case 52:
        result = QuoteNumberEnum.HS.getValue();
        break;
      default:
    }
    return result;
  }

  private String generateQuoteNumber(int productId, String quoteId) {
    return this.getQuoteNumberPrefix(productId) + "-" + EncodeUtils.crc32(quoteId).toUpperCase();
  }

  @Override
  public ApplicationQuote getApplicationQuote(QuoteBaseRequest request) {
    return quoteMapper.searchApplicationQuote(request.getQuoteId(), request.getCustomerId());
  }

  @Override
  public ApplicationQuoteListResponse searchApplicationQuoteList(
          ApplicationQuoteListSearchRequest request) {

    String sortColumn = "";
    String sortType = request.getSortType();
    if (StringUtils.isNotBlank(sortType)) {
      sortColumn = applicationQuoteSortTypeMap.get(sortType);
      if (sortColumn == null) {
        throw new BusinessException("invalid sort type");
      }
    }

    String sort = request.getSort();
    if (StringUtils.isNotBlank(sort) && !"asc".equalsIgnoreCase(sort) && !"desc"
            .equalsIgnoreCase(sort)) {
      throw new BusinessException("invalid sort value, should be 'asc' or 'desc'");
    }

    ApplicationQuoteListSearchFilter filter = ApplicationQuoteListSearchFilter.builder()
            .filter(request.getFilter()).archived(BooleanUtils.toInteger(request.isArchived()))
            .paid(request.isPaid()).sortColumn(sortColumn).sort(sort).build();
    return CompletableFuture
            .supplyAsync(MdcConfig.mdcSupplier(() -> {
              int index = request.getIndex();
              int limit = request.getLimit();
              if (index > 0 && limit > 0) {
                PageMethod.startPage(request.getIndex(), request.getLimit());
              }
              return quoteMapper.searchApplicationQuotes(filter);
            }))
            .thenCombine(CompletableFuture
                            .supplyAsync(MdcConfig.mdcSupplier(() -> quoteMapper.searchApplicationQuotesCount(filter))),
                    (list, count) -> ApplicationQuoteListResponse.builder().list(list).total(count).build()
            ).exceptionally(e -> {
              logger.error(" search quote list failed, error: {}", e.getMessage());
              return null;
            }).join();
  }

  @Override
  public boolean archiveApplicationQuote(QuoteBaseRequest request) {
    Quote quote = this
            .getQuote(request.getQuoteId(), request.getCustomerId(), request.getAgentId());
    quote.setArchived(true);
    quote.setUpdateTime(new Date());
    return quoteMapper.archiveApplicationQuote(quote) == 1;
  }

  private boolean updateQuotePremium(Quote quote, PolicyBeneficiary policyBeneficiary) {
    boolean success = quoteMapper.update(quote) == 1;
    if (success) {
      String logQut = new Gson().toJson(quote);
      logger.trace("updateQuotePremium quote: {}", logQut);
      String logBeneficiary  = new Gson().toJson(policyBeneficiary);
      logger.trace("updateQuotePremium policyBeneficiary: {}", logBeneficiary);
      premiumService.recordQuoteBeneficiaryPremium(quote, policyBeneficiary);
    }
    return success;
  }

  @Override
  public String searchQuoteByPolicyId(Integer policyId, String policyNumber) {
    return quoteMapper.searchQuoteByPolicyId(policyId, policyNumber);
  }

  @Override
  @Transactional("healthDataTransactionManager")
  public boolean createActivedQuoteNotificationTask() {
    List<HealthQuote> quoteList = quoteMapper.getQuotesByStatus(PolicyStatus.UNDERWRITING.getValue());
    if (CollectionUtils.isNotEmpty(quoteList)) {
      List<Integer> policyIds = quoteList.stream().map(HealthQuote::getExtPolicyId).collect(Collectors.toList());
      Map<Integer, List<Policy>> policyListMap = policyRemote.getBatchPolicyListsById(PolicyIdsRequest.builder().ids(policyIds).build());
      if (MapUtils.isNotEmpty(policyListMap)) {
        quoteList.forEach(healthQuote -> {
          if (CollectionUtils.isNotEmpty(policyListMap.get(healthQuote.getExtPolicyId()))) {
            Quote quote = quoteMapper.getQuote(healthQuote.getId(), healthQuote.getCustomerId(), healthQuote.getAgentId());
            quote.setStatus(PolicyStatus.LIVE.getValue());
            quote.setUpdateTime(new Date());
            boolean success = quoteMapper.update(quote) == 1;
            if (success) {
              String productName = ProductEnum.getById(healthQuote.getProductId()).getValue();
              String customerFirstName =  StringUtils.isNotBlank(healthQuote.getFirstName()) ? healthQuote.getFirstName() : "Customer";
              String text = notificationMessageBuilder.getMessage("SMS_MESSAGE_POLICY_ACTIVE",customerFirstName, productName, healthQuote.getExtPolicyNumber());
              Date today = new Date();
              PolicyNotificationTask task = PolicyNotificationTask.builder()
                      .text(text)
                      .destination(healthQuote.getPhoneNumber())
                      .type(NotificationType.SMS.getValue())
                      .subtype(SMSTaskTypeEnum.ACTIVE.getValue())
                      .status(TaskStatusEnum.PENDING.getValue())
                      .failureNumber(0)
                      .policyNumber(quote.getExtPolicyNumber())
                      .scheduleTime(today)
                      .createTime(today)
                      .build();
              policySMSEventPublisher.publishTask(task);
            }
          }
        });
      }

    }
    return false;
  }

  @Override
  public byte[] downloadQuote(HealthQuoteDownloadRequest request) throws IOException, TemplateException {
    String customerId = request.getCustomerId();
    Customer customer = customerService.getCustomer(request.getCustomerId());
    Quote quote = quoteMapper.getQuoteByCode(customerId, request.getQuoteCode());
    StringWriter stringWriter = new StringWriter();
    Map<String, Object> model = new HashMap<>();
    model.put("holderName", customer.getFirstName().toUpperCase() + " " + customer.getLastName().toUpperCase());
    String quoteNumber = (quote.getCode() != null && !quote.getCode().isEmpty()) ? quote.getCode() : "N/A";
    model.put("quoteNumber", quoteNumber);
    model.put("dateOfBirth","N/A");
    if(customer.getDateOfBirth() != null) {
      model.put("dateOfBirth", new SimpleDateFormat("dd/MM/yyyy").format(customer.getDateOfBirth()));
    }
    populateDependantDetails(model, customer.getSpouseSummary(), customer.getChildrenSummary());
    model.put("benefitValues", populateBenefitDetails(quote));
    model.put("premium", quote.getPremium().getTotalPremium());
    model.put("travel", "Not Selected");
    if (quote.getBenefit().getTravelInsurance() != null && quote.getBenefit().getTravelInsurance() > 0) {
      model.put("travel", "YES");
    }
    populateAgentDetails(model,customer);
    Template temp = configuration.getTemplate("download_quote_pdf_template.ftlh");
    temp.process(model, stringWriter);
    String template = stringWriter.getBuffer().toString();
    ByteArrayOutputStream target = new ByteArrayOutputStream();
    HtmlConverter.convertToPdf(template, target);
    byte[] bytes = target.toByteArray();
    return Base64.getEncoder().encode(bytes);
  }

  public void populateDependantDetails(Map<String, Object> model,Dependant dependant, Children children){
    model.put("isSpouse", "NO");
    model.put("spouseDateOfBirth", "N/A");
    if(dependant != null) {
      model.put("isSpouse", "YES");
      if(dependant.getDateOfBirth()!=null) {
        String dob = new SimpleDateFormat("dd/MM/yyyy").format(dependant.getDateOfBirth());
        model.put("spouseDateOfBirth", dob);
      }
    }
    model.put("noOfChildren", "0");
    if(children != null) {
      model.put("noOfChildren", children.getCount());
    }
  }

  public void populateAgentDetails(Map<String, Object> model, Customer customer) {
    model.put(AGENT_NAME, "N/A");
    model.put(AGENT_BRANCH, "N/A");
    if(customer.getAgentId() != null) {
      Intermediary intermediary = intermediaryService.getIntermediary(customer.getAgentId());
      if (intermediary != null) {
        model.put(AGENT_NAME, intermediary.getFirstName().toUpperCase() + " " + intermediary.getLastName().toUpperCase());
        String agentBranch = (intermediary.getBranchName() != null && !intermediary.getBranchName().isEmpty()) ? intermediary.getBranchName().toUpperCase() : "N/A";
        model.put(AGENT_BRANCH, agentBranch);
      }}
  }

  public Benefit populateBenefitDetails(Quote quote){
    return Benefit.builder()
            .inpatientLimit(quote.getBenefit().getInpatientLimit() == null ? 0 : quote.getBenefit().getInpatientLimit())
            .outpatientLimit(quote.getBenefit().getOutpatientLimit() == null ? 0 : quote.getBenefit().getOutpatientLimit())
            .dentalLimit(quote.getBenefit().getDentalLimit() == null ? 0 : quote.getBenefit().getDentalLimit())
            .opticalLimit(quote.getBenefit().getOpticalLimit() == null ? 0 : quote.getBenefit().getOpticalLimit())
            .maternityLimit(quote.getBenefit().getMaternityLimit() == null ? 0 : quote.getBenefit().getMaternityLimit())
            .build();
  }

}
