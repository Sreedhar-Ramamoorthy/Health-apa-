package ke.co.apollo.health.policy.service.impl;

import ke.co.apollo.health.common.domain.model.Customer;
import ke.co.apollo.health.policy.mapper.health.HealthMapper;
import ke.co.apollo.health.policy.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

  @Autowired
  private HealthMapper healthMapper;

  @Override
  public Customer getCustomer(Integer entityId) {
    return healthMapper.getCustomerByEntityId(entityId);
  }


}
