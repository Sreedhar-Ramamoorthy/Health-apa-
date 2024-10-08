package ke.co.apollo.health.repository;
import ke.co.apollo.health.domain.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(value = "transactionManager")
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {

  PaymentEntity findByName(String name);
}
