package cc.w0rm.ghost.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.FormEncoder;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
@Slf4j
public class FeignConfig {

    @Bean
    public Contract useFeignAnnotations() {
        return new Contract.Default();
    }

    @Bean
    public Feign.Builder hystrixBuilder() {
        return HystrixFeign.builder();
    }

    @Bean
    public Encoder feignEncoder() {
        return new FormEncoder(new JacksonEncoder());
    }

    @Bean
    public Decoder feignDecoder() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return new JacksonDecoder(objectMapper);
    }

    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(3000, 3000);
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(100, SECONDS.toMillis(1), 3);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }


    @Bean
    public ErrorDecoder errorDecoder() {
        return (s, response) -> {
            log.info("==================================");
            log.info("feign error {}", s);
            return null;
        };
    }

}
