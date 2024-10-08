package ke.co.apollo.health.config;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class AppLogConfig extends CommonsRequestLoggingFilter {

    public AppLogConfig() {
        super.setIncludeHeaders(false);
        super.setIncludeClientInfo(false);
        super.setIncludeQueryString(true);
        super.setIncludePayload(true);
        super.setMaxPayloadLength(2000);
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        return !StringUtils.equalsIgnoreCase(requestURI, "/v2/api-docs");
    }
}
