package ke.co.apollo.health.notification.service.impl;

import java.util.List;
import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.common.domain.model.MarketingPreference;
import ke.co.apollo.health.notification.mapper.health.CustomerMapper;
import ke.co.apollo.health.notification.mapper.intermediary.IntermediaryMapper;
import ke.co.apollo.health.notification.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

  @Autowired
  CustomerMapper customerMapper;

  @Autowired
  IntermediaryMapper intermediaryMapper;

  @Override
  public List<MarketingPreference> getMarketingPreference(String entityId, String product) {
    return intermediaryMapper.selectMarketingPreference("", entityId, product);
  }

  @Override
  public Customer getCustomerByEntityId(String entityId) {
    return customerMapper.getCustomerByEntityId(Integer.parseInt(entityId));
  }
}
