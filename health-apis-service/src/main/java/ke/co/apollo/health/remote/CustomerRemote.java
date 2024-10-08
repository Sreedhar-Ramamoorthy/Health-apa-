package ke.co.apollo.health.remote;

import java.util.List;
import ke.co.apollo.health.common.domain.model.Principal;
import ke.co.apollo.health.common.domain.model.request.AddClientEntityRequest;
import ke.co.apollo.health.common.domain.model.request.AddContactDetailsRequest;
import ke.co.apollo.health.common.domain.model.response.AddClientEntityResponse;
import ke.co.apollo.health.common.domain.model.response.AddContactDetailsResponse;

public interface CustomerRemote {

  AddClientEntityResponse addClientEntity(AddClientEntityRequest request);

  AddContactDetailsResponse addClientContact(AddContactDetailsRequest request);

  List<AddClientEntityResponse> addDependant(List<AddClientEntityRequest> request);

  Principal getPrincipalByEntityId(Long entityId);

}
