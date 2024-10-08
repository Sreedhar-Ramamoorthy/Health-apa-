package ke.co.apollo.health.domain.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import ke.co.apollo.health.annotation.PhoneNumber;
import ke.co.apollo.health.common.domain.model.Children;
import ke.co.apollo.health.common.domain.model.Dependant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateRequest {

  @Schema(defaultValue = "false")
  private boolean deleteSpouse;

  @Schema(defaultValue = "false")
  private boolean deleteChildrenRequest;

  @Schema(defaultValue = "false")
  private boolean updateNumberOfChildren;

  @NotNull
  private String customerId;
  @NotNull
  private String quoteId;

  @Valid
  private PrincipalBean principal;

  @Valid
  private Dependant spouse;

  @Valid
  private Children children;

  private String agentId;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PrincipalBean {

    private Long entityId; // Actisure client entity id

    @NotBlank
    private String firstName;

    private String lastName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    private String title;

    private String gender;

    @PhoneNumber
    private String phoneNumber;

    private String email;

    private String idNo;

    private String kraPin;

  }


}
