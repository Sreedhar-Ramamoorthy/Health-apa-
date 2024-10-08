package ke.co.apollo.health.domain;

import java.util.List;

import lombok.*;

@Builder
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteListSearchFilter {

  private List<String> agentIds;

  private String filter;

  private String sortColumn;

  private String sort;

  private Boolean hide;

  private String quoteStatus;

  private int index;
  private int limit;

}
