package ke.co.apollo.health.common.constants;

import java.math.BigDecimal;

public class GlobalConstant {

  private GlobalConstant() {
  }

  public static final String YYYYMMDD_T_HHMMSS = "yyyy-MM-dd'T'HH:mm:ss";
  public static final String YYYYMMDD = "yyyy-MM-dd";
  public static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy";
  public static final String DATE_FORMAT_DDMMYYYY = "ddMMyyyy";
  public static final String YYYYMMDD_HHSSMMSSS = "yyyy-MM-dd HH:mm:ss:SSS";
  public static final String YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
  public static final String SHA_512 = "SHA-512";
  public static final String SHA_256 = "SHA-256";
  public static final String SALT = "DSSU";

  public static final String SEND_SMS_MESSAGE_FROM = "from";
  public static final String SEND_SMS_MESSAGE_TEXT = "text";
  public static final String SEND_SMS_MESSAGE_TO = "to";
  public static final String SEND_SMS_MESSAGE_REJECTED = "REJECTED";
  public static final String SEND_SMS_MESSAGE_URL = "/notification/sms/sendSMSMessage";
  public static final String SEND_EMAIL_MESSAGE_URL = "/notification/email/sendEmail";
  public static final String SEND_EMAIL_ATT_MESSAGE_URL = "/notification/email/sendEmailAttachment";
  public static final String CREATE_INAPPNOTIFICATION_URL = "/notification/app/create";
  public static final String GET_ALL_INAPPNOTIFICATION_URL = "/notification/app/get/all";
  public static final String CLEAR_INAPPNOTIFICATION_URL = "/notification/app/clear";
  public static final String CALCULATE_PREMIUM = "calculate_premium";
  public static final String CALCULATE_RENEWAL_PREMIUM = "calculate_renewal_premium";
  public static final BigDecimal ITL_RATE = BigDecimal.valueOf(0.002);
  public static final BigDecimal PHCF_RATE = BigDecimal.valueOf(0.0025);
  public static final BigDecimal STAMP_DUTY = BigDecimal.valueOf(40);
  public static final double LOSSRATIO_FACTOR = 1.0;

  public static final String NATIONALITY_KENYA = "Kenya";
  public static final String ADDRESS_PLACEHOLDER = "ADDRESS PLACEHOLDER";
  public static final String LIST_ROLE_ADDITIONAL_INFO_KRA_PIN_NO = "KRA PIN No";
  public static final String LIST_ROLE_ADDITIONAL_INFO_MOBILE = "Mobile";
  public static final String LIST_ROLE_ADDITIONAL_INFO_EMAIL = "Email";
  public static final String LIST_ROLE_ADDITIONAL_INFO_ID_NO = "ID No";

  public static final String HEALTH_SERVICE_TYPE = "HEALTH";

  public static final String UNDERWRITING_TYPE = "FMU";

  public static final String SEND_SMS_MESSAGE_SERVICE_TYPE = "serviceType";
  public static final String APOLLO_GROUP = "APAGeneral";
  public static final String PAYMENT_SUCCESS = "Success";
  public static final String PAYMENT_FAILED = "Failed";

  public static final String READ_STATUS = "UNREAD";

  public static final String HEALTH_RENEWAL_NOTIFICATION_TITLE = "HEALTH RENEWAL NOTIFICATION";
  public static final String HEALTH_RENEWAL_NOTIFICATION_STATUS = "RENEW";
  public static final String HEALTH_PAYMENT_NOTIFICATION_TITLE = "HEALTH PAYMENT NOTIFICATION";
  public static final String HEALTH_PAYMENT_NOTIFICATION_STATUS = "PAYMENT";
  public static final String HEALTH_PAYMENT_NOTIFICATION_THANK_TITLE = "THANK YOU";

}
