package ke.co.apollo.health.domain.request;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import org.hibernate.validator.constraints.Length;

@SuperBuilder
@Data
@AllArgsConstructor
public class QuoteAgentHideRequest   {
    @NotBlank
    @Length(min = 1, max = 50)
    private String quoteId;
}
