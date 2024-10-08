package ke.co.apollo.health.domain.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class MpesaExpressResponse {
    private String merchantRequestId;
    private String checkoutRequestId;
    private String responseDescription;
    private float responseCode;
    private String customerMessage;
}
