package ke.co.apollo.health.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import ke.co.apollo.health.common.domain.model.*;
import ke.co.apollo.health.common.domain.model.remote.ApiResponse;
import ke.co.apollo.health.common.domain.model.request.AgentDetailsRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerAddPhoneRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerCreateRequest;
import ke.co.apollo.health.common.domain.model.response.PolicyRenewalResponse;
import ke.co.apollo.health.common.enums.BenefitEnum;
import ke.co.apollo.health.common.enums.GenderEnum;
import ke.co.apollo.health.domain.BenefitPremium;
import ke.co.apollo.health.domain.PolicyBeneficiary;
import ke.co.apollo.health.domain.QuoteListSearchFilter;
import ke.co.apollo.health.domain.entity.*;
import ke.co.apollo.health.domain.request.*;
import ke.co.apollo.health.domain.response.ActisurePolicyBranchDetailsResponse;
import ke.co.apollo.health.domain.response.AgentBranchDetailsResponse;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import ke.co.apollo.health.enums.QuoteStatusEnum;


public class CommonObjects {

    public static ApiResponse apiResponse = ApiResponse.builder()
            .errorMessage("")
            .success(true)
            .build();

    public static Dependant childDetails = Dependant.builder()
            .dateOfBirth(new Date(2021, 1, 1))
            .build();
    public static CustomerDetailResponse customerDetailResponse = CustomerDetailResponse.builder()
            .customerId("c111")
            .quoteId("q123")
            .principal(
                    Principal.builder()
                            .build()
            )
            .quoteDetails(
                    Quote.builder()
                            .code("Code")
                            .productId(49)
                            .agentId("A007")
                            .startDate(new Date(2023, 9, 9))
                            .effectiveDate(new Date(2023, 9, 9))
                            .renewalDate(new Date(2024, 9, 8))
                            .premium(new Premium())
                            .build())
            .benefit(
                    DependantBenefit.builder()
                            .outpatient(true)
                            .maternity(true)
                            .dental(true)
                            .optical(true)
                            .travelInsurance(true)
                            .build())
            .children(
                    Children.builder()
                            .count(1)
                            .detail(List.of(childDetails))
                            .build())
            .build();


    public static BenefitPremium benefitPremium = BenefitPremium.builder()
            .benefitType(BenefitEnum.INPATIENT)
            .benefitLimit(500000)
            .premium(BigDecimal.valueOf(24480))
            .build();

    public static List<BenefitPremium> benefitList = Arrays.asList(benefitPremium);

    public static PolicyBeneficiary.Beneficiary principalBeneficiary = PolicyBeneficiary.Beneficiary.builder()
            .customerId("CUS001")
            .entityId(2l)
            .name("PRINCIPAL")
            .age(29)
            .gender(GenderEnum.MALE.getValue())
            .relationship("Policy Holder")
            .benefitPremiums(benefitList)
            .build();

    public static Benefit benefit = Benefit.builder()
            .inpatientLimit(500000)
            .dentalLimit(30000)
            .opticalLimit(30000)
            .maternityLimit(150000)
            .outpatientLimit(150000)
            .build();

    public static PolicyBeneficiary policyBeneficiaryObject = PolicyBeneficiary.builder()
            .id("POL001")
            .principal(principalBeneficiary)
            .build();




    public static PolicyOverComingEntity policyOverComingEntityObject = PolicyOverComingEntity.builder()
                      .id(1)
                      .agentName("agentName")
                      .asagentId("asagentId")
                      .claims(BigDecimal.valueOf(200))
                      .createTime(new Date())
                      .discount(BigDecimal.ZERO)
                      .earnedPremium(BigDecimal.ZERO)
                      .effectiveDate(new Date())
                      .email("email")
                      .loading(BigDecimal.ZERO)
                      .loadingPercentage(BigDecimal.ZERO)
                      .mobile("mobile")
                      .needToUpdate(true)
                      .plan("plan")
                      .policyAmount("0")
                      .policyNumber("policyNumber")
                      .premium(BigDecimal.ZERO)
                      .principalName("principalName")
                      .renewalDate(new Date())
                      .totalPremium(BigDecimal.ZERO)
                      .updateTime(new Date())
                      .build();

    public static RenewalPremium renewalPremiumObject = RenewalPremium.builder()
                    .claimsPaid(BigDecimal.ZERO)
                    .discount(BigDecimal.ZERO)
                    .earnedPremium(BigDecimal.ZERO)
                    .loading(BigDecimal.ZERO)
                    .lossRatio(BigDecimal.ZERO)
                    .manualAdjustment(BigDecimal.ZERO)
                    .phcf(BigDecimal.ZERO)
                    .premium(BigDecimal.ZERO)
                    .stampDuty(BigDecimal.ZERO)
                    .build();

    public static PolicyRenewalResponse renewalResponseObject = PolicyRenewalResponse.builder()
                    .balance(BigDecimal.ZERO)
                    .premium(renewalPremiumObject)
                    .build();

    public static LocationEntity locationEntityObject = LocationEntity.builder()
                    .id(1)
                    .name("Location 1")
                    //.hospitalEntity()       //NO !! this will introduce a cyclic dependency 
                    .build();


