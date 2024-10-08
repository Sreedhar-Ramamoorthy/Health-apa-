package ke.co.apollo.health.feign.decoder;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        Exception exception = defaultErrorDecoder.decode(methodKey, response);

        if(exception instanceof RetryableException){
            log.error("Feign retryable exception " + Arrays.toString(exception.getStackTrace()));
            return exception;
            }
        else if (HttpStatus.valueOf(response.status()).is5xxServerError()) {
            log.error("Error " + response.status() + " retrying....");
            return new RetryableException(response.status(), response.reason(), response.request().httpMethod(), null);

            }
        else{
            log.error("Error " + response.status() + ", body = " + response.body() + " not retrying");
            return defaultErrorDecoder.decode(methodKey, response);
            }
        }

}