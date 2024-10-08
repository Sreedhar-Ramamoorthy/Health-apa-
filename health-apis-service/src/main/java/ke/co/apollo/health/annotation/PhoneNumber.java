package ke.co.apollo.health.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import ke.co.apollo.health.validator.PhoneValidator;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface PhoneNumber {

  String message() default "invalid phone number format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
