package ke.co.apollo.health.service;

import java.util.List;
import ke.co.apollo.health.common.domain.model.Intermediary;

public interface IntermediaryService {

  Integer getIntermediaryEntityId(String agentId);

  Intermediary getIntermediary(String agentId);

  List<Intermediary> getUserList(String agentId);

}
