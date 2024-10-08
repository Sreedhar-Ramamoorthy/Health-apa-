package ke.co.apollo.health.service;

import ke.co.apollo.health.domain.request.ClearDataRequest;

public interface HealthService {

  boolean clearClientData(ClearDataRequest request);

}
