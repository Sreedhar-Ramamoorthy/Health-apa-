package ke.co.apollo.health.common.utils;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import ke.co.apollo.health.common.constants.GlobalConstant;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncodeUtils {

  private static Logger logger = LoggerFactory.getLogger(EncodeUtils.class);

  private EncodeUtils() {
  }

  public static String shortDigest(final String password) {

    return digest(password, GlobalConstant.SHA_256, "");
  }

  public static String longDigest(final String password) {

    return digest(password, GlobalConstant.SHA_512, GlobalConstant.SALT);
  }

  public static boolean shortMatches(final String confirmPassword, final String firstPassword) {

    if (StringUtils.isBlank(firstPassword) || StringUtils.isBlank(confirmPassword)) {
      return false;
    } else if (firstPassword.equals(shortDigest(confirmPassword))) {
      return true;
    }
    return false;
  }

  public static boolean longMatches(final String password, final String dbPassword) {

    if (StringUtils.isBlank(password) || StringUtils.isBlank(dbPassword)) {
      return false;
    } else if (dbPassword.equals(longDigest(password))) {
      return true;
    }
    return false;
  }

  public static String digest(final String password, final String algorithm, final String salt) {

    MessageDigest messageDigest;
    String encodestr = password;
    String saltedKey = salt + password;
    try {
      messageDigest = DigestUtils.getDigest(algorithm);
      messageDigest.update(saltedKey.getBytes(StandardCharsets.UTF_8));
      encodestr = byte2Hex(messageDigest.digest());
    } catch (Exception e) {
      logger.error("digest exception: {}", e.getMessage());
    }
    return encodestr;
  }

  private static String byte2Hex(byte[] bytes) {
    StringBuilder stringBuilder = new StringBuilder();
    String temp;
    for (int i = 0; i < bytes.length; i++) {
      temp = Integer.toHexString(bytes[i] & 0xFF);
      if (temp.length() == 1) {
        stringBuilder.append("0");
      }
      stringBuilder.append(temp);
    }
    return stringBuilder.toString();
  }

  public static String crc32(String input) {
    if (StringUtils.isBlank(input)) {
      return input;
    }
    return Hashing.crc32().hashBytes(input.getBytes()).toString();
  }

}
