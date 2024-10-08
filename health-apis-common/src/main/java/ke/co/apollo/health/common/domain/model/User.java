package ke.co.apollo.health.common.domain.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@code }
 *
 * <p> </p >
 *
 * @author Rick
 * @version 1.0
 * @see
 * @since 7/15/2020
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

  private Integer id;

  private String clientCode;

  private String phoneNumber;

  private String ussdPin;

  private Date changeDate;

  private String sessionId;

  private String defaultMpesaNumber;

  private Integer version;

}
