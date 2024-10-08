package ke.co.apollo.health.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthQuoteDownloadRequest {

    @NotBlank
    @Length(min = 1, max = 50)
    private String customerId;

    private String quoteCode;
}
