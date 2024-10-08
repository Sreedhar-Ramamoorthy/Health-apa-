package ke.co.apollo.health.notification.service.impl;

import ke.co.apollo.health.common.domain.model.CognitoSns;
import ke.co.apollo.health.common.domain.model.request.CognitoSNSRequest;
import ke.co.apollo.health.common.exception.BusinessException;
import ke.co.apollo.health.notification.mapper.health.CognitoSnsMapper;
import ke.co.apollo.health.notification.service.CognitoSnsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CognitoSnsServiceImpl implements CognitoSnsService {

  @Autowired
  private CognitoSnsMapper cognitoSnsMapper;

  @Override
  public boolean addOrUpdate(CognitoSNSRequest request) {
    boolean result = false;
    CognitoSns cognitoSns = CognitoSns.builder().cognitoId(request.getCognitoId())
        .firebaseToken(request.getFirebaseToken()).endpointArn(request.getEndpointArn()).build();
    if (cognitoSns != null && StringUtils.isNotEmpty(cognitoSns.getCognitoId())) {
      CognitoSns records = this.getCognitoSnsById(cognitoSns.getCognitoId());
      if (records == null) {
        result = cognitoSnsMapper.insert(cognitoSns) == 1;
      } else {
        result = cognitoSnsMapper.updateByPrimaryKey(cognitoSns) == 1;
      }
    } else {
      throw new BusinessException("cognitoId is mandatory");
    }
    return result;
  }

  @Override
  public CognitoSns getCognitoSnsById(String cognitoId) {
    return cognitoSnsMapper.selectByPrimaryKey(cognitoId);
  }

  @Override
  public boolean updateEndpointArn(CognitoSns cognitoSns) {
    return cognitoSnsMapper.updateByPrimaryKey(cognitoSns) == 1;
  }
}
