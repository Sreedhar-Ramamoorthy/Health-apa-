package ke.co.apollo.health.policy.annotation;

import com.google.gson.Gson;
import java.util.Date;
import ke.co.apollo.health.common.constants.GlobalConstant;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ActionTailAnnotation {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  Gson gson;

  @Pointcut("execution(* ke.co.apollo.health.policy.service..*.*(..))")
  public void serviceTailAspect() {
    // Constructor
  }

  @Pointcut("execution(* ke.co.apollo.health.policy.controller..*.*(..))")
  public void controllerTailAspect() {
    // Constructor
  }

  @Pointcut("execution(* ke.co.apollo.health.policy.remote..*.*(..))")
  public void remoteTailAspect() {
    // Constructor
  }

  @Around("serviceTailAspect() || controllerTailAspect() || remoteTailAspect()")
  public Object aroundService(ProceedingJoinPoint joinPoint) throws Throwable {
    return around(joinPoint);
  }

  private Object around(ProceedingJoinPoint joinPoint) throws Throwable {

    String location =
        joinPoint.getSignature().getDeclaringType().getSimpleName() + "." + joinPoint.getSignature()
            .getName();
    long startTime = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long endTime = System.currentTimeMillis();
    String startDate = DateFormatUtils
        .format(new Date(startTime), GlobalConstant.YYYYMMDD_HHSSMMSSS);
    String endDate = DateFormatUtils.format(new Date(endTime), GlobalConstant.YYYYMMDD_HHSSMMSSS);
    logger.debug(
        "time trace: health_policy, method: {}, start_time: {}, end_time:{}, duration: {} ms",
        location, startDate, endDate, endTime - startTime);
    return result;
  }

}
