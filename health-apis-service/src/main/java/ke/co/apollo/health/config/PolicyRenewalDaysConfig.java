package ke.co.apollo.health.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PropertySource({"classpath:notification-renewal-days.properties"})

public class PolicyRenewalDaysConfig {
    @Value("#{'${POLICY_RENEWAL_DAYS}'.split(',')}")
    private List<Integer> renewal;

    public List<Integer> getRenewal() {
        return renewal;
    }

    public void setRenewal(List<Integer> renewal) {
        this.renewal = renewal;
    }

    @Value("#{'${POLICY_EXPIRED_DAYS}'.split(',')}")
    private List<Integer> expired;

    public List<Integer> getExpired() {
        return expired;
    }

    public void setExpired(List<Integer> expired) {
        this.expired = expired;
    }
}