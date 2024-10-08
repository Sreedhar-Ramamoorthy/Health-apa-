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
public class InAppNotificationMessageResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("deviceRegistrationToken")
    private String deviceRegistrationToken;

    @SerializedName("notification")
    private String notification;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("email")
    private String email;

    @SerializedName("serviceType")
    private String serviceType;

    @SerializedName("readStatus")
    private String readStatus;

    @SerializedName("createTime")
    private String createTime;

    @SerializedName("updateTime")
    private String updateTime;

    @SerializedName("actionStatus")
    private String actionStatus;

    @SerializedName("policyNumber")
    private String policyNumber;

    @SerializedName("notificationSubject")
    private String notificationSubject;

}

