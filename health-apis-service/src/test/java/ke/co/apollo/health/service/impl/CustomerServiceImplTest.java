package ke.co.apollo.health.service.impl;

import com.google.gson.Gson;
import ke.co.apollo.health.common.CommonObjects;
import ke.co.apollo.health.common.domain.model.Children;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.Dependant;
import ke.co.apollo.health.common.domain.model.Quote;
import ke.co.apollo.health.common.domain.model.request.CustomerAddSuperIdRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerCreateRequest;
import ke.co.apollo.health.common.domain.model.response.CustomerCreateResponse;
import ke.co.apollo.health.common.enums.ProductEnum;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.domain.request.CustomerUpdateRequest;
import ke.co.apollo.health.domain.response.CustomerDetailResponse;
import ke.co.apollo.health.mapper.health.CustomerMapper;
import ke.co.apollo.health.mapper.health.QuoteMapper;
import ke.co.apollo.health.repository.HealthStepRepository;
import ke.co.apollo.health.service.QuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class CustomerServiceImplTest {

    @InjectMocks
    CustomerServiceImpl customerService;

    @Mock
    HealthStepRepository healthStepRepository;

    @Mock
    CustomerMapper customerMapper;

    @Mock
    QuoteMapper quoteMapper;

    @Mock
    QuoteService quoteService;

    @BeforeEach
    void setUpMocks(){
        initMocks(this);
        ReflectionTestUtils.setField(customerService, "gson", new Gson());
        when(healthStepRepository.save(any())).thenReturn(null);
        when(customerMapper.getSpouseByParentId(any(),any())).thenReturn(null);
        when(quoteMapper.getSpouseQuoteId(any())).thenReturn("SpouseId");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true,false})
    void testUpdateCustomerWhenRemoveSpouseIsTrueOrFalse(boolean removeSpouse) {
        if(removeSpouse) {
            // Arrange
            when(customerMapper.deleteDependantsByCustomerId(anyString())).thenReturn(1);
            when(customerMapper.removeChildFromPrincipal(anyString())).thenReturn(1);
            when(customerMapper.updateDependant(any())).thenReturn(1);
            when(customerMapper.addDependant(any())).thenReturn(1);
            when(customerMapper.updatePrincipalCustomer(any())).thenReturn(1);
            when(customerMapper.getCustomerByCustomerId(any())).thenReturn(CommonObjects.customer);
            when(quoteService
                    .getQuoteNoThrowException(any(),any(),any())).thenReturn(CommonObjects.quote);
            when(customerMapper
                    .getCustomerDependant(anyString(),anyString())).thenReturn(List.of(CommonObjects.dependant));
            Date date = new GregorianCalendar(1996, Calendar.FEBRUARY, 11).getTime();


            // Act
            CustomerDetailResponse response = customerService.updateCustomer(
                    CustomerUpdateRequest.builder()
                            .customerId("1234")
                            .principal(
                                    CustomerUpdateRequest.PrincipalBean.builder()
                                            .dateOfBirth(date)
                                            .firstName("Test")
                                            .build()
                            )
                            .deleteSpouse(removeSpouse)
                            .deleteChildrenRequest(false)
                            .updateNumberOfChildren(false)
                            .quoteId("q1234")
                            .spouse(Dependant.builder()
                                    .relationship("spouse")
                                    .dependantCode("spouse1234")
                                    .build())
                            .build()
            );

            //Assert
            assertNotNull(response);
        }

    }

    @ParameterizedTest()
    @ValueSource(booleans = {true,false})
    void testUpdateCustomerWhenRemoveSpouseIsTrueorFalseAndDependantCodeIsMissing(boolean removeSpouse) {
        if(removeSpouse) {
            when(customerMapper.getCustomerByCustomerId(any())).thenReturn(Customer.builder().gender("male").build());
            // Arrange
            Date date = new GregorianCalendar(1996, Calendar.FEBRUARY, 11).getTime();
            CustomerUpdateRequest request = CustomerUpdateRequest.builder()
                    .customerId("1234")
                    .principal(
                            CustomerUpdateRequest.PrincipalBean.builder()
                                    .dateOfBirth(date)
                                    .firstName("Test")
                                    .build()
                    )
                    .deleteSpouse(removeSpouse)
                    .deleteChildrenRequest(false)
                    .updateNumberOfChildren(false)
                    .quoteId("q1234")
                    .spouse(Dependant.builder()
                            .dependantCode(null)
                            .build())
                    .build();

            // Act
            CustomerDetailResponse response = customerService.updateCustomer(request);


            assertNotNull(response);
        }

    }

    @ParameterizedTest
    @ValueSource(booleans = {true,false})
    void testUpdateCustomerWhenRemoveChildrenIsTrueOrFalse(boolean removeChildren) {
        // Arrange
        if(removeChildren) {
            when(customerMapper.deleteDependantsByCustomerId(anyString())).thenReturn(1);
            when(customerMapper.removeChildFromPrincipal(anyString())).thenReturn(1);
            when(customerMapper.updateDependant(any())).thenReturn(1);
            when(customerMapper.addDependant(any())).thenReturn(1);
            when(customerMapper.updatePrincipalCustomer(any())).thenReturn(1);
            when(customerMapper.getCustomerByCustomerId(any())).thenReturn(CommonObjects.customer);
            when(quoteService
                    .getQuoteNoThrowException(any(),any(),any())).thenReturn(CommonObjects.quote);
            when(customerMapper
                    .getCustomerDependant(anyString(),anyString())).thenReturn(List.of(CommonObjects.dependant));
            Date date = new GregorianCalendar(1996, Calendar.FEBRUARY, 11).getTime();


            //Act
            Dependant child = Dependant.builder()
                    .dependantCode("child123")
                    .build();
            List<Dependant> childList = List.of(child);

            CustomerDetailResponse response = customerService.updateCustomer(
                    CustomerUpdateRequest.builder()
                            .customerId("1234")
                            .principal(
                                    CustomerUpdateRequest.PrincipalBean.builder()
                                            .dateOfBirth(date)
                                            .firstName("Test")
                                            .build()
                            )
                            .deleteSpouse(false)
                            .updateNumberOfChildren(false)
                            .deleteChildrenRequest(removeChildren)
                            .quoteId("q1234")
                            .spouse(Dependant.builder().build())
                            .children(Children.builder()
                                    .count(0)
                                    .detail(childList)
                                    .build())
                            .build()
            );

            //Assert
            assertNotNull(response);

        }


    }

    @ParameterizedTest
    @ValueSource(booleans = {true,false})
    void testUpdateCustomerWhenUpdateNumberOfChildrenIsTrueOrFalse(boolean updateNumberOfChildren) {
        // Arrange
        if(updateNumberOfChildren) {
            when(customerMapper.updateNumberOfChildren(any(), anyString())).thenReturn(1);
            when(customerMapper.updateDependant(any())).thenReturn(1);
            when(customerMapper.addDependant(any())).thenReturn(1);
            when(customerMapper.updatePrincipalCustomer(any())).thenReturn(1);
            when(customerMapper.getCustomerByCustomerId(any())).thenReturn(CommonObjects.customer);
            when(quoteService
                    .getQuoteNoThrowException(any(),any(),any())).thenReturn(CommonObjects.quote);
            when(customerMapper
                    .getCustomerDependant(anyString(),anyString())).thenReturn(List.of(CommonObjects.dependant));
            Date date = new GregorianCalendar(1996, Calendar.FEBRUARY, 11).getTime();


            Dependant child = Dependant.builder()
                    .dependantCode("child123")
                    .build();
            List<Dependant> childList = List.of(child);
            //Act
            CustomerDetailResponse response = customerService.updateCustomer(
                    CustomerUpdateRequest.builder()
                            .customerId("1234")
                            .principal(
                                    CustomerUpdateRequest.PrincipalBean.builder()
                                            .dateOfBirth(date)
                                            .firstName("Test")
                                            .build()
                            )
                            .deleteSpouse(false)
                            .spouse(Dependant.builder().firstName("").lastName("").build())
                            .quoteId("q1234")
                            .deleteChildrenRequest(false)
                            .updateNumberOfChildren(updateNumberOfChildren)
                            .children(Children.builder()
                                    .count(1)
                                    .detail(childList)
                                    .build())
                            .build()
            );

            //Assert
            assertNotNull(response);
        }
    }


    @Test
    void testUpdateCustomer(){
        when(customerMapper.updatePrincipalCustomer(any())).thenReturn(1);
        when(customerMapper.getCustomerDependantQuoteList(any())).thenReturn(Collections.emptyList());
        when(quoteService.getQuoteList(any())).thenReturn(Collections.emptyList());
        when(quoteService.updateQuotePremiumByCustomer(any(),any(),any())).thenReturn(true);
        when(healthStepRepository.save(any())).thenReturn(null);
        CustomerDetailResponse resp = customerService.updateCustomer(CustomerUpdateRequest
                .builder()
                .principal(CustomerUpdateRequest
                        .PrincipalBean
                        .builder()
                        .firstName("Test")
                        .lastName("Last")
                        .dateOfBirth(new Date())
                        .title("Title")
                        .gender("M")
                        .phoneNumber("Phone")
                        .email("email")
                        .idNo("8899999")
                        .kraPin("AKSKSKKSKSKS")
                        .build()).build());
        assertNull(resp);
    }
