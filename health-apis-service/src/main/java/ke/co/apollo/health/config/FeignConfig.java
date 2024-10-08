/**  
Global Retryer
*/
package ke.co.apollo.health.config;

import feign.Logger;
import feign.Retryer;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import ke.co.apollo.health.feign.decoder.FeignErrorDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
        }

    @Bean
    Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
        }

    @Bean
    ErrorDecoder feignErrorDecoder()  {
        return new FeignErrorDecoder();
        }

    @Bean
    public Retryer retryer() {
        /**
        * starting interval of 5 seconds,
        * the maximum interval of 20 seconds,
        * and the maximum number of attempts as 3
        * NB:   interval = waiting period = backoff
        *       backoff factor = 1.5
        **/
        return new Retryer.Default(1000 * 5, 1000 * 20, 3);

        }

    }