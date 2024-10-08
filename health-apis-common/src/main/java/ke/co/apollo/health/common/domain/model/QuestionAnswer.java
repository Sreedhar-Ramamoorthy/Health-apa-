package ke.co.apollo.health.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswer {


  private String code;

  private String name;

  private String questionId;

  private String content;

  private String doctorName;

}
