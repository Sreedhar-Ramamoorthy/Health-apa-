package ke.co.apollo.health.policy.remote.impl;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Date;

import com.google.gson.Gson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import ke.co.apollo.health.common.domain.model.request.AddBeneficiariesToPolicyRequest;
import ke.co.apollo.health.common.domain.model.response.ASAPIResponse;
import ke.co.apollo.health.common.exception.BusinessException;
public class PolicyRemoteImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PolicyRemoteImpl policyRemoteImpl;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        ReflectionTestUtils.setField(policyRemoteImpl, "healthApisBaseUrl", "http://192.168.131.235");
        ReflectionTestUtils.setField(policyRemoteImpl, "gson", new Gson());
        }

    @Test
    public void testAddBeneficiariesToPolicy(){
        
        AddBeneficiariesToPolicyRequest addBenefitsToPolicyRequest = AddBeneficiariesToPolicyRequest.builder()
                .policyId(111)
                .policyEffectiveDate("2022-01-01")
                .entityId(222)
                .joinDate(new Date())
                .originalJoinDate(new Date())
                .build();

        ASAPIResponse resp = new ASAPIResponse();
        resp.setSuccess(true);
        resp.setErrorMessage("no error");
        resp.setErrors(null);

        Mockito
            .when(restTemplate.postForEntity(anyString(),any(),any()))
            .thenReturn(new ResponseEntity(resp, HttpStatus.OK));

        ASAPIResponse apiResponse = policyRemoteImpl.addBeneficiaryToPolicy(addBenefitsToPolicyRequest);

        assertNotNull(apiResponse);
    }


    @Test
    public void testAddBeneficiariesToPolicyNotSuccessful(){
        
        AddBeneficiariesToPolicyRequest addBenefitsToPolicyRequest = AddBeneficiariesToPolicyRequest.builder()
                .policyId(111)
                .policyEffectiveDate("2022-01-01")
                .entityId(222)
                .joinDate(new Date())
                .originalJoinDate(new Date())
                .build();

        ASAPIResponse resp = new ASAPIResponse();
            resp.setSuccess(false);
            resp.setErrorMessage("Failed");
            resp.setErrors(null);

        Mockito
            .when(restTemplate.postForEntity(anyString(),any(),any()))
            .thenReturn(new ResponseEntity(resp, HttpStatus.OK));

        ASAPIResponse apiResponse = policyRemoteImpl.addBeneficiaryToPolicy(addBenefitsToPolicyRequest);

        assertNotNull(apiResponse);
    }

    @Test
    public void testAddBeneficiariesToPolicyWithInvalidDate(){
        AddBeneficiariesToPolicyRequest addBenefitsToPolicyRequest = AddBeneficiariesToPolicyRequest.builder()
            .policyId(111)
            .policyEffectiveDate("2022")
            .entityId(222)
            .joinDate(new Date())
            .originalJoinDate(new Date())
            .build();

        ASAPIResponse resp = new ASAPIResponse();
            resp.setSuccess(true);
            resp.setErrorMessage("no error");
            resp.setErrors(null);


        Exception thrown =
                assertThrows(BusinessException.class,
                        () -> policyRemoteImpl.addBeneficiaryToPolicy(addBenefitsToPolicyRequest),
                        "Unparseable date: \"2022\"");
        assertNotNull(thrown.getMessage());

        }
        


        @Test
        public void testAddBeneficiariesToPolicyRestTemplateFailure(){
            AddBeneficiariesToPolicyRequest addBenefitsToPolicyRequest = AddBeneficiariesToPolicyRequest.builder()
                .policyId(111)
                .policyEffectiveDate("2022-01-01")
                .entityId(222)
                .joinDate(new Date())
                .originalJoinDate(new Date())
                .build();
    
            ASAPIResponse resp = new ASAPIResponse();
                resp.setSuccess(true);
                resp.setErrorMessage("no error");
                resp.setErrors(null);
    
    
            Exception thrown =
                    assertThrows(Exception.class,
                            () -> policyRemoteImpl.addBeneficiaryToPolicy(addBenefitsToPolicyRequest),
                            "Error");
            assertNotNull(thrown);
    
            }
}
