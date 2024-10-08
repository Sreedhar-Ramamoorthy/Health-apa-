package ke.co.apollo.health.policy.config;

import java.util.HashMap;
import ke.co.apollo.health.common.domain.model.BenefitBreakDownCategory;
import ke.co.apollo.health.common.enums.BenefitEnum;
import ke.co.apollo.health.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BenefitBreakDownCategoryInitializer {

  private static final Logger logger = LoggerFactory
      .getLogger(BenefitBreakDownCategoryInitializer.class);

  @Bean
  public BenefitBreakDownCategory convertBenefitBean() {

    BenefitBreakDownCategory benefitCategory = JsonUtils
        .readJsonFromClassPath("benefit-mapping.json", BenefitBreakDownCategory.class);
    HashMap<String, String> map = new HashMap<>();
    benefitCategory.getDental().forEach(benefit ->
        map.put(benefit.getName(), BenefitEnum.DENTAL.getValue())
    );
    benefitCategory.getMaternity().forEach(benefit ->
        map.put(benefit.getName(), BenefitEnum.MATERNITY.getValue())
    );

    benefitCategory.getOptical().forEach(benefit ->
        map.put(benefit.getName(), BenefitEnum.OPTICAL.getValue())
    );

    benefitCategory.getInpatient().forEach(benefit ->
        map.put(benefit.getName(), BenefitEnum.INPATIENT.getValue())
    );

    benefitCategory.getOutpatient().forEach(benefit ->
        map.put(benefit.getName(), BenefitEnum.OUTPATIENT.getValue())
    );

    benefitCategory.getTravel().forEach(benefit ->
        map.put(benefit.getName(), BenefitEnum.TRAVEL.getValue())
    );

    logger.debug("benefit category map {}", map);

    benefitCategory.setCategories(map);

    return benefitCategory;
  }


}
