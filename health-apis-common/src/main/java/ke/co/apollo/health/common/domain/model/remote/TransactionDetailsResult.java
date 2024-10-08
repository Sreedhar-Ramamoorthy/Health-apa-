package ke.co.apollo.health.common.domain.model.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailsResult implements java.io.Serializable {

  private static final long serialVersionUID = 6391739123632504666L;

  private Boolean success;

  private String msg;

  TransactionDetailsResponse data;

}
