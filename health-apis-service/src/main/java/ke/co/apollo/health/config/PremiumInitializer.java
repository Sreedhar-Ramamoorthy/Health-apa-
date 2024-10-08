package ke.co.apollo.health.config;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ke.co.apollo.health.common.domain.model.ProductPremium;
import ke.co.apollo.health.common.domain.model.ProductPremium.Health.Femina;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap.JamiiPlusMap;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap.JamiiPlusSharedMap;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap.JamiiPlusChildOnlyCoverMap;

import ke.co.apollo.health.common.domain.model.ProductPremium.Health.JamiiPlus;
import ke.co.apollo.health.common.domain.model.ProductPremium.Health.JamiiPlusShared;
import ke.co.apollo.health.common.domain.model.ProductPremium.Health.JamiiPlusChildOnly;



import ke.co.apollo.health.common.domain.model.ProductPremiumMap.FeminaMap;
import ke.co.apollo.health.common.domain.model.ProductPremiumMap.InpatientMap;
import ke.co.apollo.health.common.utils.JsonUtils;

@Configuration
@SuppressWarnings("squid:S1854")
public class PremiumInitializer {

  private static final Logger logger = LoggerFactory.getLogger(PremiumInitializer.class);

  @Bean
  public ProductPremium initProductPremium() {
    ProductPremium productPremium = JsonUtils
        .readJsonFromClassPath("product-premium.json", ProductPremium.class);
    logger.debug("productPremium: {}", productPremium);
    return productPremium;
  }

  private JamiiPlusChildOnlyCoverMap initJamiiPlusChildOnlyCoverMap(JamiiPlusChildOnly jpChildOnly) {
    JamiiPlusChildOnlyCoverMap jpChildOnlyMap = JamiiPlusChildOnlyCoverMap.builder().build();
    Map<Integer, Integer> inpatientMap = new HashMap<>();
    Map<Integer, Integer> outpatientMap = new HashMap<>();

    for(ProductPremium.Health.JamiiPlusChildOnly.Inpatient inpatient: jpChildOnly.getInpatient())
      inpatientMap.put(inpatient.getBenefit(), inpatient.getPremium());

    for(ProductPremium.Health.JamiiPlusChildOnly.Outpatient outpatient: jpChildOnly.getOutpatient())
      outpatientMap.put(outpatient.getBenefit(), outpatient.getPremium());

    jpChildOnlyMap.setInpatient(inpatientMap);
    jpChildOnlyMap.setOutpatient(outpatientMap);

    return jpChildOnlyMap;
  }

  private JamiiPlusMap initJamiiPlusMap(JamiiPlus jamiiPlus) {
    // Jamii Plus
    JamiiPlusMap jamiiPlusMap = JamiiPlusMap.builder()
        .build();

    // Inpatient
    InpatientMap inpatientMap = InpatientMap.builder().build();
    MultiKeyMap<Integer, Integer> principalMultiKeyMap = new MultiKeyMap<>();
    MultiKeyMap<Integer, Integer> spouseMultiKeyMap = new MultiKeyMap<>();
    Map<Integer, Integer> childMap = new HashMap<>();
    for (ProductPremium.Health.JamiiPlus.Inpatient inpatient : jamiiPlus.getInpatient()) {
      for (int i = 0; i < inpatient.getPrincipal().size(); i++) {
        principalMultiKeyMap.put(inpatient.getBenefit(), i, inpatient.getPrincipal().get(i));
        spouseMultiKeyMap.put(inpatient.getBenefit(), i, inpatient.getSpouse().get(i));

        // Inpatient Age
        if (CollectionUtils.isEmpty(jamiiPlusMap.getInpatientAge())) {
          jamiiPlusMap.setInpatientAge(inpatient.getAge());
        }

      }
      childMap.put(inpatient.getBenefit(), inpatient.getChild());

    }
    inpatientMap.setPrincipal(principalMultiKeyMap);
    inpatientMap.setSpouse(spouseMultiKeyMap);
    inpatientMap.setChild(childMap);
    jamiiPlusMap.setInpatient(inpatientMap);

    // Outpatient
    MultiKeyMap<Integer, Integer> multiKeyMap = new MultiKeyMap<>();
    for (ProductPremium.Health.JamiiPlus.Outpatient outpatient : jamiiPlus
        .getOutpatient()) {
      for (int i = 0; i < outpatient.getPremium().size(); i++) {
        multiKeyMap.put(outpatient.getBenefit(), i, outpatient.getPremium().get(i));
      }
      // Outpatient Age
      if (CollectionUtils.isEmpty(jamiiPlusMap.getOutpatientAge())) {
        jamiiPlusMap.setOutpatientAge(outpatient.getAge());
      }
    }
    jamiiPlusMap.setOutpatient(multiKeyMap);

    // Dental
    Map<Integer, Integer> dentalMap = new HashMap<>();
    for (ProductPremium.Health.JamiiPlus.Permium permium : jamiiPlus.getDental()) {
      dentalMap.put(permium.getBenefit(), permium.getPremium());
    }
    jamiiPlusMap.setDental(dentalMap);

    // Optical
    Map<Integer, Integer> opticalMap = new HashMap<>();
    for (ProductPremium.Health.JamiiPlus.Permium permium : jamiiPlus.getOptical()) {
      opticalMap.put(permium.getBenefit(), permium.getPremium());
    }
    jamiiPlusMap.setOptical(opticalMap);

    // Maternity
    Map<Integer, Integer> maternityMap = new HashMap<>();
    for (ProductPremium.Health.JamiiPlus.Permium permium : jamiiPlus.getMaternity()) {
      maternityMap.put(permium.getBenefit(), permium.getPremium());
    }
    jamiiPlusMap.setMaternity(maternityMap);

    // Travel
    Map<Integer, Integer> travelMap = new HashMap<>();
    ProductPremium.Health.JamiiPlus.Permium permium = jamiiPlus.getTravel();
    if (permium != null) {
      travelMap.put(permium.getBenefit(), permium.getPremium());
    }
    jamiiPlusMap.setTravel(travelMap);

    return jamiiPlusMap;
  }

