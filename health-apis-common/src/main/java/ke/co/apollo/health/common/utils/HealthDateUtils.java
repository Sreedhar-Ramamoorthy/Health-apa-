package ke.co.apollo.health.common.utils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ke.co.apollo.health.common.constants.GlobalConstant;

public class HealthDateUtils {

  private static Logger logger = LoggerFactory.getLogger(HealthDateUtils.class);

  private HealthDateUtils() {

  }

  public static String currentDateTimeString() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern(GlobalConstant.YYYYMMDD_HHMMSS));
  }

  public static String currentDateString() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern(GlobalConstant.YYYYMMDD));
  }

  public static Date currentDate() {
    return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  public static LocalDate date2LocalDate(Date date) {
    if (date == null) {
      return null;
    }
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public static Date currentDateTime() {
    return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date parseDate(String str, String parsePattern) {
    Date date = null;
    try {
      date = DateUtils
          .parseDate(str, parsePattern);
    } catch (Exception e) {
      logger.error("parse date error: {}", e.getMessage());
    }

    return date;

  }

  public static Date nextDay(Date date) {
    return DateUtils.addDays(date, 1);
  }

  public static Date nextYear(Date date) {
    return DateUtils.addYears(date, 1);
  }

  public static boolean same(Date date1, Date date2, int days) {
    return DateUtils.isSameDay(date1, DateUtils.addDays(date2, days));
  }

  public static Date getGMTDate(int hour) {
    Date dt = Date.from(Instant.now());
    dt = DateUtils.setHours(dt, hour);
    return dt;
  }

  public static int getCurrentYear() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(Calendar.YEAR);
  }

  public static int getYear(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.YEAR);
  }

  public static int calculateAge(Date dob, Date startDate) {
    LocalDate date = date2LocalDate(startDate);
    LocalDate birthday = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    return Math.round(birthday.until(date, ChronoUnit.DAYS) / 365.2425f);
  }

  public static Date getDayStartTime(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }

  public static Date getDayEndTime(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
    return cal.getTime();
  }
}
