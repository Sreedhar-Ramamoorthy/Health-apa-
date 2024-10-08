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
public class Status {

  @SerializedName("groupId")
  private int groupId;

  @SerializedName("groupName")
  private String groupName;

  @SerializedName("id")
  private int id;

  @SerializedName("name")
  private String name;

  @SerializedName("description")
  private String description;

}