  private JamiiPlusSharedMap initJamiiPlusSharedMap (JamiiPlusShared jamiiPlusShared) {
    // Jamii Plus
    JamiiPlusSharedMap jamiiPlusSharedMap = JamiiPlusSharedMap.builder().build();

    // Inpatient
    InpatientMap inpatientMap = InpatientMap.builder().build();
    MultiKeyMap<Integer, Integer> principalMultiKeyMap = new MultiKeyMap<>();
    MultiKeyMap<Integer, Integer> spouseMultiKeyMap = new MultiKeyMap<>();
    Map<Integer, Integer> childMap = new HashMap<>();
    for (JamiiPlusShared.Inpatient inpatientShared : jamiiPlusShared.getInpatient()) {
      for (int i = 0; i < inpatientShared.getPrincipal().size(); i++) {
        principalMultiKeyMap.put(inpatientShared.getBenefit(), i, inpatientShared.getPrincipal().get(i));
        spouseMultiKeyMap.put(inpatientShared.getBenefit(), i, inpatientShared.getSpouse().get(i));

        // Inpatient Age
        if (CollectionUtils.isEmpty(jamiiPlusSharedMap.getInpatientAge())) {
          jamiiPlusSharedMap.setInpatientAge(inpatientShared.getAge());
        }

      }
      childMap.put(inpatientShared.getBenefit(), inpatientShared.getChild());

    }
    inpatientMap.setPrincipal(principalMultiKeyMap);
    inpatientMap.setSpouse(spouseMultiKeyMap);
    inpatientMap.setChild(childMap);
    jamiiPlusSharedMap.setInpatient(inpatientMap);

    // Outpatient
    MultiKeyMap<Integer, Integer> multiKeyMap = new MultiKeyMap<>();
    for (JamiiPlusShared.Outpatient outpatient : jamiiPlusShared.getOutpatient()) {
      for (int i = 0; i < outpatient.getPremium().size(); i++) {
        multiKeyMap.put(outpatient.getBenefit(), i, outpatient.getPremium().get(i));
      }
      // Outpatient Age
      if (CollectionUtils.isEmpty(jamiiPlusSharedMap.getOutpatientAge())) {
        jamiiPlusSharedMap.setOutpatientAge(outpatient.getAge());
      }
    }
    jamiiPlusSharedMap.setOutpatient(multiKeyMap);

    // Dental
    Map<Integer, Integer> dentalMap = new HashMap<>();
    for (JamiiPlusShared.Permium permium : jamiiPlusShared.getDental()) {
      dentalMap.put(permium.getBenefit(), permium.getPremium());
    }
    jamiiPlusSharedMap.setDental(dentalMap);

    // Optical
    Map<Integer, Integer> opticalMap = new HashMap<>();
    for (JamiiPlusShared.Permium permium : jamiiPlusShared.getOptical()) {
      opticalMap.put(permium.getBenefit(), permium.getPremium());
    }
    jamiiPlusSharedMap.setOptical(opticalMap);

    // Maternity
    Map<Integer, Integer> maternityMap = new HashMap<>();
    for (JamiiPlusShared.Permium permium : jamiiPlusShared.getMaternity()) {
      maternityMap.put(permium.getBenefit(), permium.getPremium());
    }
    jamiiPlusSharedMap.setMaternity(maternityMap);

    // Travel
    Map<Integer, Integer> travelMap = new HashMap<>();
    JamiiPlusShared.Permium permium = jamiiPlusShared.getTravel();
    if (permium != null) {
      travelMap.put(permium.getBenefit(), permium.getPremium());
    }
    jamiiPlusSharedMap.setTravel(travelMap);

    return jamiiPlusSharedMap;
  }





