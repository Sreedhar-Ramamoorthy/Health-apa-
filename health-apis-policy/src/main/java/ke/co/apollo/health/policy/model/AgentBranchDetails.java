package ke.co.apollo.health.policy.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentBranchDetails {

    public Integer entityId;
    public String firstName;
    public String surName;
    public String branch;

}
