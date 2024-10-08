package ke.co.apollo.health.policy.remote;

import java.util.List;
import ke.co.apollo.health.common.domain.model.EntityDetails;
import ke.co.apollo.health.common.domain.model.request.AddClientEntityRequest;
import ke.co.apollo.health.common.domain.model.request.AddContactDetailsRequest;
import ke.co.apollo.health.common.domain.model.request.GetEntityDetailsRequest;
import ke.co.apollo.health.common.domain.model.response.AddClientEntityResponse;
import ke.co.apollo.health.common.domain.model.response.AddContactDetailsResponse;

/**
 * @author Rick
 * @version 1.0
 * @see
 * @since 9/3/2020
 */
public interface EntityMaintenanceRemote {

  AddClientEntityResponse addClientEntity(AddClientEntityRequest request);

  List<AddClientEntityResponse> addDependant(List<AddClientEntityRequest> request);

  AddContactDetailsResponse addContactDetails(AddContactDetailsRequest request);

  EntityDetails getEntityDetailsById(GetEntityDetailsRequest request);

}
