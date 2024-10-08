package ke.co.apollo.health.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 *
 * <p>This Filter Fix Security Weaknesses by WAS Scan Report </p>
 * <b>  include those five issue: </b>
 * <p>  150202 Missing header: X-Content-Type-Options </p>
 * <p>  150206 Content-Security-Policy Not Implemented </p>
 * <p>  150208 Missing header: Referrer-Policy </p>
 * <p>  150262 Missing header: Feature-Policy  </p>
 * <p>  150204 Missing header: X-XSS-Protection  </p>
 *
 *
 * @author wang
 * @version 1.0
 * @see Filter
 * @since 2020/7/1
 */
@WebFilter("/filter-response-header/*")
@Component
public class SecurityFilter implements Filter {


  @Override
  public void init(FilterConfig filterConfig) {
//    Do nothing because do not need init function
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    httpServletResponse.setHeader("X-XSS-Protection", "1; mode=block");
    httpServletResponse.setHeader("X-Content-Type-Options", "nosniff");
    httpServletResponse.setHeader("Feature-Policy", "geolocation 'none'");
    httpServletResponse.setHeader("Referrer-Policy", "no-referrer");
    httpServletResponse.setHeader("Content-Security-Policy", "default-src 'self'");
    chain.doFilter(request, response);

  }

  @Override

  public void destroy() {
    // Do nothing because do not need destroy function
  }
}
