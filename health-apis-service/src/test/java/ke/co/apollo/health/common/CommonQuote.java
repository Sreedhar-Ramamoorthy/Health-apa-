package ke.co.apollo.health.common;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.domain.model.*;
import ke.co.apollo.health.domain.request.HealthQuoteDownloadRequest;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CommonQuote {
    private CommonQuote() {

    }

    public static HealthQuoteDownloadRequest getHealthQuoteDownloadRequest() {
        return HealthQuoteDownloadRequest.builder()
                .customerId("9f617dae1f9ae06f3922246d34fb4070")
                .quoteCode("HJ-8142B45A")
                .build();
    }

    public static Customer getCustomer() {
        return Customer.builder()
                .customerId("9f617dae1f9ae06f3922246d34fb4070")
                .firstName("John")
                .lastName("Bala")
                .dateOfBirth(new Date())
                .phoneNumber("750756")
                .email("test@gmail.com")
                .gender("Male")
                .agentId("12")
                .spouseSummary(Dependant
                        .builder()
                        .dateOfBirth(new Date()).
                        build())
                .childrenSummary(Children
                        .builder()
                        .count(1)
                        .build())
                .build();
    }
    public static Customer getCustomerWithNullAgent() {
        return Customer.builder()
                .customerId("9f617dae1f9ae06f3922246d34fb4070")
                .firstName("John")
                .lastName("Bala")
                .dateOfBirth(null)
                .phoneNumber("750756")
                .email("test@gmail.com")
                .gender("Male")
                .agentId(null)
                .spouseSummary(Dependant
                        .builder()
                        .dateOfBirth(null).
                        build())
                .childrenSummary(Children
                        .builder()
                        .count(1)
                        .build())
                .build();
    }

    public static Customer getCustomerWithNullDependants() {
        return Customer.builder()
                .customerId("9f617dae1f9ae06f3922246d34fb4070")
                .firstName("John")
                .lastName("Bala")
                .dateOfBirth(null)
                .phoneNumber("750756")
                .email("test@gmail.com")
                .gender("Male")
                .agentId(null)
                .spouseSummary(null)
                .childrenSummary(null)
                .build();
    }

    public static Quote getQuote(){
        return Quote.builder().customerId("9f617dae1f9ae06f3922246d34fb4070")
                .code("HJ-8142B45A")
                .extPolicyNumber("111")
                .productId(52)
                .premium(Premium.builder().build())
                .benefit(getBenefit()).build();

    }
    public static Intermediary getIntermediary(){
        return Intermediary.builder()
                .firstName("John")
                .lastName("ss")
                .phoneNumber("750756")
                .email("test@gmail.com")
                .status("a")
                .branchName("b")
                .build();
    }
    public static Intermediary getIntermediaryNull(){
        return Intermediary.builder()
                .firstName("John")
                .lastName("ss")
                .phoneNumber("750756")
                .email("test@gmail.com")
                .status("a")
                .branchName(null)
                .build();
    }
    public static Quote getQuoteWithNull(){
        return Quote.builder().customerId("9f617dae1f9ae06f3922246d34fb4070")
                .code(null)
                .premium(Premium.builder().totalPremium(BigDecimal.valueOf(10.0)).build())
                .benefit(getBenefitWithNull()).build();

    }
    public static Intermediary getIntermediaryEmpty(){
        return Intermediary.builder()
                .firstName("John")
                .lastName("ss")
                .phoneNumber("750756")
                .email("test@gmail.com")
                .status("a")
                .branchName("")
                .build();
    }
    public static Quote getQuoteEmpty(){
        return Quote.builder().customerId("9f617dae1f9ae06f3922246d34fb4070")
                .code("")
                .premium(Premium.builder().totalPremium(BigDecimal.valueOf(10.0)).build())
                .benefit(getBenefitWithNoTravel()).build();

    }
    public static Benefit getBenefit(){
        return Benefit.builder()
                .inpatientLimit(100)
                .opticalLimit(100)
                .dentalLimit(100)
                .maternityLimit(100)
                .outpatientLimit(100)
                .travelInsurance(100)
                .build();

    }
    public static Benefit getBenefitWithNull(){
        return Benefit.builder()
                .inpatientLimit(null)
                .opticalLimit(null)
                .dentalLimit(null)
                .maternityLimit(null)
                .outpatientLimit(null)
                .travelInsurance(null).build();

    }
    public static Benefit getBenefitWithNoTravel(){
        return Benefit.builder()
                .inpatientLimit(null)
                .opticalLimit(null)
                .dentalLimit(null)
                .maternityLimit(null)
                .outpatientLimit(null)
                .travelInsurance(-1).build();

    }
}