    public static PaymentEntity paymentEntityObject = PaymentEntity.builder()
                    .id(1)
                    .name("Payment 1")
                    //.hospitalEntity()       //NO !! this will introduce a cyclic dependency 
                    .build();

    public static ServiceEntity serviceEntityObject = ServiceEntity.builder()
                    .id(1)
                    .name("Service 1")
                    //.hospitalEntity()       //NO !! this will introduce a cyclic dependency 
                    .build();

    public static HospitalEntity hospitalEntityObject = HospitalEntity.builder()
                    .id(1)
                    .name("Hosi")
                    .address("Nairobi CBD")
                    .contact("Dr")
                    .email("info@example.com")
                    .locationId(1)
                    .paymentId(1)
                    .location(locationEntityObject)
                    //.services()
                    //.coPayments()
                    .build();
    public static DependantBenefit dependantBenefit = DependantBenefit.builder()
            .dental(true)
            .maternity(true)
            .optical(true)
            .outpatient(true)
            .travelInsurance(false)
            .outpatient(true)
            .build();
    public static Dependant dependant = Dependant.builder()
            .dependantCode("code").relationship("spouse").build();

    public static Dependant dependantSpouse = Dependant.builder().build();

    public static Children children = Children.builder()
            .count(1)
            .detail(List.of(dependant))
            .build();
    public static CustomerCreateRequest customerCreateRequest = CustomerCreateRequest.builder()
            .quoteId("qweqweqweqwe")
            .agentId("ewrwerwerwe")
            .customerId("werwerewre")
            .benefit(dependantBenefit)
            .children(children)
            .dateOfBirth(new Date())
            .firstName("Steve")
            .gender("Male")
            .lastName("Kati")
            .onlyChild(false)
            .product("Jamii Plus")
            .spouse(dependantSpouse)
            .build();

    public static Quote quote = Quote.builder()
            .id("trytryyt")
            .isChildrenOnly(false)
            .effectiveDate(new Date())
            .agentId("89999")
            .paymentStyle("FULL")
            .productId(49)
            .extPolicyId(90)
            .startDate(new Date())
            .renewalDate(new Date())
            .premium(Premium.builder().build())
            .build();

    public static DependantAddRequest dependantAddRequest = DependantAddRequest.builder()
            .benefit(dependantBenefit)
            .children(children)
            .customerId("werwrwerew")
            .onlyChild(true)
            .spouse(dependantSpouse)
            .quoteId("werwerwer")
            .build();


    public static AgentDetailsRequest agentDetailsRequest = AgentDetailsRequest
            .builder()
            .entityId(9099)
            .build();

    public static AgentBranchDetailsResponse agentBranchDetailsResponse = AgentBranchDetailsResponse
            .builder()
            .branch("Kisumu")
            .build();

    public static AgentBranchEntity agentBranchEntity = AgentBranchEntity.builder()
            .entityId(200)
            .agentId("agent")
            .branchName("Kisumu")
            .build();

    public static PolicyAdditionalInfoRequest policyAdditionalInfoRequest = PolicyAdditionalInfoRequest
            .builder()
            .policyAdditionalInfoList(List.of(PolicyAdditionalInfoList.builder().key("APA").value("BR").build()))
            .build();

    public static QuoteQuestion quoteQuestion = QuoteQuestion.builder()
            .quoteId("q123")
            .agentId("agent")
            .customerId("agent")
            .build();

    public static ActisurePolicyBranchDetailsResponse actisurePolicyBranchDetailsResponse = ActisurePolicyBranchDetailsResponse
            .builder()
            .success(true)
            .build();

    public static Customer customer = Customer
            .builder()
            .customerId("788")
            .agentId("999o")
            .build();
    public static CustomerSearchRequest customerSearchRequest = CustomerSearchRequest
            .builder()
            .customerId("344333")
            .build();
    public static ClearDataRequest clearDataRequest = ClearDataRequest
             .builder()
             .customerId("12345")
             .build();

    public static Intermediary intermediary = Intermediary.builder()
            .agentId("agent123")
            .parentAgentId("4321")
            .entityId(5678)
            .firstName("Test")
            .lastName("Lasttest")
            .phoneNumber("0712345678")
            .limitedCompany(true)
            .status("Status")
            .deleted(true)
            .email("test@test.com")
            .poBox("8733")
            .postalCode("00100")
            .officeContactNumber("0721345768")
            .bankId("988765")
            .bankName("TestBanK")
            .bankAccountNumber("0987654321")
            .branchId("001")
            .branchName("TestBranch")
            .agentCode("AC001")
            .organization("DF")
            .enabled(true)
            .createTime(new Date())
            .updateTime(new Date())
            .lastLoginTime(new Date())
            .build();

    public static QuoteListSearchFilter quoteListSearchFilter = QuoteListSearchFilter.builder()
            .quoteStatus("ACTIVE")
            .index(1)
            .build();

    public static QuoteListSearchRequest quoteListSearchRequest = QuoteListSearchRequest.builder()
            .quoteStatus(QuoteStatusEnum.ACTIVE)
            .index(1)
            .agentId("1234")
            .limit(1)
            .sortType("sortType")
            .build();

    public static CustomerAddPhoneRequest customerAddPhoneRequest = CustomerAddPhoneRequest
            .builder()
            .customerId(customer.getCustomerId())
            .build();
}

