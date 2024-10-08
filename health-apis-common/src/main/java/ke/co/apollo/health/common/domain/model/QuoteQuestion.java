package ke.co.apollo.health.common.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteQuestion {

  private String quoteId;

  private String customerId;

  private String agentId;

  private List<Members> members;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Members {

    private String code;

    private String name;

    private List<Questions> questions;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Questions {

      private String questionId;

      private String content;

      private String doctorName;
    }
  }
}
