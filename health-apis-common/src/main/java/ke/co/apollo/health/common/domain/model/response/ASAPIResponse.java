package ke.co.apollo.health.common.domain.model.response;

import java.util.List;
import ke.co.apollo.health.common.domain.model.ActBusinessError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ASAPIResponse {

  String errorMessage;
  boolean success;
  List<ActBusinessError> errors;

  @Override
  public String toString() {
    return "ASAPIResponse{" +
        "errorMessage='" + errorMessage + '\'' +
        ", success=" + success +
        ", errors=" + errors +
        '}';
  }
}
