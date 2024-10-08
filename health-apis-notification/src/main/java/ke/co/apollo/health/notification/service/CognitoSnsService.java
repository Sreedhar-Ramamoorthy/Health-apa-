package ke.co.apollo.health.notification.service;


import ke.co.apollo.health.common.domain.model.CognitoSns;
import ke.co.apollo.health.common.domain.model.request.CognitoSNSRequest;

public interface CognitoSnsService {

  boolean addOrUpdate(CognitoSNSRequest cognitoSNSRequest);

  CognitoSns getCognitoSnsById(String cognitoId);

  boolean updateEndpointArn(CognitoSns cognitoSns);

}
