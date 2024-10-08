package ke.co.apollo.health.common.domain.model.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SMSMessageResponse {

  @SerializedName("messages")
  private List<Message> messages;

}
