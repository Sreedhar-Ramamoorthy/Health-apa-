package ke.co.apollo.health.common.domain.model.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentWalletResponse implements java.io.Serializable {

  private static final long serialVersionUID = 7092906238192790921L;

  private String merchantRequestId;

  private String checkoutRequestId;

  private Integer responseCode;

  private String responseDescription;

  private String customerMessage;

}
