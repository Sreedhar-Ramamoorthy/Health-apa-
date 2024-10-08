package ke.co.apollo.health.common.domain.model.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddIndividualPolicyBeneficiaryUWQuestionsRequest {

  private BeneficiaryUWQuestions BeneficiaryUnderwritingQuestions;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class BeneficiaryUWQuestions {

    private Long PolicyId;
    private String PolicyEffectiveDate;
    private Long EntityId;
    private List<UnderwritingQuestions> UnderwritingQuestions;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnderwritingQuestions {

      private String Question;
      private String Answer;
      private boolean DiscountApplicable;
      private String FreeFormatAnswer;
    }
  }
}
