package ke.co.apollo.health.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import ke.co.apollo.health.common.domain.model.ProductPremium;
import ke.co.apollo.health.common.domain.model.ApplicationRenewalPolicy;
import ke.co.apollo.health.common.domain.model.remote.PolicyNumberRequest;
import ke.co.apollo.health.common.domain.model.request.ComingPolicyListRequest;
import ke.co.apollo.health.common.domain.model.request.CustomerPolicyListRequest;
import ke.co.apollo.health.common.domain.model.request.EntityPolicyListRequest;
import ke.co.apollo.health.common.domain.model.request.PolicyRenewalBalanceRequest;
import ke.co.apollo.health.common.domain.model.request.PolicyRenewalRequest;
import ke.co.apollo.health.common.domain.model.response.ApplicationRenewalPolicyListResponse;
import ke.co.apollo.health.common.domain.model.response.PolicyOverComingResponseDto;
import ke.co.apollo.health.common.domain.model.response.PolicyRenewalResponse;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.domain.request.ApplicationPolicyListSearchRequest;
import ke.co.apollo.health.domain.request.CustomerIdRequest;
import ke.co.apollo.health.domain.request.PolicyComplaintRequest;
import ke.co.apollo.health.domain.response.HealthPolicyListResponse;
import ke.co.apollo.health.service.ComplaintService;
import ke.co.apollo.health.service.ProductService;

class ProductControllerTest {

    @InjectMocks
    ProductController productController;

    @Mock
    ProductService productService;



    @BeforeEach
    void setUpMocks(){
        initMocks(this);
    }

    @Test
    void getProductPremiumTest(){
        when(productService.getProductPremium()).thenReturn(ProductPremium.builder().build());
        ResponseEntity<DataWrapper> wrapper = productController.getProductPremium();
        assertNotNull(wrapper);
        }


    @Test
    void getProductNullPremiumTest(){
        when(productService.getProductPremium()).thenReturn(null);
        ResponseEntity<DataWrapper> wrapper = productController.getProductPremium();
        assertNotNull(wrapper);
        }

}
