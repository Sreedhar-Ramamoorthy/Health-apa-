package ke.co.apollo.health.common.domain.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailAttachmentRequest extends EmailRequest {
    @Expose
    @SerializedName("attachmentName")
    private String attachmentName;

    @Expose
    @SerializedName("attachmentPath")
    private String attachmentPath;
}
