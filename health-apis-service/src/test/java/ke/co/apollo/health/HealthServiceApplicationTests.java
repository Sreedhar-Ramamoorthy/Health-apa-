package ke.co.apollo.health;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import org.junit.jupiter.api.Test;

class HealthServiceApplicationTests {

    @Test
    void contextLoads() {
        HealthServiceApplication quotationApplication = new HealthServiceApplication();
        assertTrue("If I run, Means I'm Okay!", true);
        assertNotNull(quotationApplication);
    }


}
