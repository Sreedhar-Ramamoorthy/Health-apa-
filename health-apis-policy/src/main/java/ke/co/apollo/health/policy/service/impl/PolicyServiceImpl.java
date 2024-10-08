package ke.co.apollo.health.policy.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.ImmutableMap;

import ke.co.apollo.health.common.domain.model.request.*;
import ke.co.apollo.health.policy.model.AgentBranchDetails;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.Benefit;
import ke.co.apollo.health.common.domain.model.BenefitBreakDownCategory;
import ke.co.apollo.health.common.domain.model.Claim;
import ke.co.apollo.health.common.domain.model.IntermediaryEntity;
import ke.co.apollo.health.common.domain.model.Policy;
import ke.co.apollo.health.common.domain.model.PolicyAdjustment;
import ke.co.apollo.health.common.domain.model.PolicyBenefit;
import ke.co.apollo.health.common.domain.model.PolicyDetail;
import ke.co.apollo.health.common.domain.model.Role;
import ke.co.apollo.health.common.domain.model.remote.AddBenefitsToPolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.ApiResponse;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyRequest;
import ke.co.apollo.health.common.domain.model.remote.CreatePolicyResponse;
import ke.co.apollo.health.common.domain.model.remote.PolicyIdRequest;
import ke.co.apollo.health.common.domain.model.request.AddBusinessSourceToIndividualPolicyRequest.BusinessSourceBean;
import ke.co.apollo.health.common.domain.model.request.AddBusinessSourceToIndividualPolicyRequest.BusinessSourceBean.InterestedPartiesBean;
import ke.co.apollo.health.common.domain.model.response.ASAPIResponse;
import ke.co.apollo.health.common.domain.model.response.AddBeneficiariesToPolicyResponse;
import ke.co.apollo.health.common.enums.AgentType;
import ke.co.apollo.health.common.enums.BenefitEnum;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.policy.mapper.hms.ClaimHMSMapper;
import ke.co.apollo.health.policy.mapper.hms.CommissionHMSMapper;
import ke.co.apollo.health.policy.mapper.hms.IntermediaryHMSMapper;
import ke.co.apollo.health.policy.mapper.hms.PolicyHMSMapper;
import ke.co.apollo.health.policy.model.Commission;
import ke.co.apollo.health.policy.remote.PolicyRemote;
import ke.co.apollo.health.policy.service.PolicyService;
import ke.co.apollo.health.policy.service.QuoteService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PolicyServiceImpl implements PolicyService {

  private static final Map<String, String> sortTypeMap = ImmutableMap
      .of("renewalDate", "RENEWALDATE", "policyNumber", "POLICYNUMBER");
  public static final String DIRECT = "DIRECT";
  public static final String TIED_AGENT = "TIED AGENT";
  public static final String INDEPENDENT_AGENT = "INDEPENDENT AGENT";
  public static final String BROKER = "BROKER";

  @Autowired
  PolicyRemote policyRemote;

  @Autowired
  PolicyHMSMapper policyHMSMapper;

  @Autowired
  CommissionHMSMapper commissionHMSMapper;

  @Autowired
  ClaimHMSMapper claimHMSMapper;

  @Autowired
  IntermediaryHMSMapper intermediaryHMSMapper;

  @Autowired
  BenefitBreakDownCategory benefitCategory;

  @Autowired
  QuoteService quoteService;

  @Override
  public CreatePolicyResponse createPolicy(CreatePolicyRequest createPolicyRequest) {
    return policyRemote.createPolicy(createPolicyRequest);
  }

  @Override
  public ASAPIResponse addPolicyBranchDetails(PolicyAdditionalInfoRequest request) {
    return policyRemote.addAgentBranchDetailsToPolicy(request);
  }

  @Override
  public ApiResponse addBenefitsToPolicy(AddBenefitsToPolicyRequest request) {
    return policyRemote.addBenefitsToPolicy(request);
  }

  @Override
  public ApiResponse addIndividualPolicyBeneficiaryUWQuestions(
      AddIndividualPolicyBeneficiaryUWQuestionsRequest request) {
    return policyRemote.addIndividualPolicyBeneficiaryUWQuestions(request);
  }
  @Override
  public Map<Long, List<Policy>> getBatchPolicyLists(List<Integer> request) {
    List<Policy> policyList = policyHMSMapper.searchBatchPolicyLists(request);
    Map<Long, List<Policy>> policyMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(policyList)) {
      policyMap = policyList.stream()
          .collect(Collectors.groupingBy(Policy::getPolicyHolderEntityId));
    }
    return policyMap;
  }

  @Override
  public List<Policy> getPolicyLists(CustomerPolicyListRequest request) {
    List<Policy> policies = new ArrayList<>();
    Integer policyHolderId = Integer.parseInt(request.getEntityId());

    String sortColumn = this.getSortColumn(request.getSortType());
    String sort = request.getSort();
    this.validateSort(sort);

    int index = request.getIndex();
    int limit = request.getLimit();
    if (index > 0 && limit > 0) {
      PageMethod.startPage(request.getIndex(), request.getLimit());
    }

    List<Policy> policyList = policyHMSMapper.searchPolicyLists(policyHolderId, request.getFilter(), sortColumn, sort);

    String range = request.getRange();
    if (CollectionUtils.isNotEmpty(policyList)) {
      if (StringUtils.isBlank(range) || "All".equals(range)) {
        policies = policyList;
      } else {
        Date currentDate = new Date();
        Date dueDate = this.getDueDate(currentDate, range);
        policies = policyList.stream().filter(policy -> {
          Date renewalDate = policy.getPolicyRenewalDate();
          return renewalDate != null && renewalDate.after(currentDate) && renewalDate
              .before(dueDate);
        }).collect(Collectors.toList());
      }
    }

    return policies;
  }

  @Override
  public List<ke.co.apollo.health.policy.model.Policy> getIntermediaryPoliciesList(IntermediaryPolicyDetailsRequest request) {
    return policyHMSMapper.getPolicyListByIntermediary(request);
  }

  @Override
  public List<Policy> getPolicyHistoryLists(PolicyIdRequest request) {
    return policyHMSMapper
        .searchPolicyHistoryLists(request.getPolicyId(), request.getEffectiveDate());
  }

  @Override
  public int getPolicyRenewalCount(PolicyIdRequest request) {
    return CollectionUtils.size(this.getPolicyHistoryLists(request));
  }

  @Override
  public List<Claim> getPolicyClaims(Integer policyId) {
    return claimHMSMapper.getPolicyClaims(policyId);
  }


  @Override
  public List<Commission> getCommissions(Integer agentId) {
    return commissionHMSMapper.getCommissions(agentId);
  }


  @Override
  public Benefit getPolicyBenefit(PolicyIdRequest request) {
    List<PolicyBenefit> benefits = policyHMSMapper.getPolicyBenefit(request.getPolicyId(),
        DateFormatUtils.format(request.getEffectiveDate(), GlobalConstant.YYYYMMDD));
    Benefit benefit = this.convert2Benefit(benefits);
    int travelBenefitLimit = quoteService.getTravelBenefitLimit(request.getPolicyId(), null);
    benefit.setTravelInsurance(travelBenefitLimit);
    return benefit;
  }

  @Override
  public PolicyAdjustment getPolicyAdjustment(PolicyIdRequest request) {
    return policyHMSMapper.getPolicyAdjustment(request.getPolicyId(),
        DateFormatUtils.format(request.getEffectiveDate(), GlobalConstant.YYYYMMDD));
  }

  @Override
  public AgentBranchDetails getAgentBranchDetails(Integer entityId) {
    return policyHMSMapper.getAgentBranchDetails(entityId);
  }

  private Benefit convert2Benefit(List<PolicyBenefit> policyBenefits) {
    Benefit benefit = Benefit.builder()
                             .inpatientLimit(0)
                             .maternityLimit(0)
                             .dentalLimit(0)
                             .opticalLimit(0)
                             .outpatientLimit(0)
                             .travelInsurance(0)
                             .build();
    if (CollectionUtils.isNotEmpty(policyBenefits)) {
      HashMap<String, String> map = benefitCategory.getCategories();
      for (PolicyBenefit policyBenefit : policyBenefits) {
        BenefitEnum category = BenefitEnum.getByValue(map.get(policyBenefit.getBenefit()));
        this.setBenefit(category, benefit, policyBenefit.getLimit());
      }
    }
    return benefit;
  }

  private void setBenefit(BenefitEnum category, Benefit benefit, Integer limit) {
    switch (category) {
      case DENTAL:
        benefit.setDentalLimit(limit);
        break;
      case OPTICAL:
        benefit.setOpticalLimit(limit);
        break;
      case MATERNITY:
        benefit.setMaternityLimit(limit);
        break;
      case INPATIENT:
        benefit.setInpatientLimit(limit);
        break;
      case OUTPATIENT:
        benefit.setOutpatientLimit(limit);
        break;
      case TRAVEL:
        benefit.setTravelInsurance(limit);
        break;
      default:
        break;
    }
  }

  private String getSortColumn(String sortType) {
    String sortColumn = "";
    if (StringUtils.isNotBlank(sortType)) {
      sortColumn = sortTypeMap.get(sortType);
      if (sortColumn == null) {
        throw new BusinessException("invalid sort type");
      }
    }

    return sortColumn;
  }

  private void validateSort(String sort) {
    if (StringUtils.isNotBlank(sort) && !"asc".equalsIgnoreCase(sort) && !"desc"
        .equalsIgnoreCase(sort)) {
      throw new BusinessException("invalid sort value, should be 'asc' or 'desc'");
    }
  }

  private Date getDueDate(Date date, String range) {
    int day = 0;
    if ("All".equals(range)) {
      day = 9999;
    } else {
      day = Integer.parseInt(range);
    }
    return DateUtils.addDays(date, day);
  }

  @Override
  public List<AddBeneficiariesToPolicyResponse> addBeneficiariesToPolicy(
      List<AddBeneficiariesToPolicyRequest> request) {

    List<AddBeneficiariesToPolicyResponse> result = new ArrayList<>();
    for (AddBeneficiariesToPolicyRequest beneficiary : request) {
      ASAPIResponse response = policyRemote.addBeneficiaryToPolicy(beneficiary);

      //convert effectiveDate to Date object before updating joinDate and originalJoinDate
      AddBeneficiariesToPolicyResponse beneficiariesResponse = AddBeneficiariesToPolicyResponse
          .builder()
          .entityId(beneficiary.getEntityId())
          .joinDate(beneficiary.getJoinDate())
          .originalJoinDate(beneficiary.getOriginalJoinDate())
          .policyEffectiveDate(beneficiary.getPolicyEffectiveDate())
          .policyId(beneficiary.getPolicyId())
          .underwritingType(beneficiary.getUnderwritingType())
          .build();
      if (response != null) {
        beneficiariesResponse.setSuccess(response.isSuccess());
      }
      result.add(beneficiariesResponse);
    }

    return result;
  }

  @Override
  public ApiResponse addBusinessSourceToIndividualPolicy(
      AddBusinessSourceToIndividualPolicyRequest request) {
    ApiResponse apiResponse = null;
    InterestedPartiesBean interestedParty = Optional.ofNullable(request)
                                                    .map(AddBusinessSourceToIndividualPolicyRequest::getBusinessSource)
                                                    .map(BusinessSourceBean::getInterestedParties)
                                                    .map(interestedParties -> interestedParties.get(0))
                                                    .orElse(null);
    if (interestedParty != null) {
      IntermediaryEntity intermediary = intermediaryHMSMapper.getIntermediary(interestedParty.getEntityId());
      String roleDesc = Optional.ofNullable(intermediary)
                                .map(IntermediaryEntity::getRoleDesc)
                                .orElse("");
      Role role = this.getRole(roleDesc);
      if (role != null) {
        request.getBusinessSource().setBusinessSourceWebName(role.getBusinessSourceWebName());
        interestedParty.setRole(role.getRoleName());
        getRoleBusinessSourceWebName(request);
        apiResponse = policyRemote.addBusinessSourceToIndividualPolicy(request);
      } else {
        log.error("role type is null, agent entity id: {}, role desc: {}",
                  interestedParty.getEntityId(), roleDesc);
      }
    }

    return apiResponse;
  }

  private static void getRoleBusinessSourceWebName(AddBusinessSourceToIndividualPolicyRequest request) {

    List<InterestedPartiesBean> interestedParties = request.getBusinessSource()
                                                           .getInterestedParties();
    switch (StringUtils.upperCase(request.getBusinessSource()
                                         .getBusinessSourceWebName())) {
      case BROKER:
      case INDEPENDENT_AGENT:
      case TIED_AGENT:
        interestedParties = interestedParties.stream()
                                             .filter(o -> StringUtils.equalsIgnoreCase("Agent Tied", o.getRole()) ||
                                                          StringUtils.equalsIgnoreCase("WHT", o.getRole()))
                                             .collect(Collectors.toList());

        break;
      case DIRECT:
        interestedParties = null;
        break;
      default:
    }
    request.getBusinessSource()
           .setInterestedParties(interestedParties);
  }

  private Role getRole(String roleDesc) {
    Role role = null;
    if (StringUtils.isNotBlank(roleDesc)) {
      switch (AgentType.getAgentType(roleDesc)) {
        case AGENTINDEPENDENT:
          role = new Role(INDEPENDENT_AGENT, "Agent Independent");
          break;
        case DIRECT:
          role = new Role(DIRECT, DIRECT);
          break;
        case AGENTTIED:
          role = new Role(TIED_AGENT, "Agent Tied");
          break;
        case BROKER:
          role = new Role(BROKER, BROKER);
          break;
        default:
      }
    }
    return role;
  }

  @Override
  public PolicyDetail getPolicyDetailByMapper(String policyNumber, Date effectiveDate) {

    return policyHMSMapper.searchPolicyDetail(policyNumber, DateFormatUtils.format(effectiveDate,
        GlobalConstant.YYYYMMDD));
  }

  @Override
  public Map<Integer, List<Policy>> getBatchPolicyListsById(List<Integer> request) {
    List<Policy> policyList = policyHMSMapper.searchBatchPolicyListsById(request);
    Map<Integer, List<Policy>> policyMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(policyList)) {
      policyMap = policyList.stream()
          .collect(Collectors.groupingBy(Policy::getPolicyId));
    }
    return policyMap;
  }



}
