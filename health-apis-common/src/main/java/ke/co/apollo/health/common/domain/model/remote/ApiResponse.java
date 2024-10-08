package ke.co.apollo.health.common.domain.model.remote;

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
public class ApiResponse {

  private String errorMessage;
  private boolean success;
  private List<ErrorsBean> errors;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ErrorsBean {

    private String description;
    private String number;
    private String type;
    private String appName;
    private VersionNumberBean versionNumber;
    private String className;
    private String methodName;
    private String userName;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VersionNumberBean {

      private int major;
      private int minor;
      private int build;
      private int revision;
    }
  }
}
