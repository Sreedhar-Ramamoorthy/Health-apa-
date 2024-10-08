package ke.co.apollo.health.notification.service;

import java.util.List;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.MarketingPreference;

public interface CustomerService {

  List<MarketingPreference> getMarketingPreference(String entityId, String product);

  Customer getCustomerByEntityId(String entityId);
}
