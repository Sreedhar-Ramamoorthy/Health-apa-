package ke.co.apollo.health.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteStepRequest {
    @NotBlank
    @Length(min = 1, max = 50)
    private String quoteOrPolicy;
}
