package ke.co.apollo.health.service.impl;

import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.*;
import ke.co.apollo.health.common.domain.model.remote.PolicyIdRequest;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.common.domain.model.response.ApplicationRenewalPolicyListResponse;
import ke.co.apollo.health.common.domain.model.response.PolicyOverComingResponseDto;
import ke.co.apollo.health.common.domain.model.response.PolicyRenewalResponse;
import ke.co.apollo.health.common.enums.DependantRelationship;
import ke.co.apollo.health.common.enums.PolicyStatus;
import ke.co.apollo.health.common.enums.ProductEnum;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.common.utils.HealthDateUtils;
import ke.co.apollo.health.common.utils.JsonUtils;
import ke.co.apollo.health.config.MdcConfig;
import ke.co.apollo.health.config.PolicyRenewalDaysConfig;
import ke.co.apollo.health.config.PolicyRenewalExecutorConfiguration;
import ke.co.apollo.health.config.PolicyStatusConfig;
import ke.co.apollo.health.domain.ApplicationPolicyListSearchFilter;
import ke.co.apollo.health.domain.PolicyBeneficiary;
import ke.co.apollo.health.domain.PolicyBeneficiary.Beneficiary;
import ke.co.apollo.health.domain.PolicyClaim;
import ke.co.apollo.health.domain.entity.PolicyOverComingEntity;
import ke.co.apollo.health.domain.request.ApplicationPolicyListSearchRequest;
import ke.co.apollo.health.domain.request.CustomerIdRequest;
import ke.co.apollo.health.domain.request.CustomerSearchRequest;
import ke.co.apollo.health.domain.request.EmailAttachmentBytesDto;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import ke.co.apollo.health.domain.response.HealthPolicyListResponse;
import ke.co.apollo.health.event.ReminderEventPublisher;
import ke.co.apollo.health.feign.NotificationClient;
import ke.co.apollo.health.mapper.health.PolicyMapper;
import ke.co.apollo.health.mapper.health.QuoteMapper;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.repository.PolicyOverComingRepository;
import ke.co.apollo.health.service.*;
import ke.co.apollo.health.utils.GenericExcelFileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PolicyServiceImpl implements PolicyService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private static final Map<String, String> applicationPolicySortTypeMap = ImmutableMap
      .of("createdTime", "policy.create_time", "policyNumber", "policy.policy_number");

  @Autowired
  PolicyRemote policyRemote;

  @Autowired
  CustomerService customerService;

  @Autowired
  PolicyMapper policyMapper;

  @Autowired
  QuoteService quoteService;

  @Autowired
  ProductService productService;

  @Autowired
  PremiumService premiumService;

  @Autowired
  ReminderEventPublisher reminderEventPublisher;

  @Autowired
  private PolicyOverComingRepository policyOverComingRepository;
  @Autowired
  GenericExcelFileUtils fileUtils;
  @Autowired
  PolicyRenewalDaysConfig policyRenewalDaysConfig;
  @Autowired
  private NotificationClient notificationClient;

  @Autowired
  PolicyRenewalExecutorConfiguration renewalExecutorConfiguration;

  @Value("${business.email.recipient.address}")
  String destinationEmail;

  @Value("${business.email.renewal.template}")
  String emailBody;

  @Autowired
  QuoteMapper quoteMapper;

  @Autowired
  PolicyStatusConfig policyStatusConfig;

  public PolicyOverComingResponseDto policyUpdateDetails(ComingPolicyListRequest request) {
    Pageable pageable = PageRequest.of(request.getIndex() == 0 ? request.getIndex() : request.getIndex() - 1, request.getLimit());
    Date now = new Date();

    Date startDate =  request.getStartDate() == null  ? DateUtils.addMonths(now, -1) : request.getStartDate();
    Date endDate = request.getEndDate() == null ? DateUtils.addMonths(now, 2) : request.getEndDate();
    Page<PolicyOverComingEntity> comingEntityPage = policyOverComingRepository.findAllByRenewalDateBetween(startDate, endDate, pageable);
    List<PolicyOverComingDto> policyOverComingDtos;
    policyOverComingDtos = comingEntityPage.get().map(o ->
            {
              PolicyOverComingDto policy = new PolicyOverComingDto();
              BeanUtils.copyProperties(o, policy);
              return policy;
            }
    ).collect(Collectors.toList());
    return PolicyOverComingResponseDto.builder().total(comingEntityPage.getTotalPages() * request.getLimit()).policyOverComingListDto(policyOverComingDtos).build();
  }

  @Override
  public byte[] comingPolicyListInExcel(ComingPolicyListRequestInExcel request) {
    List<PolicyOverComingEntity> comingEntityPage = policyOverComingRepository.findAllByRenewalDateBetween(request.getStartDate(), request.getEndDate());
    List<PolicyOverComingDto> policyOverComingDtos;
    policyOverComingDtos = comingEntityPage.stream().map(o ->
            {
              PolicyOverComingDto policy = new PolicyOverComingDto();
              BeanUtils.copyProperties(o, policy);
              return policy;
            }
    ).collect(Collectors.toList());
    if(policyOverComingDtos.isEmpty())
      return new byte[0];
    return fileUtils.createExcelFile(policyOverComingDtos,"ONCOMING POLICY RENEWAL LIST BETWEEN"+request.getStartDate()+"AND" + request.getEndDate(),"SHEET1");
  }

  @Override
  public void renewalsDueIn60Days() {
    Date now = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(DateUtils.addMonths(now, 2));
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    Date startDate = calendar.getTime();

    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);

    Date endDate = calendar.getTime();

    logger.info("Start from : {}", startDate);
    logger.info("end from : {}", endDate);
    byte[] file = comingPolicyListInExcel(ComingPolicyListRequestInExcel.builder().startDate(startDate).endDate(endDate).build());
    if(file.length > 0)
      notificationClient.sendEmailAttachmentBytes(
              new Gson().toJson(
                      EmailAttachmentBytesDto
                      .builder()
                      .bytes(file)
                      .attachmentName("ONCOMING POLICY RENEWALS.xlsx")
                      .emailAddress(destinationEmail)
                      .text(emailBody)
                      .subject("POLICY(s) RENEWING IN 60 DAYS")
                      .build()
              )
      );
  }


  @Override
  public List<HealthPolicyListResponse> getCustomerPolicyList(CustomerPolicyListRequest request) {
    List<HealthPolicyListResponse> responseList = new ArrayList<>();
    CustomerDetailResponse customerDetail = customerService.getCustomer(
        CustomerSearchRequest.builder().customerId(request.getCustomerId()).build());
    logger.debug("get policy list, customerId: {}, customerDetail: {}", request.getCustomerId(),
        customerDetail);
    Long entityId = Optional.ofNullable(customerDetail).map(CustomerDetailResponse::getPrincipal)
        .map(Principal::getEntityId).orElse(null);
    if (entityId != null) {
      request.setEntityId(String.valueOf(entityId));
      responseList = this.getPolicyLists(request);
      responseList.addAll(this.getUnderwritingQuotes(request));
    } else {
      logger.warn("get policy list, entity id is null");
    }
    return responseList;
  }

  public List<HealthPolicyListResponse> getUnderwritingQuotes(CustomerPolicyListRequest request){
    List<Quote> quotesList = quoteMapper
            .getCustomerQuotes(request.getCustomerId(), null,
                    PolicyStatus.UNDERWRITING.getValue());
    logger.warn("quotesList  {}",quotesList);
    List<HealthPolicyListResponse> responseQoteList = new ArrayList<>();
    quotesList.stream().forEach(quote -> {
      HealthPolicyListResponse response = HealthPolicyListResponse
              .builder()
              .productId(String.valueOf(quote.getProductId()))
              .productName(ProductEnum.getById(quote.getProductId()).getValue())
              .policyNumber(quote.getExtPolicyNumber())
              .startDate(quote.getStartDate())
              .effectiveDate(quote.getEffectiveDate())
              .renewalDate(quote.getRenewalDate())
              .status("U")
              .premium(quote.getPremium().getTotalPremium())
              .build();
      responseQoteList.add(response);
    });
    return responseQoteList;
  }

  private String getProductName(Integer productId) {
    return ProductEnum.getById(productId).getValue();
  }

  @Override
  public List<HealthPolicyListResponse> getCustomerPolicyList(EntityPolicyListRequest request) {
    CustomerPolicyListRequest customerPolicyListRequest = CustomerPolicyListRequest.builder()
        .customerId(request.getEntityId()).entityId(request.getEntityId()).build();
    return this.getPolicyLists(customerPolicyListRequest);
  }

  protected List<HealthPolicyListResponse> getPolicyLists(CustomerPolicyListRequest request) {
    List<HealthPolicyListResponse> responseList = new ArrayList<>();
    List<Policy> policyList = policyRemote.getPolicyLists(request);
    if (CollectionUtils.isNotEmpty(policyList)) {
      List<Integer> policyIds = policyList.stream().map(Policy::getPolicyId)
          .collect(Collectors.toList());
      List<ApplicationRenewalPolicy> renewedPolicyList = policyMapper
          .searchRenewedPolicyList(policyIds);
      List<ApplicationRenewalPolicy> statusPolicyList = policyMapper
              .searchPolicyList(policyIds);
      policyList.stream()
          .forEach(p -> {
                boolean renewed = false;
                    if (CollectionUtils.isNotEmpty(renewedPolicyList)) {
                      for (ApplicationRenewalPolicy policy : renewedPolicyList) {
                        if (p.getPolicyId().equals(policy.getPolicyId()) && DateUtils
                                .isSameDay(p.getPolicyEffectiveDate(), policy.getEffectiveDate())) {
                          renewed = true;
                          break;
                        }
                      }
                    }
                updatePolicyStatus(statusPolicyList, p);
                responseList
                    .add(HealthPolicyListResponse.builder().policyNumber(p.getPolicyNumber())
                        .productId(String.valueOf(p.getProductId()))
                        .productName(this.getProductName(p.getProductId()))
                        .effectiveDate(p.getPolicyEffectiveDate())
                        .startDate(p.getPolicyStartDate()).renewalDate(p.getPolicyRenewalDate())
                        .status(p.getPolicyStatus()).premium(p.getPolicyAmount()).renewed(renewed)
                        .build());
              }
          );
    }
    return responseList;
  }

  public void updatePolicyStatus(List<ApplicationRenewalPolicy> statusPolicyList, Policy p){
    if (CollectionUtils.isNotEmpty(statusPolicyList)) {
      for (ApplicationRenewalPolicy policy : statusPolicyList) {
        if (p != null && p.getPolicyId() != null && p.getPolicyId().equals(policy.getPolicyId())
        && policy.getStatus().equals(PolicyStatus.UNDERWRITING.getValue())) {
          p.setPolicyStatus(policyStatusConfig.getStatusMap().get(policy.getStatus()));
        }
      }
    }
  }

  @Override
  public boolean createCustomerPolicyCache(CustomerIdRequest request) {
    boolean result = false;
    String customerId = request.getCustomerId();
    Customer customer = customerService.getCustomer(customerId);
    Long entityId = Optional.ofNullable(customer).map(Customer::getEntityId).orElse(null);
    if (entityId != null) {
      String customerEntityId = String.valueOf(entityId);
      CustomerPolicyCache customerPolicyCache = this.getCustomerPolicyCache(customerEntityId);
      if (customerPolicyCache == null) {
        CustomerPolicyListRequest customerPolicyListRequest = CustomerPolicyListRequest.builder()
            .customerId(customerId).entityId(customerEntityId).build();
        List<Policy> policyList = policyRemote.getPolicyLists(customerPolicyListRequest);
        if (CollectionUtils.isNotEmpty(policyList)) {
          result = policyMapper.insertCustomerPolicy(
              CustomerPolicyCache.builder().entityId(customerEntityId).policyList(policyList)
                  .createTime(new Date()).updateTime(new Date()).build()) == 1;
        }
      } else {
        logger.debug("customer[{}] policy cache already existed", customerEntityId);
      }
    } else {
      logger.warn("create customer[{}] policy cache, entity id is empty", customerId);
    }

    return result;
  }
  @Override
  public PolicyRenewalResponse renewalPolicyForComingWorker(PolicyRenewalRequest request) {
    RenewalPremium premium = null;
    String policyNumber = request.getPolicyNumber();
    Date effectiveDate = request.getEffectiveDate();
    // 1. get policy detail
    PolicyDetail policyDetail = policyRemote.getPolicyDetail(PolicyNumberRequest.builder().policyNumber(policyNumber).effectiveDate(effectiveDate).build());
    logger.debug("main_2.2. get policy detail: {}", policyDetail);
    if (policyDetail != null) {
      // 2. get policy beneficiary
      CompletableFuture<PolicyBeneficiary> policyBeneficiary = CompletableFuture.supplyAsync(() -> this.getPolicyBeneficiary(policyDetail));
      // 3. get policy benefit
      CompletableFuture<Benefit> benefit = CompletableFuture.supplyAsync(() -> this.getPolicyBenefit(policyDetail));
      // 4. get policy claim
      CompletableFuture<PolicyClaim> policyClaim = CompletableFuture.supplyAsync(() -> this.getPolicyClaim(policyDetail));
      // 5. getP olicy Adjustment
      CompletableFuture<BigDecimal> policyAdjustment = CompletableFuture.supplyAsync(() -> this.getPolicyAdjustment(policyDetail));
      // 6. calculate Renewal Premium
      CompletableFuture<RenewalPremium> premiumFuture = CompletableFuture.allOf(policyBeneficiary, benefit, policyClaim, policyAdjustment).thenApply(
                      aVoid -> productService.calcRenewPremiumByTotalPremiumForComingWorker(policyBeneficiary.join(), policyDetail, benefit.join(), policyClaim.join(), policyAdjustment.join()));
      premium = premiumFuture.join();
    }
    return PolicyRenewalResponse.builder()
            .premium(premium)
            .balance(premium != null ? premium.getTotalPremium() : BigDecimal.ZERO)
            .build();
  }

  @Override
  public PolicyRenewalResponse renewalPolicy(PolicyRenewalRequest request) {
    RenewalPremium premium;
    String policyNumber = request.getPolicyNumber();
    Date effectiveDate = request.getEffectiveDate();

    HealthPolicy policy = this.getPolicy(policyNumber, effectiveDate);

    if (policy != null) {
      return PolicyRenewalResponse.builder()
                                  .premium(policy.getRenewalPremium())
                                  .balance(policy.getRenewalBalance())
                                  .build();
    }

    // 1. get policy detail
    long startTime = System.currentTimeMillis();
    PolicyDetail policyDetail = policyRemote.getPolicyDetail(
        PolicyNumberRequest.builder().policyNumber(policyNumber).effectiveDate(effectiveDate).build());
    logger.info("{} *** policyDetail: \n {}", GlobalConstant.CALCULATE_RENEWAL_PREMIUM, policyDetail);
    long endTime = System.currentTimeMillis();
    logger.info("1. get policy detail, duration: {} ms", endTime - startTime);
    if (policyDetail != null) {
      this.checkRenewalDate(policyDetail.getPolicyRenewalDate());
      // 2. get policy beneficiary
      CompletableFuture<PolicyBeneficiary> policyBeneficiary = CompletableFuture
          .supplyAsync(MdcConfig.mdcSupplier(() -> this.getPolicyBeneficiary(policyDetail)));
      // 3. get policy benefit
      CompletableFuture<Benefit> benefit = CompletableFuture
          .supplyAsync(MdcConfig.mdcSupplier(() -> this.getPolicyBenefit(policyDetail)));
      // 4. get policy claim
      CompletableFuture<PolicyClaim> policyClaim = CompletableFuture
          .supplyAsync(MdcConfig.mdcSupplier(() -> this.getPolicyClaim(policyDetail)));
      // 5. get policy claim
      CompletableFuture<BigDecimal> policyAdjustment = CompletableFuture
          .supplyAsync(MdcConfig.mdcSupplier(() -> this.getPolicyAdjustment(policyDetail)));
      // 6. calculate Renewal Premium
      CompletableFuture<RenewalPremium> premiumFuture = CompletableFuture
          .allOf(policyBeneficiary, benefit, policyClaim, policyAdjustment).thenApply(
              aVoid -> productService.calcRenewPremiumByTotalPremium(policyBeneficiary.join(), policyDetail,
                      benefit.join(), policyClaim.join(), policyAdjustment.join()));
      premium = premiumFuture.join();
      long endTime2 = System.currentTimeMillis();
      logger.debug("6. calculate Renewal Premium, duration: {} ms", endTime2 - endTime);
      String quoteId = quoteService.searchQuoteByPolicyId(policyDetail.getPolicyId(), policyDetail.getPolicyNumber());
      // 7. insert policy
      HealthPolicy healthPolicy = HealthPolicy.builder()
          .quoteId(quoteId).policyId(policyDetail.getPolicyId())
          .policyNumber(policyDetail.getPolicyNumber()).startDate(policyDetail.getPolicyStartDate())
          .effectiveDate(policyDetail.getPolicyEffectiveDate())
          .renewalDate(policyDetail.getPolicyRenewalDate())
          .policyHolderId(policyDetail.getPolicyHolderEntityId())
          .productId(policyDetail.getProductId()).status(policyDetail.getPolicyStatus())
          .benefit(benefit.join())
          .premium(Premium.builder().totalPremium(policyDetail.getTotalPremium()).build())
          .balance(policyDetail.getPremiumLeftToPay())
          .renewalPremium(premium)
          .renewalBalance(premium.getTotalPremium())
          .createTime(new Date()).build();
      this.savePolicyRenewalPremium(healthPolicy, policyBeneficiary.join());
    } else {
      throw new BusinessException("can't find the policy");
    }

    return PolicyRenewalResponse.builder()
                                .premium(premium)
                                .balance(premium.getTotalPremium())
                                .build();
  }

  private boolean savePolicyRenewalPremium(HealthPolicy healthPolicy,
      PolicyBeneficiary policyBeneficiary) {
    boolean success = policyMapper.insert(healthPolicy) == 1;
    String log = new Gson().toJson(policyBeneficiary);
    logger.debug("policyBeneficiary: {}", log);
    if (success) {
      premiumService.recordPolicyBeneficiaryPremium(healthPolicy, policyBeneficiary);
    }
    return success;
  }

  private PolicyClaim getPolicyClaim(PolicyDetail policyDetail) {
    long startTime = System.currentTimeMillis();
    BigDecimal earnedPremium = policyDetail.getPremiumPaid();
    if (earnedPremium == null) {
      earnedPremium = BigDecimal.ZERO;
    }
    PolicyClaim policyClaim = PolicyClaim.builder().claimsPaid(BigDecimal.ZERO).earnedPremium(earnedPremium).noClaimYear(0).build();
    return CompletableFuture
        .supplyAsync(() -> policyRemote.getPolicyClaims(policyDetail.getPolicyId()))
        .thenCombine(CompletableFuture.supplyAsync(() -> policyRemote.getPolicyHistoryLists(PolicyIdRequest.builder()
                        .policyId(policyDetail.getPolicyId())
                        .effectiveDate(policyDetail.getPolicyEffectiveDate()).build())),
            (claimList, policyHistoryLists) -> {
              if (CollectionUtils.isEmpty(claimList)) {
                logger.debug("ClaimList isEmpty ");
                int noClaimYear = CollectionUtils.size(policyHistoryLists);
                policyClaim.setNoClaimYear(noClaimYear);
              } else {
                this.setPolicyClaim(policyDetail, policyClaim, claimList, policyHistoryLists);
              }
              long endTime = System.currentTimeMillis();
              logger.debug("4. get policy claim, duration: {} ms", endTime - startTime);
              return policyClaim;
            }
        ).exceptionally(e -> {
              logger.info("{}", e.getStackTrace());
          logger.error(" get policy claim failed, error: {}", e.getMessage());

          return null;
        }).join();
  }

  private void setPolicyClaim(PolicyDetail policyDetail, PolicyClaim policyPremium,
                              List<Claim> claimList, List<Policy> policyHistoryLists) {
    Integer noClaimYear = 0;
    String claimListLog = new Gson().toJson(claimList);
    String policyHistoryListsLog = new Gson().toJson(policyHistoryLists);
    String policyPremiumLog = new Gson().toJson(policyPremium);
    String policyDetailLog = new Gson().toJson(policyDetail);

    logger.info("setPolicyClaim claimList : {}", claimListLog);
    logger.info("setPolicyClaim policyHistoryLists : {}", policyHistoryListsLog);
    logger.info("setPolicyClaim policyPremium : {}", policyPremiumLog);
    logger.info("setPolicyClaim policyDetail : {}", policyDetailLog);

    Date startDate = policyDetail.getPolicyStartDate();
    logger.info("setPolicyClaim startDate: {}", startDate);
    BigDecimal claimsPaid = claimList.stream()
                                     .filter(c -> c.getTreatmentDate().after(startDate))
                                     .map(Claim::getSettledAmount)
                                     .reduce(BigDecimal::add)
                                     .orElse(BigDecimal.ZERO);

    logger.info("setPolicyClaim claimsPaid: {}", claimsPaid);

    Optional<Date> claimHistoryDate = claimList.stream()
                                               .filter(c -> c.getTreatmentDate().before(startDate))
                                               .max(Comparator.comparing(Claim::getTreatmentDate))
                                               .map(Claim::getTreatmentDate);

    logger.info("setPolicyClaim claimHistoryDate: {}", claimHistoryDate);


    if (claimHistoryDate.isPresent() && CollectionUtils.isNotEmpty(policyHistoryLists)) {
      Integer thisYear = 0;
      noClaimYear = (int) policyHistoryLists.stream()
                                            .filter(p -> p.getPolicyStartDate().after(claimHistoryDate.get()))
                                            .count();

      Optional<Policy> policy = policyHistoryLists.stream()
                                                  .filter(o -> eqNowYear(o.getPolicyStartDate()))
                                                  .findAny();
      if (!policy.isPresent()) {
        thisYear = inThisYear(policyDetail, claimHistoryDate.get()) ? 0 : 1;
      }

      logger.info("setPolicyClaim noClaimYear : {}", noClaimYear);
      noClaimYear = Math.min(noClaimYear + thisYear, 6);
    }

    logger.info("setPolicyClaim noClaimYear min: {}", noClaimYear);
    logger.info("setPolicyClaim claimsPaid: {}", claimsPaid);
    policyPremium.setClaimsPaid(claimsPaid);
    policyPremium.setNoClaimYear(noClaimYear);
  }

  private static boolean eqNowYear(Date policyStartDate) {
    Calendar cal = Calendar.getInstance();
    int year = cal.get(Calendar.YEAR);

    Calendar policyStartCalendar = Calendar.getInstance();
    policyStartCalendar.setTime(policyStartDate);
    int policyStartYear = policyStartCalendar.get(Calendar.YEAR);

    return year == policyStartYear;
  }
  private static boolean inThisYear(PolicyDetail policyDetail, Date claimHistoryDate) {
    Date renewalDateLess = HealthDateUtils.getDayEndTime(DateUtils.addMonths(policyDetail.getPolicyRenewalDate(), -2));
    Date policyFromStartTime = HealthDateUtils.getDayStartTime(policyDetail.getPolicyStartDate());
    return claimHistoryDate.after(policyFromStartTime) && claimHistoryDate.before(renewalDateLess);
  }


  private PolicyBeneficiary getPolicyBeneficiary(PolicyDetail policyDetail) {
    long startTime = System.currentTimeMillis();
    List<DependantDetail> dependantDetailList = policyRemote.getPolicyBeneficiary(PolicyIdRequest.builder()
                                             .policyId(policyDetail.getPolicyId())
                                             .effectiveDate(policyDetail.getPolicyEffectiveDate())
                                             .build());
    String dependantDetailListString = JsonUtils.objectToJson(dependantDetailList);
    logger.debug("dependantDetailList: \n {}", dependantDetailListString);
    PolicyBeneficiary policyBeneficiary = this.convertPolicyBeneficiary(dependantDetailList,
                                                                        HealthDateUtils.nextDay(policyDetail.getPolicyRenewalDate()),
                                                                        String.valueOf(policyDetail.getPolicyId()));
    long endTime = System.currentTimeMillis();
    logger.debug("2. get policy beneficiary, duration: {} ms", endTime - startTime);
    return policyBeneficiary;
  }

  private PolicyBeneficiary convertPolicyBeneficiary(List<DependantDetail> dependantDetailList,
      Date startDate, String policyId) {
    PolicyBeneficiary policyBeneficiary = PolicyBeneficiary.builder().id(policyId).build();
    List<Beneficiary> list = new ArrayList<>();
    for (DependantDetail dependant : dependantDetailList) {
      Date dob = dependant.getDateOfBirth();
      String gender = dependant.getGender();
      String relationship = dependant.getRelationship();
      Long entityId = dependant.getEntityId();
      String name = dependant.getFirstName() + " " + dependant.getLastName();
      if (DependantRelationship.POLICY_HOLDER.getValue()
                                             .equals(relationship)) {
        Beneficiary beneficiary = Beneficiary.builder()
                                             .entityId(entityId)
                                             .name(name)
                                             .relationship(relationship)
                                             .age(HealthDateUtils.calculateAge(dob, startDate))
                                             .gender(gender)
                                             .build();
        policyBeneficiary.setPrincipal(beneficiary);
      } else if (DependantRelationship.SPOUSE.getValue().equals(relationship) ||
                 DependantRelationship.PARTNER.getValue().equals(relationship)) {
        Beneficiary beneficiary = Beneficiary.builder()
                                             .entityId(entityId)
                                             .name(name)
                                             .relationship(relationship)
                                             .age(HealthDateUtils.calculateAge(dob, startDate))
                                             .gender(gender)
                                             .build();
        policyBeneficiary.setSpouse(beneficiary);
      } else if (
          DependantRelationship.UNMARRIED_CHILD.getValue().equals(relationship)
          || DependantRelationship.MARRIED_CHILD.getValue().equals(dependant.getRelationship())) {
        Beneficiary beneficiary = Beneficiary.builder()
                                             .entityId(entityId)
                                             .name(name)
                                             .relationship(relationship)
                                             .age(HealthDateUtils.calculateAge(dob, startDate))
                                             .gender(gender)
                                             .build();
        list.add(beneficiary);
      }
    }
    policyBeneficiary.setChildren(list);
    return policyBeneficiary;
  }

  private BigDecimal getPolicyAdjustment(PolicyDetail policyDetail) {
    BigDecimal adjustment = BigDecimal.ZERO;
    long startTime = System.currentTimeMillis();
    PolicyIdRequest request = PolicyIdRequest.builder()
        .policyId(policyDetail.getPolicyId()).effectiveDate(policyDetail.getPolicyEffectiveDate())
        .build();
    PolicyAdjustment response = policyRemote.getPolicyAdjustment(request);
    long endTime = System.currentTimeMillis();
    logger.debug("5. get policy adjustment, duration: {} ms", endTime - startTime);
    if (response != null) {
      adjustment = response.getAdjustment();
    }
    return adjustment;
  }

  private Benefit getPolicyBenefit(PolicyDetail policyDetail) {
    long startTime = System.currentTimeMillis();
    PolicyIdRequest request = PolicyIdRequest.builder()
        .policyId(policyDetail.getPolicyId()).effectiveDate(policyDetail.getPolicyEffectiveDate())
        .build();
    Benefit response = policyRemote.getPolicyBenefit(request);
    String log = new Gson().toJson(response);
    logger.info("getPolicyBenefit response: {}", log);
    long endTime = System.currentTimeMillis();
    logger.debug("3. get policy benefit, duration: {} ms", endTime - startTime);
    if (response == null) {
      throw new BusinessException("can't get the benefit");
    }
    return response;
  }

  private void checkRenewalDate(Date renewalDate) {
    Date validDate = DateUtils.addMonths(renewalDate, -2);
    if (validDate.after(new Date())) {
      throw new BusinessException("can't calculate the renewal premium");
    }
  }

  private CustomerPolicyCache getCustomerPolicyCache(String entityId) {
    return policyMapper.selectCustomerPolicyCache(entityId);
  }

  public void processUpdateCustomerPolicyCacheTask() {
    List<CustomerPolicyCache> policyCaches = policyMapper.selectCustomerPolicyCacheList(new Date());
    if (CollectionUtils.isNotEmpty(policyCaches)) {
      logger.info("update customer policy cache task, total {} policy", policyCaches.size());
      List<Integer> entityIds = new ArrayList<>();
      for (int i = 0; i < policyCaches.size(); i++) {
        CustomerPolicyCache policyCache = policyCaches.get(i);
        entityIds.add(Integer.parseInt(policyCache.getEntityId()));
        if (entityIds.size() == 100 || i == policyCaches.size() - 1) {
          Map<Long, List<Policy>> policyListMap = policyRemote
              .getBatchPolicyLists(CustomerEntityIdsRequest.builder().entityIds(entityIds)
                  .build());
          policyListMap.forEach((entityId, policyList) ->
              policyMapper.updateCustomerPolicyCache(
                  CustomerPolicyCache.builder().entityId(String.valueOf(entityId))
                      .policyList(policyList)
                      .updateTime(new Date()).build())
          );
          entityIds = new ArrayList<>();
        }
      }
    }
  }

  @Override
  public boolean updatePolicyRenewalBalance(String policyNumber, Date effectiveDate,
      BigDecimal amount) {
    boolean result = false;
    HealthPolicy policy = this.getPolicy(policyNumber, effectiveDate);
    if (policy != null) {
      BigDecimal renewalBalance = policy.getRenewalBalance().subtract(amount);
      policy.setRenewalBalance(renewalBalance);
      policy.setUpdateTime(new Date());
      if (renewalBalance.intValue() <= 0) {
        policy.setStatus(PolicyStatus.UNDERWRITING.getValue());
        ApplicationRenewalPolicy renewalPolicy = policyMapper
            .searchRenewalPolicy(policyNumber, effectiveDate);
        ReminderRequest reminderRequest = ReminderRequest.builder()
            .customerId(
                Optional.ofNullable(renewalPolicy).map(ApplicationRenewalPolicy::getCustomerId)
                    .orElse(null))
            .quoteId(policy.getQuoteId()).policyNumber(policyNumber)
            .effectiveDate(DateFormatUtils.format(effectiveDate, GlobalConstant.YYYYMMDD))
            .type("health_renewal")
            .build();
        reminderEventPublisher.publishReminder(reminderRequest);
      }
      result = policyMapper.updateRenewalBalance(policy) == 1;
    } else {
      throw new BusinessException(
          "can't find the policy, [policyNumber: " + policyNumber + ", effectiveDate: "
              + effectiveDate + "]");
    }
    return result;
  }

  @Override
  public HealthPolicy getPolicy(String policyNumber, Date effectiveDate) {
    return policyMapper.select(policyNumber, effectiveDate);
  }

  @Override
  public ApplicationRenewalPolicyListResponse searchApplicationRenewalPolicyList(
      ApplicationPolicyListSearchRequest request) {
    String sortColumn = "";
    String sortType = request.getSortType();
    if (StringUtils.isNotBlank(sortType)) {
      sortColumn = applicationPolicySortTypeMap.get(sortType);
      if (sortColumn == null) {
        throw new BusinessException("invalid sort type");
      }
    }

    String sort = request.getSort();
    if (StringUtils.isNotBlank(sort) && !"asc".equalsIgnoreCase(sort) && !"desc"
        .equalsIgnoreCase(sort)) {
      throw new BusinessException("invalid sort value, should be 'asc' or 'desc'");
    }

    ApplicationPolicyListSearchFilter filter = ApplicationPolicyListSearchFilter.builder()
        .filter(request.getFilter()).archived(BooleanUtils.toInteger(request.isArchived()))
        .paid(request.isPaid()).sortColumn(sortColumn).sort(sort).build();
    return CompletableFuture
        .supplyAsync(MdcConfig.mdcSupplier(() -> {
          int index = request.getIndex();
          int limit = request.getLimit();
          if (index > 0 && limit > 0) {
            PageMethod.startPage(request.getIndex(), request.getLimit());
          }
          return policyMapper.searchRenewalPolicyList(filter);
        }))
        .thenCombine(CompletableFuture
                .supplyAsync(MdcConfig.mdcSupplier(() -> policyMapper.searchRenewalPolicyListCount(filter))),
            (list, count) -> ApplicationRenewalPolicyListResponse.builder().list(list).total(count)
                .build()
        ).exceptionally(e -> {
          logger.error(" search renewal policy list failed, error: {}", e.getMessage());
          return null;
        }).join();
  }

  @Override
  public ApplicationRenewalPolicy getApplicationRenewalPolicy(PolicyNumberRequest request) {
    return policyMapper.searchRenewalPolicy(request.getPolicyNumber(), request.getEffectiveDate());
  }

  @Override
  public boolean archiveApplicationRenewalPolicy(PolicyNumberRequest request) {
    HealthPolicy policy = HealthPolicy.builder().policyNumber(request.getPolicyNumber())
        .effectiveDate(request.getEffectiveDate()).archived(true).updateTime(new Date()).build();
    return policyMapper.archiveApplicationRenewalPolicy(policy) == 1;
  }

  @Override
  public void renewalNotificationPolicies() throws InterruptedException {
    List<Integer> renewal = policyRenewalDaysConfig.getRenewal();
    List<PolicyOverComingEntity> toBeRenewal = new ArrayList<>();
    for (Integer days : renewal) {
      List<PolicyOverComingEntity> comingEntityPage = policyOverComingRepository.findAllPoliciesDueForRenewalIn(days);
      toBeRenewal.addAll(comingEntityPage);
    }
    renewalExecutorConfiguration.sendNotificationsAsync(toBeRenewal, true);
  }

  @Override
  public void expiredNotificationPolicies() throws InterruptedException {
    List<Integer> expired = policyRenewalDaysConfig.getExpired();
    List<PolicyOverComingEntity> toBeRenewal = new ArrayList<>();
    for (Integer days : expired) {
      List<PolicyOverComingEntity> comingEntityPage = policyOverComingRepository.findAllPoliciesDueForRenewalIn(days);
      toBeRenewal.addAll(comingEntityPage);
    }
    renewalExecutorConfiguration.sendNotificationsAsync(toBeRenewal, false);
  }
}