//    @ParameterizedTest
//    @ValueSource(ints = {49, 52})
//    void createCustomerAndQuoteForDifferentProducts(Integer productId) {
//        Quote generalQuote = CommonObjects.quote;
//        generalQuote.setProductId(productId);
//        CustomerCreateRequest customerCreateRequest = CommonObjects.customerCreateRequest;
//        customerCreateRequest.setProductId(productId);
//        when(quoteService.createInitQuote(any(), any(), any(), anyBoolean(), anyInt())).thenReturn(List.of(generalQuote));
//        when(customerMapper.createCustomer(any())).thenReturn(1);
//
//        if (productId == 49) {
//            CustomerCreateResponse customerCreateResponse = customerService.createCustomerAndQuote(customerCreateRequest);
//            assertNotNull(customerCreateResponse);
//        } else {
//            customerCreateRequest.setProduct(ProductEnum.JAMIIPLUS_SHARED.getValue());
//            CustomerCreateResponse customerCreateResponse = customerService.createCustomerAndQuote(customerCreateRequest);
//            assertNotNull(customerCreateResponse);
//        }
//    }

    @Test
    void createCustomerAndQuoteEmptyCustomer(){
        CustomerCreateRequest customerCreateRequest1 = CommonObjects.customerCreateRequest;
        customerCreateRequest1.setCustomerId("");
        when(customerMapper.updateCustomer(any())).thenReturn(1);
        when(quoteService.createInitQuote(any(),any(),any(),anyBoolean(), anyInt())).thenReturn(List.of(CommonObjects.quote));

        CustomerCreateResponse customerCreateRequest = customerService.createCustomerAndQuote(customerCreateRequest1);

        assertNotNull(customerCreateRequest);

    }

    @Test
    void testAddPhoneForCustomer(){
       when(customerMapper.upgradeCustomer(any())).thenReturn(1);
       when(healthStepRepository.save(any())).thenReturn(null);
       boolean customerCreateRequest = customerService.addPhoneForCustomer(CommonObjects.customerAddPhoneRequest);
       assertTrue(customerCreateRequest);
    }
    @ParameterizedTest
    @EnumSource(ProductEnum.class)
    void addDependantForDifferentProducts(ProductEnum productEnum){

        when(customerMapper.addCustomerDependant(any())).thenReturn(1);
        CommonObjects.dependantAddRequest.setProductId(productEnum.getId());
        Integer response = customerService.addDependant(CommonObjects.dependantAddRequest);
        assertNotNull(response);
    }
    @Test
    void addSuperCustomerIdForCustomer(){

        when(customerMapper.upgradeCustomer(any())).thenReturn(1);
        when(quoteMapper.updateQuotStatus(any())).thenReturn(1);
        boolean response = customerService.addSuperCustomerIdForCustomer(CustomerAddSuperIdRequest.builder().build());
        assertTrue(response);
    }

    @Test
    void testGetCustomer(){
        when(customerMapper.getCustomerByCustomerId(any())).thenReturn(CommonObjects.customer);
        when(quoteService
                .getQuoteNoThrowException(any(),any(),any())).thenReturn(CommonObjects.quote);
        when(customerMapper
                .getCustomerDependant(anyString(),anyString())).thenReturn(List.of(CommonObjects.dependant));

        CustomerDetailResponse result = customerService.getCustomer(CommonObjects.customerSearchRequest);
        assertNotNull(result);
    }

    @Test
    void testCreateCustomerQuoteThrowsExceptionIfRequestIsOnlyChild() {
        CustomerCreateRequest request = CommonObjects.customerCreateRequest;
        request.getChildren().setCount(0);
        request.setOnlyChild(true);
        String expectedMessage = "Child only policy needs at least to add one child";

        Exception exception = assertThrows(BusinessException.class, () -> {
            customerService.createCustomerAndQuote(request);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

//    @Test
//    void testCreateCustomerQuoteWhenCustomerIdIsNotEmpty() {
//        CustomerCreateRequest request = CommonObjects.customerCreateRequest;
//
//    }

}
