package ke.co.apollo.health.common.domain.model;


import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Common {

  Date createTime;
  Date updateTime;
  String createBy;
  String updateBy;
}
