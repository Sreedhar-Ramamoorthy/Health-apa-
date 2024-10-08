package ke.co.apollo.health.validator;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import ke.co.apollo.health.annotation.PhoneNumber;
import org.apache.commons.lang3.StringUtils;

public class PhoneValidator implements ConstraintValidator<PhoneNumber, String> {

  @Override
  public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

    boolean result = false;
    if (StringUtils.startsWith(s, "254")) {
      s = StringUtils.remove(s, "254");
      if (s.length() < 10) {
        result = true;
      }
    }
    return result;
  }
}
