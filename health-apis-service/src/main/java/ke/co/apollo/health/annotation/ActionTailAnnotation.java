package ke.co.apollo.health.annotation;

import com.google.common.util.concurrent.AtomicLongMap;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Date;
import ke.co.apollo.health.common.constants.GlobalConstant;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ActionTailAnnotation {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static final AtomicLongMap<String> VISIT_COUNT_MAP = AtomicLongMap.create();

  @Autowired
  HikariDataSource healthDataSource;

  @Pointcut("execution(* ke.co.apollo.health.service..*.*(..))")
  public void actionTailAspect() {
    // Constructor
  }

  @Pointcut("execution(* ke.co.apollo.health.remote..*.*(..))")
  public void remoteTailAspect() {
    // Constructor
  }

  @Pointcut("execution(* ke.co.apollo.health.controller..*.*(..))")
  public void controllerTailAspect() {
    // Constructor
  }

  @Around("actionTailAspect() || remoteTailAspect() || controllerTailAspect()")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

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
        "time trace: hms-apis, method: {}, start_time: {}, end_time:{}, duration: {} ms",
        location, startDate, endDate, endTime - startTime);
    return result;
  }

  @Before(value = "controllerTailAspect()")
  public void before(JoinPoint joinPoint) {
    logger.info("Current request total count: {}", VISIT_COUNT_MAP.incrementAndGet("count"));
    HikariDataSourcePoolMetadata hikariDataSourcePoolMetadata = new HikariDataSourcePoolMetadata(
        healthDataSource);
    logger.info("health datasource active: {}, idle: {}, mix: {}, max: {}",
        hikariDataSourcePoolMetadata.getActive(),
        hikariDataSourcePoolMetadata.getIdle(),
        hikariDataSourcePoolMetadata.getMin(),
        hikariDataSourcePoolMetadata.getMax());
  }

  @After(value = "controllerTailAspect()")
  public void after(JoinPoint joinPoint) {
    VISIT_COUNT_MAP.decrementAndGet("count");
  }
}
