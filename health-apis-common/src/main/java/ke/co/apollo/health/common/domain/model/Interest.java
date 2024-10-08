package ke.co.apollo.health.common.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Interest implements Serializable {

  @Size(max = 10)
  @JsonInclude(Include.NON_NULL)
  private List<String> health;
  @Size(max = 10)
  @JsonInclude(Include.NON_NULL)
  private List<String> motor;
  @JsonInclude(Include.NON_NULL)
  @Length(max = 100)
  private String other;

}
