package ke.co.apollo.health.repository;

import ke.co.apollo.health.domain.entity.HealthStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthStepRepository  extends JpaRepository<HealthStepEntity, String> {
}
