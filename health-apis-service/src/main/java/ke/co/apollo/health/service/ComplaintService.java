package ke.co.apollo.health.service;

import ke.co.apollo.health.domain.request.PolicyComplaintRequest;

public interface ComplaintService {

  boolean submitComplaint(PolicyComplaintRequest request);
}
