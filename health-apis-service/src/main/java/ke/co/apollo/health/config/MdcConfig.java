package ke.co.apollo.health.config;

import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.MDC;

public class MdcConfig {

    private MdcConfig() {
    }

    public static Runnable withMdc(Runnable runnable) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        return () -> {
            MDC.setContextMap(mdc);
            runnable.run();
        };
    }
    public static <U> Supplier<U> mdcSupplier(Supplier<U> supplier) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        return () -> {
            MDC.setContextMap(mdc);
            return supplier.get();
        };
    }

}
