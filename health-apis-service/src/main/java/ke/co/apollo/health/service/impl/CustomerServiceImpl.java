package ke.co.apollo.health.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import ke.co.apollo.health.domain.entity.HealthStepEntity;
import ke.co.apollo.health.domain.request.*;
import ke.co.apollo.health.enums.HealthQuoteStepsEnum;
import ke.co.apollo.health.enums.UpdateDependentEnum;
import ke.co.apollo.health.mapper.health.QuoteMapper;
import ke.co.apollo.health.repository.HealthStepRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.ActBusinessError;
import ke.co.apollo.health.common.domain.model.Address;
import ke.co.apollo.health.common.domain.model.Children;
import ke.co.apollo.health.common.domain.model.ContactDetails;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.Dependant;
import ke.co.apollo.health.common.domain.model.DependantDetails;
import ke.co.apollo.health.common.domain.model.Phone;
import ke.co.apollo.health.common.domain.model.Principal;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.common.domain.model.RoleAdditionalInfo;
import ke.co.apollo.health.common.domain.model.request.AddClientEntityRequest;
import ke.co.apollo.health.common.domain.model.request.AddContactDetailsRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerAddPhoneRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerAddSuperIdRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerCreateRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerSuperIdRequest;
import ke.co.apollo.health.common.domain.model.request.GetCustomerByPhoneNoRequest;
import ke.co.apollo.health.common.domain.model.response.AddClientEntityResponse;
import ke.co.apollo.health.common.domain.model.response.AddContactDetailsResponse;
import ke.co.apollo.health.common.domain.model.response.CustomerCreateResponse;
import ke.co.apollo.health.common.domain.model.response.CustomerCreateResponse.QuoteBean;
import ke.co.apollo.health.common.domain.model.response.GetCustomerInfoResponse;
import ke.co.apollo.health.common.enums.DependantRelationship;
import ke.co.apollo.health.common.enums.ProductEnum;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.domain.request.CustomerUpdateRequest.PrincipalBean;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import ke.co.apollo.health.mapper.health.CustomerMapper;
import ke.co.apollo.health.mapping.CustomerMapping;
import ke.co.apollo.health.remote.CustomerRemote;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.service.CustomerService;
import ke.co.apollo.health.service.PolicyService;
import ke.co.apollo.health.service.QuestionService;
import ke.co.apollo.health.service.QuoteService;

import static ke.co.apollo.health.common.enums.DependantRelationship.SPOUSE;

/**
 * Manage customer
 *
 * @author Rick
 * @version 1.0
 * @see
 * @since 9/14/2020
 */
