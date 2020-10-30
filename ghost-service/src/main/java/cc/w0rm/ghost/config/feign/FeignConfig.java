package cc.w0rm.ghost.config.feign;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.netflix.hystrix.*;
import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.FormEncoder;
import feign.hystrix.HystrixFeign;
import feign.hystrix.SetterFactory;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
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
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        return new JacksonDecoder(objectMapper);
    }

    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(30000, 30000);
    }

    @Bean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
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

    @Bean
    public SetterFactory setterFactory() {
        return (target, method) -> {

            String groupKey = target.name();
            String commandKey = Feign.configKey(target.type(), method);

            HystrixCommandProperties.Setter setter = HystrixCommandProperties.Setter()
                    .withFallbackEnabled(true)
                    .withExecutionTimeoutEnabled(true)
                    .withExecutionTimeoutInMilliseconds(1000 * 20)
                    .withExecutionIsolationThreadInterruptOnTimeout(false)
                    .withMetricsRollingStatisticalWindowInMilliseconds(1000 * 60)
                    .withCircuitBreakerErrorThresholdPercentage(80)
                    .withCircuitBreakerRequestVolumeThreshold(5)
                    .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                    .withCircuitBreakerSleepWindowInMilliseconds(1000 * 60);

            HystrixThreadPoolProperties.Setter threadSetter = HystrixThreadPoolProperties.Setter()
                    .withCoreSize(30)
                    .withMaximumSize(50)
                    .withAllowMaximumSizeToDivergeFromCoreSize(true);


            return HystrixCommand.Setter
                    .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
                    .andCommandPropertiesDefaults(setter)
                    .andThreadPoolPropertiesDefaults(threadSetter);


        };


    }
}
