package ke.co.apollo.health.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActisureErrors {
    public String description;
    public String number;
    public String type;
    public String appName;
    public Object versionNumber;
    public String className;
    public String methodName;
    public Object userName;

}
