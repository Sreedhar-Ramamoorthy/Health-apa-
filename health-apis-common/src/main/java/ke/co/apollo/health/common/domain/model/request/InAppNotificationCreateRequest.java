package ke.co.apollo.health.common.domain.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InAppNotificationCreateRequest {
    private Integer id;
    private String deviceRegistrationToken;
    private String notification;
    private String phoneNumber;
    private String email;
    private String serviceType;
    private String readStatus;
    private String actionStatus;
    private String policyNumber;
    private String notificationSubject;

}