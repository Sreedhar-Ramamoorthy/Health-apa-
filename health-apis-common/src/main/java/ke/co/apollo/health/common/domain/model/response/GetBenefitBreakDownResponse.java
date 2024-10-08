package ke.co.apollo.health.common.domain.model.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetBenefitBreakDownResponse extends ASAPIResponse {

  private String beneficiaryName;
  private BenefitUsageCurrency benefitUsageCurrency;
  private int sumInsuredTotal;
  private int sumInsuredRemaining;
  private List<BenefitItems> items;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class BenefitUsageCurrency {

    private int id;
    private String description;
    private String code;

  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class BenefitItems {

    private String type;
    private int id;
    private String description;
    private String limitOccurrence;
    private Object benefitOccurrence;
    private int benefitLimit;
    private int amountPaid;
    private int amountProcessed;
    private int amountLoaded;
    private int amountOther;
    private int amountTotal;
    private int preAuthOutstanding;
    private int preAuthLoaded;
    private int exGratia;
    private int exGratiaConsuming;
    private int benefitRemaining;
    private int benefitPerAnnum;
    private Object sessionLimit;
    private int totalSessions;
    private Object sessionsPerAnnum;
    private Object pooledSessions;
    private Object pooledSessionUsage;
    private Object sessionsRemaining;
    private Object monetaryLimitPerSession;
    private boolean hasNextLevel;
    private Object sumInsuredPercentage;
    private Object sumInsuredTotal;
    private Object sumInsuredRemaining;
    private int sumInsuredUsage;
    private boolean isSumInsured;
    private List<?> paidInvoiceIds;
    private List<?> processedInvoiceIds;
    private List<?> loadedInvoiceIds;
    private List<BenefitItems> subItems;

  }

}
