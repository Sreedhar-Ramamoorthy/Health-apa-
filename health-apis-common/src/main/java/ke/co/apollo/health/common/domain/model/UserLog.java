package ke.co.apollo.health.common.domain.model;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@code }
 *
 * <p> this is use for record user's steps</p>
 *
 * @author wang
 * @version 1.0
 * @see
 * @since 2020/7/9
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLog implements Serializable {
  String sessionId;
  String phoneNumber;
  String serviceCode;
  String networkCode;
  String menuKey;
  String nextMenuKey;
  String input;
  String output;
  Date createDate;
  Date startTime;
  Date endTime;
  Integer duration;
  String clientCode;
}
