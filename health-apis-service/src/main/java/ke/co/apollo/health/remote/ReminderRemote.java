package ke.co.apollo.health.remote;

import ke.co.apollo.health.common.domain.model.request.ReminderRequest;
import ke.co.apollo.health.common.domain.model.response.ResultResponse;

public interface ReminderRemote {

  ResultResponse sendReminder(ReminderRequest request);

}
