package ke.co.apollo.health.common.domain.model;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {

  @NotBlank
  @Length(min = 1, max = 50)
  private String quoteId;

  @NotBlank
  @Length(min = 1, max = 50)
  private String customerId;

  @Length(max = 50)
  private String agentId;

  @Valid
  private List<Answers> answers;

  @SuperBuilder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Answers {

    @Length(min = 1, max = 50)
    private String questionId;
    @NotNull
    private boolean answer;
    @Valid
    private List<Detail> detail;

    @SuperBuilder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {

      @Length(max = 50)
      private String code;
      @Length(max = 62)
      private String name;
      @Length(max = 300)
      private String content;
      @Length(max = 50)
      private String doctorName;
    }
  }
}