@Service
public class CustomerServiceImpl implements CustomerService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  CustomerMapper customerMapper;

  @Autowired
  QuoteMapper quoteMapper;

  @Autowired
  QuoteService quoteService;

  @Autowired
  PolicyService policyService;

  @Autowired
  PolicyRemote policyRemote;

  @Autowired
  QuestionService questionService;

  @Autowired
  CustomerRemote customerRemote;

  @Autowired
  Gson gson;

  @Autowired
  HealthStepRepository stepRepository;

  @Override
  public String addCustomer(CustomerAddRequest request) {
    if (request.getEntityId() != null) {
      GetCustomerInfoResponse response = this.getCustomerByEntityId(request.getEntityId());
      if (response != null) {
        throw new BusinessException("Existing client[entity id:" + request.getEntityId() + "]");
      }
    }
    Customer customer = CustomerMapping.customerAddRequest2Entity(request);
    customerMapper.addCustomer(customer);
    return customer.getCustomerId();
  }

  @Override
  @Transactional("healthDataTransactionManager")
  public Integer addDependant(DependantAddRequest request) {
    int count = customerMapper.addCustomerDependant(request);
    quoteService.createInitQuote(request.getCustomerId(), request.getAgentId(), request.getQuoteId(), request.isOnlyChild(), request.getProductId());
    return count;
  }

  @Override
  public CustomerDetailResponse getCustomer(CustomerSearchRequest request) {

    CustomerDetailResponse result = null;
    if (request.getCustomerId() != null) {
      Customer customer = customerMapper.getCustomerByCustomerId(request.getCustomerId());
      logger.info("---> customer object from getCustomer() = {}", customer);
      if (customer != null) {
        Quote quote = quoteService
                .getQuoteNoThrowException(request.getQuoteId(), request.getCustomerId(), null);
        result = convertCustomer2Response(customer, quote);
        List<Dependant> childrenList = new ArrayList<>();
        setSpouseAndChildren(request, result, childrenList);
        if (quote != null) {
          result.setQuoteId(quote.getId());
          result.setOnlyChild(quote.isChildrenOnly());
        }
      }
    }
    return result;
  }

  private void setSpouseAndChildren(CustomerSearchRequest request, CustomerDetailResponse result,
      List<Dependant> childrenList) {
    if (request.getQuoteId() != null) {
      List<Dependant> list = customerMapper
          .getCustomerDependant(request.getCustomerId(), request.getQuoteId());
      for (Dependant dependant : list) {
        switch (DependantRelationship.getRelationship(dependant.getRelationship())) {
          case SPOUSE:
            result.setSpouse(dependant);
            break;
          case MARRIED_CHILD:
          case UNMARRIED_CHILD:
            childrenList.add(dependant);
            break;
          default:
        }
      }
      if (result.getChildren() != null) {
        result.getChildren().setDetail(childrenList);
      }
    }
  }

  /**
   * System need add default dependants for customer's all of quotes when user first time add
   * dependant. After that, system have to add/update each single quote's dependant depend on
   * request param.
   */
  @Override
  @Transactional("healthDataTransactionManager")
  public CustomerDetailResponse updateCustomer(CustomerUpdateRequest request) {
    boolean boolRemoveChildren = request.isDeleteChildrenRequest();
    boolean boolUpdateNumberOfChildren = request.isUpdateNumberOfChildren();
    boolean boolRemoveSpouse = request.isDeleteSpouse();

    if (boolRemoveSpouse && request.getSpouse() != null){
      removeSpouse(request);
      request.setSpouse(null);
    }else{
      if(request.getSpouse() != null) {
        Dependant spouse = request.getSpouse();
        Customer c = Customer
                .builder()
                .spouseSummary(
                        Dependant.builder()
                                .gender(spouse.getGender())
                                .lastName(spouse.getLastName())
                                .firstName(spouse.getFirstName())
                                .title(spouse.getTitle())
                                .dependantCode(spouse.getDependantCode())
                                .dateOfBirth(spouse.getDateOfBirth())
                                .build()
                ).build();
        request.setSpouse(c.getSpouseSummary());
        int update = customerMapper.updateSpouseSummary(c.getSpouseSummary(), request.getCustomerId());
        logger.info(" Updated spouse : {}" ,update);
      }
    }

    if(boolRemoveChildren){
      removeChild(request);
      request.setChildren(null);
    }

    if(boolUpdateNumberOfChildren && request.getChildren() != null){
      updateNumberOfChildren(request);
    }

    customerMapper.updatePrincipalCustomer(prepareUpdatePrincipalParam(request));
    Customer existingSpouse  = customerMapper.getSpouseByParentId(request.getCustomerId(), SPOUSE.getValue());
    if(existingSpouse == null && request.getSpouse() != null){
      String principalId = request.getCustomerId();
      Dependant spouse = request.getSpouse();
      String spouseQuoteId = quoteMapper.getSpouseQuoteId(principalId);
      Customer customer = Customer.builder()
              .gender(spouse.getGender())
              .customerId(spouse.getDependantCode())
              .relationshipDesc(SPOUSE.getValue())
              .dateOfBirth(spouse.getDateOfBirth())
              .firstName(spouse.getFirstName())
              .lastName(spouse.getLastName())
              .title(spouse.getTitle())
              .parentId(spouseQuoteId)
              .build();
      customerMapper.addCustomer(customer);
    }

    List<String> needInitQuote = sortUpQuoteList(request);
    List<Customer> dependantList;
    //Follow currently standard business logic if neeInitQuote is not empty it means user update customer details on the first time.
    //There is no principal's dependant store in database. Need initialize default dependants for principal's all quotes.
    if (needInitQuote.isEmpty()) {
      dependantList = convertRequestToDependantList(request, request.getQuoteId());
      List<Customer> newDependants = new ArrayList<>();
      List<Customer> customers = customerMapper.getCustomerByParentId(request.getCustomerId())
              .stream()
              .filter(r -> r.getRelationshipDesc().equalsIgnoreCase(SPOUSE.getValue())
              ).collect(Collectors.toList());

      if(!customers.isEmpty() && customers.size() > 1) {
        int removed = customerMapper.removeTemporary(request.getCustomerId());
        logger.info(" ==== removed customer details : {}", removed);
      }

      for (Customer dependant : dependantList) {
        logger.info(" ==== customer details : {}", gson.toJson(dependant));
        if (dependant.getCustomerId() != null && !dependant.getCustomerId().isEmpty()) {
          dependant.setFirstName(dependant.getFirstName());
          dependant.setLastName(dependant.getLastName());
          customerMapper.updateDependant(dependant);
          logger.info(" ==== updated spouse with details : {}", gson.toJson(dependant));
        } else {
          newDependants.add(dependant);
          logger.info(" ==== added dependants with details : {}", gson.toJson(dependant));
        }
      }

      if (!newDependants.isEmpty()) {
        customerMapper.addDependant(newDependants);
        logger.info(" ==== added dependants with details : {}", gson.toJson(newDependants));

      }

      if (request.getChildren() != null || request.getSpouse() != null) {
        Customer updatePrincipalWithDependentSummary = prepareChildAndSpouseSummary(request);
        customerMapper.updatePrincipalCustomer(updatePrincipalWithDependentSummary);
      }

    } else {
      for (String prepareAddQuoteId : needInitQuote) {
        dependantList = convertRequestToDependantList(request, prepareAddQuoteId);
        if (!CollectionUtils.isEmpty(dependantList)) {
          logger.info("---> dependentList {}", dependantList);
          customerMapper.addDependant(dependantList);
        }
      }
    }

    if (request.getChildren() != null || request.getSpouse() != null) {
      Customer updatePrincipalWithDependentSummary = prepareChildAndSpouseSummary(request);
      customerMapper.updatePrincipalCustomer(updatePrincipalWithDependentSummary);
    }


    if(boolRemoveSpouse){
      List<Dependant> p = customerMapper.getCustomerDependant(request.getCustomerId(), request.getQuoteId());
      p.forEach(dependant -> {
        if(dependant.getRelationship().equalsIgnoreCase(SPOUSE.getValue())){
          customerMapper.deleteDependantsByCustomerId(dependant.getDependantCode());
        }
      });
    }

    quoteService.updateQuotePremiumByCustomer(request.getQuoteId(), request.getCustomerId(), null);
    logger.info("\n ==== created step {} : for quote {} ==== \n " , HealthQuoteStepsEnum.CUSTOMER_DETAILS, request.getQuoteId());
    stepRepository.save(HealthStepEntity
            .builder()
            .quoteOrPolicyId(request.getQuoteId())
            .agentId(request.getAgentId())
            .customerId(request.getCustomerId())
            .step(HealthQuoteStepsEnum.CUSTOMER_DETAILS)
            .build());

    return getCustomer(CustomerSearchRequest.builder().customerId(request.getCustomerId())
            .quoteId(request.getQuoteId()).build());
  }

  private Customer prepareChildAndSpouseSummary(CustomerUpdateRequest request) {
    Customer customer = new Customer();
    if (request.getChildren() != null) {
       customer = Customer.builder()
              .childrenSummary(
                      Children.builder()
                              .count(request.getChildren().getCount())
                              .build())
              .customerId(request.getCustomerId())
              .build();
    }

    if(request.getSpouse() != null) {
      customer =  Customer.builder()
              .spouseSummary(Dependant.builder().dateOfBirth(request.getSpouse().getDateOfBirth()).build())
              .build();
    }

    return customer;
  }

  private void removeSpouse(CustomerUpdateRequest request) {
      customerMapper.deleteDependantsByCustomerId(request.getSpouse().getDependantCode());
      customerMapper.removeSpouseFromPrincipal(request.getCustomerId());
      CustomerDetailResponse response = getCustomer(CustomerSearchRequest.builder().customerId(request.getCustomerId())
              .quoteId(request.getQuoteId()).build());
      response.setUpdateDependent(UpdateDependentEnum.SPOUSE_REMOVED.getValue());
  }

  private void removeChild(CustomerUpdateRequest request) {
    if(request.getChildren() != null){
      int childrenCount = request.getChildren().getCount();
      List<Dependant> childrenDetails = request.getChildren().getDetail();

      if (childrenCount > 0) {
        request.getChildren().setCount(0);
        updateNumberOfChildren(request);
      }
      customerMapper.removeChildFromPrincipal(request.getCustomerId());
      if (!childrenDetails.isEmpty()) {
        for (Dependant dependant : childrenDetails) {
          customerMapper.deleteDependantsByCustomerId(dependant.getDependantCode());
        }
      }
    }

    CustomerDetailResponse response = getCustomer(CustomerSearchRequest.builder().customerId(request.getCustomerId())
            .quoteId(request.getQuoteId()).build());
    response.setUpdateDependent(UpdateDependentEnum.CHILD_REMOVED.getValue());
  }

  private CustomerDetailResponse updateNumberOfChildren(CustomerUpdateRequest request) {
    customerMapper.updateNumberOfChildren(
            Children.builder().count(request.getChildren().getCount()).build(),
            request.getCustomerId());
    CustomerDetailResponse response = getCustomer(CustomerSearchRequest.builder().customerId(request.getCustomerId())
            .quoteId(request.getQuoteId()).build());
    response.setUpdateDependent(UpdateDependentEnum.CHILDREN_AMENDED.getValue());
    return response;
  }

  private List<String> sortUpQuoteList(CustomerUpdateRequest request) {
    List<String> customerQuotes = customerMapper
        .getCustomerDependantQuoteList(request.getCustomerId()); //will be zero if no dependents and thus groupByDependents is also zero and thus needInitQuote will be NOT BE empty
    List<Quote> quotes = quoteService.getQuoteList(
        QuoteListRequest.builder().customerId(request.getCustomerId()).build());

    Map<String, String> groupByDependant = new HashMap<>();
    for (String quoteId : customerQuotes) {
      groupByDependant.put(quoteId, quoteId);
    }
    List<String> needInitQuote = new ArrayList<>();
    for (Quote quote : quotes) {
      if (!groupByDependant.containsKey(quote.getId())) {
        needInitQuote.add(quote.getId());
      }
    }
    return needInitQuote;
  }

  private List<Customer> convertRequestToDependantList(CustomerUpdateRequest request,
      String prepareAddQuoteId) {
    List<Customer> dependantList = new ArrayList<>();
    Dependant spouse = request.getSpouse();
    prepareAddDependant(request, dependantList, spouse, SPOUSE,
        prepareAddQuoteId);
    Children children = request.getChildren();
    if (children != null) {
      List<Dependant> dependants = children.getDetail();
      for (Dependant dependant : dependants) {
        prepareAddDependant(request, dependantList, dependant, DependantRelationship.UNMARRIED_CHILD,
            prepareAddQuoteId);
      }
    }
    return dependantList;
  }

  @Override
  public int deleteDependant(DependantDeleteRequest request) {
    return customerMapper
        .deleteDependants(request.getCustomerId(), request.getQuoteId(), request.getAgentId());
  }

  /**
   * Extract client and dependant data send to Actisure. And receive response entityId update
   * correspond customer records.
   */
  @Override
  @Transactional("healthDataTransactionManager")
  public Long addClientAndDependantToBase(String customerId, String quoteId) {
    Long principalEntityId = null;
    Customer customer = customerMapper.getCustomerByCustomerId(customerId);

    if (customer.getEntityId() == null) {
      //1. Add principal member to Actisure
      AddClientEntityRequest request = this.prepareAddClientEntityRequest(customer);
      AddClientEntityResponse response = customerRemote.addClientEntity(request);
      logger.debug("1. Add principal member response {}", response);
      principalEntityId = appendEntityIdToCustomer(principalEntityId, response);
    } else {
      principalEntityId = customer.getEntityId();
    }

    //2. Add dependant to Actisure -- addClientEntity
    List<DependantDetails> dependantDetails = customerMapper.getCustomerDependantDetails(customerId, quoteId);

    customerMapper.getCustomerByCustomerId(customerId);
    List<AddClientEntityRequest> requestList = new ArrayList<>();
    for (DependantDetails dependantTemp : dependantDetails) {
      if (dependantTemp.getEntityId() == null) {
        AddClientEntityRequest dependantRequest = prepareRemoteDependantRequest(principalEntityId, dependantTemp);
        requestList.add(dependantRequest);
      }
    }
    List<AddClientEntityResponse> dependants = null;
    if (CollectionUtils.isNotEmpty(requestList)) {
      dependants = customerRemote.addDependant(requestList);
    }
    logger.debug("2. Add dependant response {}", dependants);

    //Sync entityId to Health customer records
    if (CollectionUtils.isNotEmpty(dependants)) {
      for (AddClientEntityResponse dependantResponse : dependants) {
        if (dependantResponse == null || !dependantResponse.isSuccess()) {
          throw new BusinessException("Add dependant client to base exception!");
        }
        customerMapper.appendEntityIdToCustomer(dependantResponse.getCustomerId(),
            dependantResponse.getEntityId());
      }
    }

    // 3. add Client Contact -- addContactDetails
    Quote quote = quoteService.getQuote(quoteId, customerId, null);
    List<Address> addresses = new ArrayList<>();
    Address address1 = Address.builder()
                              .territory(GlobalConstant.NATIONALITY_KENYA)
                              .addressId(0L)
                              .startDate(DateFormatUtils.format(quote.getStartDate(), GlobalConstant.YYYYMMDD))
                              .endDate(null)
                              .addressLine1(GlobalConstant.ADDRESS_PLACEHOLDER)
                              .addressLine2(GlobalConstant.ADDRESS_PLACEHOLDER)
                              .addressLine3(GlobalConstant.ADDRESS_PLACEHOLDER)
                              .addressLine4("")
                              .postCode("00100")
                              .addressType("Residential")
                              .latitude(0L)
                              .longitude(0L)
                              .build();
    addresses.add(address1);
    Address address2 = Address.builder()
                              .territory(GlobalConstant.NATIONALITY_KENYA)
                              .addressId(0L)
                              .startDate(DateFormatUtils.format(quote.getStartDate(), GlobalConstant.YYYYMMDD))
                              .endDate(null)
                              .addressLine1(customer.getEmail())
                              .addressLine2("")
                              .addressLine3("")
                              .addressLine4("")
                              .postCode("")
                              .addressType(GlobalConstant.LIST_ROLE_ADDITIONAL_INFO_EMAIL)
                              .latitude(0L)
                              .longitude(0L)
                              .build();
    addresses.add(address2);

    List<Phone> phones = new ArrayList<>();
    Phone phone = Phone.builder()
                       .phoneId(0L)
                       .phoneNumber(delCountryDialCodeFromPhone(customer.getPhoneNumber()))
                       .countryDialCode("254")
                       .regionDialCode("")
                       .phoneType(GlobalConstant.LIST_ROLE_ADDITIONAL_INFO_MOBILE)
                       .build();
    phones.add(phone);

    ContactDetails contactDetails = ContactDetails.builder()
                                                  .addresses(addresses)
                                                  .phones(phones)
                                                  .build();
    AddContactDetailsRequest request = AddContactDetailsRequest.builder()
                                                               .entityId(principalEntityId)
                                                               .contactDetails(contactDetails)
                                                               .build();
    AddContactDetailsResponse response = customerRemote.addClientContact(request);
    logger.debug("3. add Client Contact: {}", response);

    if (response == null || !response.isSuccess()) {
      String errorMsg = Optional.ofNullable(response)
                                .map(AddContactDetailsResponse::getErrorMessage)
                                .orElse(null);
      List<ActBusinessError> errors = Optional.ofNullable(response)
                                              .map(AddContactDetailsResponse::getErrors)
                                              .orElse(null);
      logger.error("Add contact details occurred error, response:{} ,errors:{}", errorMsg, errors);
    }
    logger.info("Add contact details successfully");
    return principalEntityId;
  }

  private static String delCountryDialCodeFromPhone(String phoneNumber) {
    if (StringUtils.startsWith(phoneNumber, "254")) {
      phoneNumber = StringUtils.removeStart(phoneNumber, "254");
    }
    return phoneNumber;
  }

  private Long appendEntityIdToCustomer(Long principalEntityId, AddClientEntityResponse response) {
    boolean callSuccess = false;
    if (response != null) {
      if (response.isSuccess()) {
        principalEntityId = response.getEntityId();
        customerMapper.appendEntityIdToCustomer(response.getCustomerId(), response.getEntityId());
        callSuccess = true;
      } else {
        logger.error("Add client entity occurred error , errorMessage:{} ,errors:{}",
            response.getErrorMessage(), response.getErrors());
      }
    }
    if (!callSuccess) {
      throw new BusinessException("Add principal client to base exception!");
    }
    return principalEntityId;
  }

  @Override
  public List<Customer> getCustomerAndDependants(String customerId, String quoteId) {
    return customerMapper.getCustomerAndDependants(customerId, quoteId);
  }

  @Override
  public Customer getCustomer(String customerId) {
    if (customerId != null) {
      return customerMapper.getCustomerByCustomerId(customerId);
    } else {
      throw new BusinessException("Customer Id is mandatory!");
    }
  }

  @Override
  public List<Customer> getCustomerByParentId(String parentId) {
    if (parentId != null) {
      return customerMapper.getCustomerByParentId(parentId);
    } else {
      throw new BusinessException("Customer Parent Id is mandatory!");
    }
  }

  @Override
  public List<Customer> getCustomerList(CustomerListRequest request) {
    return customerMapper.getCustomerList(Customer.builder().agentId(request.getAgentId()).build());
  }

  @Override
  @Transactional("healthDataTransactionManager")
  public CustomerCreateResponse createCustomerAndQuote(CustomerCreateRequest request) {

    if (request.isOnlyChild() && request.getChildren().getCount() < 1) {
      throw new BusinessException("Child only policy needs at least to add one child");
    }
    Customer customer = CustomerMapping.customerCreateRequest2Entity(request);
    if (customer.getCustomerId().isEmpty()){
      customerMapper.createCustomer(customer);
    } else {
      customerMapper.updateCustomer(customer);
    }

    request.setCustomerId(customer.getCustomerId());

    List<Quote> quotes = quoteService
        .createInitQuote(request.getCustomerId(), request.getAgentId(), request.getQuoteId(), request.isOnlyChild(), request.getProductId());

    logger.info("==== These are the quotes {}", quotes);
    Optional<Quote> jamiPlus = quotes.stream()
        .filter(quote -> ProductEnum.JAMIIPLUS.getId() == quote.getProductId() ||
                ProductEnum.JAMIIPLUS_SHARED.getId() == quote.getProductId())
        .findFirst();

    return prepareQuote(request, jamiPlus);
  }

  private CustomerCreateResponse prepareQuote(CustomerCreateRequest request,
      Optional<Quote> jamiPlus) {
    if (jamiPlus.isPresent()) {
      Quote quote = jamiPlus.get();
      return CustomerCreateResponse.builder().customerId(request.getCustomerId()).quote(
          QuoteBean.builder().quoteId(quote.getId()).quoteNumber(quote.getCode())
              .balance(quote.getBalance()).benefit(quote.getBenefit())
              .premium(quote.getPremium()).productId(String.valueOf(quote.getProductId()))
              .startDate(DateFormatUtils.format(quote.getStartDate(), GlobalConstant.YYYYMMDD))
              .status(quote.getStatus()).build()).build();
    } else {
      throw new BusinessException("Can not find Jamiplus product");
    }

  }

  @Override
  public boolean addPhoneForCustomer(CustomerAddPhoneRequest request) {

    int result = customerMapper
        .upgradeCustomer(Customer.builder().customerId(request.getCustomerId())
            .phoneNumber(request.getPhoneNumber()).build());

    stepRepository.save(HealthStepEntity
            .builder()
            .step(HealthQuoteStepsEnum.AGENT_SEND_TO_PRINCIPAL)
            .quoteOrPolicyId(request.getQuoteId())
            .agentId(request.getAgentId())
            .customerId(request.getCustomerId())
            .build());

    return result == 1;
  }

  @Override
  public boolean addSuperCustomerIdForCustomer(CustomerAddSuperIdRequest request) {

    int result = customerMapper
        .upgradeCustomer(Customer.builder().customerId(request.getCustomerId())
            .superCustomerId(request.getSuperCustomerId()).build());
    quoteMapper.updateQuotStatus(request.getCustomerId());

    return result == 1;
  }

  @Override
  public Customer findBySuperCustomerId(CustomerSuperIdRequest request) {
    return customerMapper.getCustomerBySuperId(request.getSuperCustomerId());
  }

  @Override
  public GetCustomerInfoResponse getCustomerByPhoneNumber(GetCustomerByPhoneNoRequest request) {

    List<Customer> customers = customerMapper
        .getCustomerList(Customer.builder().phoneNumber(request.getPhoneNumber()).build());

    return fulfillGetCustomerInfoResponse(
        customers);
  }

  @Override
  public GetCustomerInfoResponse getCustomerByEntityId(Long entityId) {
    GetCustomerInfoResponse result = null;
    List<Customer> customers = customerMapper
        .getCustomerList(Customer.builder().entityId(entityId).build());
    if (CollectionUtils.isNotEmpty(customers)) {
      if (customers.size() > 1) {
        throw new BusinessException("There are more than one " + entityId + " customer record");
      } else {
        result = fulfillGetCustomerInfoResponse(customers);
      }
    }
    return result;
  }

  private GetCustomerInfoResponse fulfillGetCustomerInfoResponse(List<Customer> customers) {
    GetCustomerInfoResponse result = null;
    if (CollectionUtils.isNotEmpty(customers)) {
      Customer customer = customers.get(0);
      result = GetCustomerInfoResponse.builder().customerId(customer.getCustomerId())
          .dateOfBirth(DateFormatUtils.format(customer.getDateOfBirth(),
              GlobalConstant.YYYYMMDD)).firstName(customer.getFirstName())
          .lastName(customer.getLastName()).gender(customer.getGender())
          .title(customer.getTitle()).agentId(customer.getAgentId()).type("health").build();
    }
    return result;
  }

  private AddClientEntityRequest prepareRemoteDependantRequest(Long principalEntityId,
                                                               DependantDetails dependantTemp) {

    AddClientEntityRequest addClientEntityRequest = AddClientEntityRequest.builder()
                                                                          .dateOfBirth(dependantTemp.getDateOfBirth())
                                                                          .firstName(dependantTemp.getFirstName())
                                                                          .gender(dependantTemp.getGender())
                                                                          .nationality(GlobalConstant.NATIONALITY_KENYA)
                                                                          .surname(dependantTemp.getLastName())
                                                                          .initials("")
                                                                          .title(dependantTemp.getTitle())
                                                                          .occupation("Other - Not Defined")
                                                                          .relationshipDescription(dependantTemp.getRelationship())
                                                                          //        .relationshipEffectiveDate(new Date())
                                                                          .relationshipEffectiveDate(dependantTemp.getDateOfBirth()) // it should be optional
                                                                          .parentId(principalEntityId)
                                                                          .customerId(dependantTemp.getDependantCode())
                                                                          .build();
    if (StringUtils.isNotEmpty(dependantTemp.getEmail())) {
      RoleAdditionalInfo roleEmail = RoleAdditionalInfo.builder()
                                                                .description(GlobalConstant.LIST_ROLE_ADDITIONAL_INFO_EMAIL)
                                                                .value(dependantTemp.getEmail())
                                                                .build();

      addClientEntityRequest.setListRoleAdditionalInfo(Stream.of(roleEmail).collect(Collectors.toList()));
    }

    return addClientEntityRequest;
  }


  private AddClientEntityRequest prepareAddClientEntityRequest(Customer customer) {
    AddClientEntityRequest request = AddClientEntityRequest.builder()
                                                           //        .parentId(0L).relationshipDescription(customer.getRelationshipDesc())
                                                           //        .relationshipEffectiveDate(new Date())
                                                           .title(customer.getTitle())
                                                           .firstName(customer.getFirstName())
                                                           .surname(customer.getLastName())
                                                           .initials("")
                                                           .gender(customer.getGender())
                                                           .dateOfBirth(customer.getDateOfBirth())
                                                           .occupation("Other - Not Defined")
                                                           .nationality(GlobalConstant.NATIONALITY_KENYA)
                                                           .customerId(customer.getCustomerId())
                                                           .build();
    List<RoleAdditionalInfo> listRole = new ArrayList<>();
    listRole.add(
        RoleAdditionalInfo.builder()
                          .description(GlobalConstant.LIST_ROLE_ADDITIONAL_INFO_KRA_PIN_NO)
                          .value(customer.getKraPin())
                          .build());
    listRole.add(
        RoleAdditionalInfo.builder()
                          .description(GlobalConstant.LIST_ROLE_ADDITIONAL_INFO_ID_NO)
                          .value(customer.getIdNo())
                          .build());
    listRole.add(
        RoleAdditionalInfo.builder()
                          .description(GlobalConstant.LIST_ROLE_ADDITIONAL_INFO_EMAIL)
                          .value(customer.getEmail())
                          .build());
    request.setListRoleAdditionalInfo(listRole);
    return request;
  }

  private void prepareAddDependant(CustomerUpdateRequest request, List<Customer> dependantList,
      Dependant tempDependant, DependantRelationship relationship, String quoteId) {
    if (tempDependant != null) {
      Customer dependant = Customer.builder().firstName(tempDependant.getFirstName())
          .lastName(tempDependant.getLastName()).dateOfBirth(tempDependant.getDateOfBirth())
          .title(tempDependant.getTitle()).gender(tempDependant.getGender())
          .parentId(request.getCustomerId()).relationshipDesc(relationship.getValue())
          .quoteId(quoteId).customerId(tempDependant.getDependantCode()).build();
      dependantList.add(dependant);
    }
  }

  private Customer prepareUpdatePrincipalParam(CustomerUpdateRequest request) {
    PrincipalBean principal = request.getPrincipal();
    return Customer.builder().agentId(null).firstName(principal.getFirstName())
        .lastName(principal.getLastName()).dateOfBirth(principal.getDateOfBirth())
        .title(principal.getTitle()).gender(principal.getGender())
        .phoneNumber(principal.getPhoneNumber()).email(principal.getEmail())
        .idNo(principal.getIdNo()).kraPin(principal.getKraPin()).customerId(request.getCustomerId())
        .build();
  }

  private CustomerDetailResponse convertCustomer2Response(Customer customer, Quote quote) {
    CustomerDetailResponse result;
    result = new CustomerDetailResponse();
    Principal p = new Principal();
    p.setDateOfBirth(customer.getDateOfBirth());
    p.setEntityId(customer.getEntityId());
    p.setEmail(customer.getEmail());
    p.setQuoteId(customer.getQuoteId());
    p.setFirstName(customer.getFirstName());
    p.setLastName(customer.getLastName());
    p.setGender(customer.getGender());
    p.setPhoneNumber(customer.getPhoneNumber());
    p.setTitle(customer.getTitle());
    p.setIdNo(customer.getIdNo());
    p.setKraPin(customer.getKraPin());
    p.setCustomerId(customer.getCustomerId());
    result.setPrincipal(p);
    result.setQuoteDetails(quote);
    result.setCustomerId(customer.getCustomerId());
    result.setQuoteId(customer.getQuoteId());
    result.setSpouse(customer.getSpouseSummary());
    result.setChildren(customer.getChildrenSummary());
    result.setBenefit(customer.getBenefit());
    return result;
  }
}
