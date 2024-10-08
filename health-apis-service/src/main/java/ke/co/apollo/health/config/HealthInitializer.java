package ke.co.apollo.health.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ke.co.apollo.health.common.domain.model.BenefitCategory;
import ke.co.apollo.health.common.domain.model.BenefitCategory.Benifit;
import ke.co.apollo.health.common.domain.model.BenefitCategoryMap;
import ke.co.apollo.health.common.domain.model.BenefitCategoryMap.Inpatient;
import ke.co.apollo.health.common.domain.model.BenefitCategoryMap.OptionalBenefits;
import ke.co.apollo.health.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HealthInitializer {

  private static final Logger logger = LoggerFactory.getLogger(HealthInitializer.class);

  @Bean
  public BenefitCategoryMap initBenefitCategory() {

    BenefitCategoryMap benefitCategoryMap = BenefitCategoryMap.builder().build();
    BenefitCategory benefitCategory = JsonUtils
        .readJsonFromClassPath("benefit-category.json", BenefitCategory.class);
    logger.debug("benefitCategory: {}", benefitCategory);
    if (benefitCategory != null) {
      benefitCategoryMap = BenefitCategoryMap.builder()
          .inpatient(Inpatient.builder()
              .family(listToMap(benefitCategory.getInpatient().getFamily()))
              .person(listToMap(benefitCategory.getInpatient().getPerson()))
              .childOnly(listToMap(benefitCategory.getInpatient().getChildOnly())).build())
          .optionalBenefits(OptionalBenefits.builder()
                  .outpatient(OptionalBenefits.Outpatient.builder()
                          .childOnly(listToMap(benefitCategory.getOptionalBenefits().getOutpatient().getChildOnly()))
                          .person(listToMap(benefitCategory.getOptionalBenefits().getOutpatient().getPerson()))
                          .build())
              .dental(listToMap(benefitCategory.getOptionalBenefits().getDental()))
              .maternity(listToMap(benefitCategory.getOptionalBenefits().getMaternity()))
              .optical(listToMap(benefitCategory.getOptionalBenefits().getOptical()))
              .build())
          .build();
      logger.debug("benefitCategoryMap: {}", benefitCategoryMap);
    }

    return benefitCategoryMap;

  }

  private Map<Integer, String> listToMap(List<Benifit> benifitList) {
    return benifitList.stream()
        .collect(Collectors.toMap(Benifit::getBenefit, Benifit::getName, (k1, k2) -> k1));
  }

}
