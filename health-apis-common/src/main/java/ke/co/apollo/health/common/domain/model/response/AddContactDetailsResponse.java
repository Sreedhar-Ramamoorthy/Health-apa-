package ke.co.apollo.health.common.domain.model.response;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AddContactDetailsResponse extends ASAPIResponse {

  private Long entityId;
  private List<Long> addressIds;
  private List<Long> phoneIds;

  @Override
  public String toString() {
    return "AddContactDetailsResponse{" +
        "entityId=" + entityId +
        ", addressIds=" + addressIds +
        ", phoneIds=" + phoneIds +
        ", errorMessage='" + errorMessage + '\'' +
        ", success=" + success +
        ", errors=" + errors +
        '}';
  }
}
