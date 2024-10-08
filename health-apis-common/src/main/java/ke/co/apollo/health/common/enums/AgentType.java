package ke.co.apollo.health.common.enums;

public enum AgentType {

  CORPORATE("CORPORATE"), INDIVIDUAL("INDIVIDUAL"), BROKER("BROKER"), AGENTINDEPENDENT(
      "AGENT INDEPENDENT"), AGENTTIED("AGENT TIED"), OTHER("other"),DIRECT("Direct");

  private String value;

  AgentType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static AgentType getAgentType(String value) {
    for (AgentType agentType : AgentType.values()) {
      if (agentType.getValue().equals(value)) {
        return agentType;
      }
    }
    return OTHER;
  }


}
