package ke.co.apollo.health.common.domain.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCondition implements Serializable {

  private String searchKey;

  private String sort;

  private String filter;

  private int index;

  private int limit;

}
