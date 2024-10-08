package ke.co.apollo.health.common.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import ke.co.apollo.health.common.annotation.Enums;
import org.apache.commons.lang3.StringUtils;

public class EnumsValidator implements ConstraintValidator<Enums, String> {

  private Set<String> enumSet;

  @Override
  public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
    if (StringUtils.isEmpty(s)) {
      return true;
    }
    return enumSet.contains(s);
  }

  @Override
  public void initialize(Enums enums) {
    enumSet = new HashSet<>(Arrays.asList(enums.enumCode()));
  }
}
