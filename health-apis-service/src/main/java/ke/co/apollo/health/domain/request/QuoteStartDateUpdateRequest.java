package ke.co.apollo.health.domain.request;

import java.util.Date;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QuoteStartDateUpdateRequest extends QuoteBaseRequest {

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Future
  private Date startDate;
}