  private ProductPremiumMap.AfyaNafuuMap initAfyaNafuuMap(
      ProductPremium.Health.AfyaNafuu afyaNafuu) {
    // Afya Nafuu
    ProductPremiumMap.AfyaNafuuMap afyaNafuuMap = ProductPremiumMap.AfyaNafuuMap.builder()
        .build();

    // Inpatient
    MultiKeyMap<Integer, Integer> multiKeyMap = new MultiKeyMap<>();
    for (ProductPremium.Health.AfyaNafuu.AgePermium inpatient : afyaNafuu
        .getInpatient()) {

      for (int i = 0; i < inpatient.getPremium().size(); i++) {
        multiKeyMap.put(inpatient.getBenefit(), i, inpatient.getPremium().get(i));
      }

      // Inpatient Age
      if (CollectionUtils.isEmpty(afyaNafuuMap.getInpatientAge())) {
        afyaNafuuMap.setInpatientAge(inpatient.getAge());
      }
    }
    afyaNafuuMap.setInpatient(multiKeyMap);

    // Outpatient
    MultiKeyMap<Integer, Integer> opMultiKeyMap = new MultiKeyMap<>();
    for (ProductPremium.Health.AfyaNafuu.AgePermium outpatient : afyaNafuu
        .getOutpatient()) {
      for (int i = 0; i < outpatient.getPremium().size(); i++) {
        opMultiKeyMap.put(outpatient.getBenefit(), i, outpatient.getPremium().get(i));
      }
      // Inpatient Age
      if (CollectionUtils.isEmpty(afyaNafuuMap.getOutpatientAge())) {
        afyaNafuuMap.setOutpatientAge(outpatient.getAge());
      }
    }
    afyaNafuuMap.setOutpatient(opMultiKeyMap);

    // Maternity
    Map<Integer, Integer> maternityMap = new HashMap<>();
    for (ProductPremium.Health.AfyaNafuu.Permium permium : afyaNafuu.getMaternity()) {
      maternityMap.put(permium.getBenefit(), permium.getPremium());
    }
    afyaNafuuMap.setMaternity(maternityMap);

    return afyaNafuuMap;
  }


  // Femina
  private FeminaMap initFeminaMap(List<Femina> feminaList) {
    FeminaMap feminaMap = FeminaMap.builder().build();
    MultiKeyMap<Integer, Integer> multiKeyMap = new MultiKeyMap<>();
    for (Femina permium : feminaList) {
      for (int i = 0; i < permium.getPremium().size(); i++) {
        multiKeyMap.put(permium.getBenefit(), i, permium.getPremium().get(i));
      }

      // Age
      if (CollectionUtils.isEmpty(feminaMap.getAge())) {
        feminaMap.setAge(permium.getAge());
      }
    }
    feminaMap.setFemina(multiKeyMap);
    return feminaMap;
  }

  @Bean
  public ProductPremiumMap initProductPremiumMap() {
    ProductPremiumMap productPremiumMap = ProductPremiumMap.builder().build();
    ProductPremium productPremium = initProductPremium();
    if (productPremium != null) {

      ProductPremiumMap.JamiiPlusMap jamiiPlusMap = initJamiiPlusMap(
          productPremium.getHealth().getJamiiPlus());
      ProductPremiumMap.AfyaNafuuMap afyaNafuuMap = initAfyaNafuuMap(
          productPremium.getHealth().getAfyaNafuu());
      FeminaMap feminaMap = initFeminaMap(
          productPremium.getHealth().getFemina());
      JamiiPlusSharedMap jamiiPlusSharedMap = initJamiiPlusSharedMap(
              productPremium.getHealth().getJamiiPlusShared());
      JamiiPlusChildOnlyCoverMap jpChildOnlyCoverMap = initJamiiPlusChildOnlyCoverMap(
              productPremium.getHealth().getJamiiPlusChildOnly());

      productPremiumMap.setJamiiPlusMap(jamiiPlusMap);
      productPremiumMap.setAfyaNafuuMap(afyaNafuuMap);
      productPremiumMap.setFeminaMap(feminaMap);
      productPremiumMap.setJamiiPlusSharedMap(jamiiPlusSharedMap);
      productPremiumMap.setChildOnlyCoverMap(jpChildOnlyCoverMap);
    }

    String productPremiumMapJson  =  new Gson().toJson(productPremiumMap);
    logger.debug("productPremiumMapJson: {}", productPremiumMapJson);

    return productPremiumMap;
  }

}
