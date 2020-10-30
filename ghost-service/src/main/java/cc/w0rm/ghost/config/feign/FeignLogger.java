package cc.w0rm.ghost.config.feign;

import com.alibaba.fastjson.JSON;
import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static feign.Util.UTF_8;
import static feign.Util.decodeOrDefault;

@Slf4j
@Component
public class FeignLogger extends Logger {
    @Override
    protected void log(String configKey, String format, Object... args) {
        log.info(String.format(methodTag(configKey) + format, args));
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        if (logLevel.ordinal() != Level.BASIC.ordinal()) {
            super.logRequest(configKey, logLevel, request);
            return;
        }

        if (request.body() != null) {
            String bodyText = request.charset() != null ? new String(request.body(), request.charset()) : null;
            log(configKey, "---> method == %s ;url == %s ;body == %s ;headers == %s", request.method(), request.url(), generStr(bodyText), JSON.toJSONString(request.headers()));
            return;
        }
        log(configKey, "---> method == %s ;url == %s ;headers == %s", request.method(), request.url(), JSON.toJSONString(request.headers()));
    }

    /***
     * 生成字符串
     * @param bodyText
     * @return
     */
    private String generStr(String bodyText) {
        if (StringUtils.isEmpty(bodyText)) {
            return "!empty data";
        }

        return bodyText.replaceAll("\\s", "");
    }


    @Override
    public Response logAndRebufferResponse(String configKey, Level logLevel, Response response,
                                           long elapsedTime) throws IOException {
        if (logLevel.ordinal() != Level.BASIC.ordinal()) {
            return super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);
        }
        String reason = response.reason() != null && logLevel.compareTo(Level.NONE) > 0 ?
                " " + response.reason() : "";
        int status = response.status();
        int bodyLength = 0;
        if (response.body() != null && !(status == 204 || status == 205)) {
            byte[] bodyData = Util.toByteArray(response.body().asInputStream());
            bodyLength = bodyData.length;
            if (bodyLength > 0) {
                log(configKey, "<--- %s %s(status=%s,take_time=%sms,message=%s)%s",
                        response.request().method(), response.request().url(), status, elapsedTime, reason,
                        StringEscapeUtils.unescapeJava(decodeOrDefault(bodyData, UTF_8, "Binary data")));
            }
            return response.toBuilder().body(bodyData).build();
        } else {
            log(configKey, "<--- END HTTP (%s-byte body)", bodyLength);
        }

        return response;
    }

}
