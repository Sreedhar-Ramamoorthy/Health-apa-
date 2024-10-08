package ke.co.apollo.health.utils;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtil {

  private CommonUtil() {}

  public static BigDecimal findLoadingPecentage(float lossRatio) {

      BigDecimal loadingPercentage;

      if(lossRatio >= 111){
        loadingPercentage = new BigDecimal(23);
        }
      else if(lossRatio >= 101){
          loadingPercentage = new BigDecimal(16);
      }
      else if(lossRatio >= 96){
          loadingPercentage = new BigDecimal(15);
      }
      else if(lossRatio >= 91){
          loadingPercentage = new BigDecimal(13);
      }
      else if(lossRatio >= 86){
          loadingPercentage = new BigDecimal(12);
      }
      else if(lossRatio >= 81){
          loadingPercentage = new BigDecimal(10);
      }
      else if(lossRatio >= 76){
          loadingPercentage = new BigDecimal(9);
      }
      else if(lossRatio >= 71){
          loadingPercentage = new BigDecimal(8);
      }
      else if(lossRatio >= 63){
          loadingPercentage = new BigDecimal(6);
      }
      else{
          loadingPercentage = BigDecimal.ZERO;
      }

      return loadingPercentage;
  }
}
