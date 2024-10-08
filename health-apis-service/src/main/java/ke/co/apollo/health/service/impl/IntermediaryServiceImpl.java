package ke.co.apollo.health.service.impl;

import java.util.List;
import java.util.Optional;
import ke.co.apollo.health.common.domain.model.Intermediary;
import ke.co.apollo.health.mapper.intermediary.IntermediaryMapper;
import ke.co.apollo.health.service.IntermediaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntermediaryServiceImpl implements IntermediaryService {

  @Autowired
  IntermediaryMapper intermediaryMapper;

  @Override
  public Integer getIntermediaryEntityId(String agentId) {
    Intermediary intermediary = this.getIntermediary(agentId);
    return Optional.ofNullable(intermediary).map(Intermediary::getEntityId).orElse(null);
  }

  @Override
  public Intermediary getIntermediary(String agentId) {
    return intermediaryMapper.selectByPrimaryKey(agentId);
  }

  @Override
  public List<Intermediary> getUserList(String agentId) {
    return intermediaryMapper.selectUserList(agentId);
  }
}
