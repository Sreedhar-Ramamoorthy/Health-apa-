package ke.co.apollo.health.repository;

import ke.co.apollo.health.domain.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, String> {

  LocationEntity findByName(String name);
}
