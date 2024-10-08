package ke.co.apollo.health.common.domain.model.remote;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBenefitsToPolicyRequest {

  @NotNull
  private Integer policyId;
  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private String policyEffectiveDate;

  @NotNull
  @Valid
  private List<BenefitCategoriesBean> benefitCategories;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class BenefitCategoriesBean {

    @NotEmpty
    @Length(max = 100)
    private String webName;
    @NotNull
    @Valid
    private List<BenefitsBean> benefits;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BenefitsBean {

      @NotEmpty
      @Length(max = 100)
      private String webName;
      @Valid
      private List<SalaryBandsBean> salaryBands;

      @Builder
      @Data
      @AllArgsConstructor
      @NoArgsConstructor
      public static class SalaryBandsBean {

        @Length(max = 100)
        private String lowerSalary;
        @Length(max = 100)
        private String rate;
      }
    }
  }
}
