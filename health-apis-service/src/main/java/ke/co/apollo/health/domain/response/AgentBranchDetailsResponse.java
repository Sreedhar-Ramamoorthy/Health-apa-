package ke.co.apollo.health.domain.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentBranchDetailsResponse {
    public Integer entityId;
    public String firstName;
    public String surName;
    public String branch;
}
