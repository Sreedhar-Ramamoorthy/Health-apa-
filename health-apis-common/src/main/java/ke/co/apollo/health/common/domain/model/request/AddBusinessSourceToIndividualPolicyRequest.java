package ke.co.apollo.health.common.domain.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBusinessSourceToIndividualPolicyRequest {

  @SerializedName(value = "BusinessSource")
  private BusinessSourceBean businessSource;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class BusinessSourceBean {
    @SerializedName(value = "PolicyId")
    private Integer policyId;
    @SerializedName(value = "PolicyEffectiveDate")
    private String policyEffectiveDate;
    @SerializedName(value = "BusinessSourceWebName")
    private String businessSourceWebName;
    @SerializedName(value = "BinderDetailWebName")
    private String binderDetailWebName;
    @SerializedName(value = "DisbursementBasis")
    private String disbursementBasis;
    @SerializedName(value = "InterestedParties")
    private List<InterestedPartiesBean> interestedParties;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InterestedPartiesBean {
      @SerializedName(value = "Role")
      private String role;
      @SerializedName(value = "EntityId")
      private Integer entityId;

      @SerializedName(value = "InitialCommission")
      private CommissionBean initialCommission;

      @SerializedName(value = "RenewalCommission")
      private CommissionBean renewalCommission;

      @Builder
      @Data
      @AllArgsConstructor
      @NoArgsConstructor
      public static class CommissionBean {
        @SerializedName(value = "ApplySalesTax")
        private boolean applySalesTax;

        @SerializedName(value = "Amount")
        private String amount;

        @SerializedName(value = "Fixed")
        private boolean fixed;

        @SerializedName(value = "Indemnity")
        private boolean indemnity;
      }
    }
  }
}
