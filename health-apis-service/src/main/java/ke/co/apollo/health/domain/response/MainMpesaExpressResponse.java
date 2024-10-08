package ke.co.apollo.health.domain.response;

import ke.co.apollo.health.common.domain.model.remote.PaymentWalletResponse;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class MainMpesaExpressResponse {
    private boolean success;
    private String msg;
    private PaymentWalletResponse data;
}
