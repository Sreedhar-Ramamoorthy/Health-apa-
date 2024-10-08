package ke.co.apollo.health.remote.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import ke.co.apollo.health.common.domain.model.request.ReminderRequest;
import ke.co.apollo.health.common.domain.model.response.ResultResponse;
import ke.co.apollo.health.remote.AbstractRemote;
import ke.co.apollo.health.remote.ReminderRemote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReminderRemoteImpl extends AbstractRemote implements ReminderRemote {

  @Value("${apa.intermediary-apis-admin.url}")
  String intermediaryAdminApisServiceBaseUrl;

  @Override
  public ResultResponse sendReminder(ReminderRequest request) {
    String url = intermediaryAdminApisServiceBaseUrl + "/intermediary/admin/api/notification/policy";
    return super.postForDataWrapper(new TypeReference<ResultResponse>() {
    }, url, request, "send reminder");
  }
}
