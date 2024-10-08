package ke.co.apollo.health.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationQuoteListSearchFilter {

  private String filter;

  private boolean paid;

  private int archived;

  private String sortColumn;

  private String sort;

}
