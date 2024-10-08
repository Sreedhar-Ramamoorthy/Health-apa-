package ke.co.apollo.health.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActisurePolicyBranchDetailsResponse {
    public Object errorMessage;
    public boolean success;
    public List<ActisureErrors> errors;
}

