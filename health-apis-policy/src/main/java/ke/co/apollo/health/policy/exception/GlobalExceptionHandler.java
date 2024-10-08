package ke.co.apollo.health.policy.exception;


import javax.validation.ConstraintViolationException;
import javax.validation.UnexpectedTypeException;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(value = {ConstraintViolationException.class, BindException.class,
      MethodArgumentNotValidException.class, UnexpectedTypeException.class})
  public ResponseEntity<DataWrapper> validationExceptionHandler(Exception exception) {
    BindingResult bindResult = null;
    StringBuilder msg = new StringBuilder();
    if (exception instanceof ConstraintViolationException) {
      msg = msg.append(((ConstraintViolationException) exception).getMessage());
    } else if (exception instanceof UnexpectedTypeException) {
      msg = msg.append(((UnexpectedTypeException) exception).getMessage());
    } else if (exception instanceof BindException) {
      bindResult = ((BindException) exception).getBindingResult();
    } else if (exception instanceof MethodArgumentNotValidException) {
      bindResult = ((MethodArgumentNotValidException) exception).getBindingResult();
    }

    if (bindResult != null && bindResult.hasErrors()) {
      logger.error("Validation Binding Exception: {}", bindResult.getAllErrors());
      for (ObjectError objectError : bindResult.getAllErrors()) {
        FieldError fieldError = (FieldError) objectError;
        msg = msg.append(fieldError.getField()).append(": ")
            .append(fieldError.getDefaultMessage()).append(", ");
      }
    }
    logger.error("Validation Exception: {}", msg);
    return ResponseEntity
        .ok(new DataWrapper(ReturnCode.INVALID_PARAMETER, msg.toString(), null));
  }

  @ExceptionHandler(value = HttpMessageNotReadableException.class)
  public ResponseEntity<DataWrapper> handleMessageNotReadableException(Exception e) {
    logger.error("Message Not Readable Exception: ", e);
    String errorMsg = e.getMessage();
    if (StringUtils.isNotEmpty(errorMsg)) {
      int index = errorMsg.indexOf(";");
      if (index > 0) {
        errorMsg = errorMsg.substring(0, index);
      }
    }
    return ResponseEntity.ok(new DataWrapper(ReturnCode.INVALID_PARAMETER, errorMsg, null));
  }

  @ExceptionHandler(value = BusinessException.class)
  public ResponseEntity<DataWrapper> handleBusinessException(Exception e) {
    logger.error("Business Exception: ", e);
    return ResponseEntity.ok(new DataWrapper(ReturnCode.EXCEPTION, e.getMessage(), null));
  }

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<DataWrapper> handleException(Exception e) {
    logger.error("Global Internal Runtime Error: ", e);
    return ResponseEntity.ok(new DataWrapper(ReturnCode.ERROR));
  }

}
