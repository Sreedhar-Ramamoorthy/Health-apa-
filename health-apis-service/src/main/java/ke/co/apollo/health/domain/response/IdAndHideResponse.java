package ke.co.apollo.health.domain.response;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdAndHideResponse {
    Map<String, Boolean> idMap = new HashMap<>();
}
