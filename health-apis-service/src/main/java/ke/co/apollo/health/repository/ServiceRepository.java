package ke.co.apollo.health.repository;

import ke.co.apollo.health.domain.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(value = "transactionManager")
public interface ServiceRepository extends JpaRepository<ServiceEntity, Integer> {

}
