package ke.co.apollo.health.common.domain.model.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

  @SerializedName("to")
  private String to;

  @SerializedName("status")
  private Status status;

  @SerializedName("smsCount")
  private int smsCount;

  @SerializedName("messageId")
  private String messageId;

}
