package ke.co.apollo.health.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import ke.co.apollo.health.common.enums.PositionType;
import ke.co.apollo.health.common.utils.LogUtils;

/**
 * {@code }
 *
 * <p> </p>
 *
 * @author wang
 * @version 1.0
 * @see
 * @since 9/24/2020
 */
@Component
@Slf4j
public class SessionConfig implements HandlerInterceptor {

    private static final String TOKEN = "token";
    private static final int PAYLOAD = 1;

    @Override
    public void afterCompletion(HttpServletRequest arg0,
                                HttpServletResponse arg1, Object arg2, Exception arg3) {

        MDC.remove(TOKEN);
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
                           Object arg2, ModelAndView arg3) {
        // we do not need postHandle
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) {

        String token = request.getHeader("auth");
        String portal = request.getHeader("portal");
        if (StringUtils.isNotEmpty(token) && StringUtils.isEmpty(portal)) {
            try {
                final String payload = token.split("\\.")[PAYLOAD];
                final byte[] payloadBytes = Base64.getUrlDecoder()
                                                  .decode(payload);
                final String payloadString = new String(payloadBytes, StandardCharsets.UTF_8);
                JsonObject jsonObject = new Gson().fromJson(payloadString, JsonObject.class);
                token = jsonObject.get("phone_number")
                                  .getAsString();
            } catch (Exception ex) {
            log.debug("can not get token");
            }
            MDC.put(TOKEN, LogUtils.overlayValue(token, PositionType.LEFT));
        }
        if (StringUtils.isEmpty(token) && StringUtils.isNotEmpty(portal)) {
            MDC.put(TOKEN, "portal");
        }

        return true;
    }
}
