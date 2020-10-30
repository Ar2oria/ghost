package cc.w0rm.ghost.config.feign;

import cc.w0rm.ghost.common.http.MyRequestProperties;
import cc.w0rm.ghost.common.json.JsonUtil;
import cc.w0rm.ghost.config.RequestProperties;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/30 2:47 上午
 */
@Slf4j
@Component
public class BaoZouFeignInterceptor implements RequestInterceptor {
    private static final Pattern CSRF = Pattern.compile("^csrftoken=(.*?);\\s");
    private static final Pattern SESSION = Pattern.compile("^sessionid=(.*?);\\s");
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private static final Cache<String, BaoZouSession> SESSION_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(Integer.MAX_VALUE)
            .expireAfterAccess(2, TimeUnit.HOURS)
            .softValues()
            .build();

    private static final String CACHE = "CACHE";
    private static final String URL = "mzsmn.com";
    private static final String CSRFTOKEN = "csrftoken";
    private static final String SESSIONID = "sessionid";
    private static final String XCSRFTOKEN = "X-CSRFToken";

    @Autowired
    private RequestProperties requestProperties;

    @PostConstruct
    public void init(){
        BaoZouSession session = getSession();
        if (session != null) {
            SESSION_CACHE.put(CACHE, session);
        }
    }

    private BaoZouSession getSession() {
        MyRequestProperties myRequestProperties = requestProperties.getProps("baozou");
        String csrf = myRequestProperties.getArgs().get("csrf").toString();
        String url = myRequestProperties.getUrl();
        String appkey = myRequestProperties.getArgs().get("appkey").toString();
        String appSecret = myRequestProperties.getAppSecret();
        Response response = getResponse(url, appSecret, appkey, csrf);

        if (response == null) {
            return null;
        }

        BaoZouSession baoZouSession = new BaoZouSession();
        List<String> sessionList = response.headers("Set-Cookie");
        sessionList.forEach(str -> {
            Matcher matcher = CSRF.matcher(str);
            if (matcher.find()) {
                baoZouSession.setCsrf(matcher.group(1));
            }

            matcher = SESSION.matcher(str);
            if (matcher.find()) {
                baoZouSession.setSession(matcher.group(1));
            }
        });
        response.close();

        return baoZouSession;
    }

    @Nullable
    private Response getResponse(String url, String appSecret, String appkey, String csrf) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", appkey);
        params.put("pwd", appSecret);
        Headers headers = new Headers.Builder()
                .add("Cookie", "csrftoken=" + csrf)
                .add("X-CSRFToken", csrf)
                .add("Host", "m.mzsmn.com")
                .add("Origin", "http://m.mzsmn.com")
                .add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36")
                .build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JsonUtil.writeValueAsString(params));

        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build();

        Call call = OK_HTTP_CLIENT.newCall(request);
        Response response = null;
        try {
            log.debug("try connect to baozou net service...");
            response = call.execute();
        } catch (Exception exp) {
            log.error("获取暴走工具箱token失败", exp);
        }

        if (response == null || !response.isSuccessful()) {
            return null;
        }
        log.debug("get baozou session success.");
        return response;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Collection<String> hosts = requestTemplate.headers().get("Host");
        if (CollectionUtils.isEmpty(hosts)){
            return;
        }

        if (hosts.size() == 1) {
            Object[] objects = hosts.toArray();
            String host = objects[0].toString();
            if (!host.contains(URL)) {
                return;
            }
        }

        BaoZouSession baoZouSession = SESSION_CACHE.getIfPresent(CACHE);
        if (baoZouSession == null) {
            baoZouSession = getSession();
            if (baoZouSession == null) {
                throw new IllegalStateException("获取暴走工具箱token失败！网络异常");
            }
            SESSION_CACHE.put(CACHE, baoZouSession);
        }

        Collection<String> cookies = requestTemplate.headers().get("Cookie");
        if (CollectionUtils.isEmpty(cookies)){
            cookies = new ArrayList<>();
        }

        List<String> newCookies = cookies.stream()
                .filter(cookie -> !cookie.startsWith(CSRFTOKEN) && !cookie.startsWith(SESSIONID))
                .collect(Collectors.toList());
        newCookies.add(CSRFTOKEN + "=" + baoZouSession.getCsrf() + "; " + SESSIONID + "=" + baoZouSession.getSession());
        requestTemplate.header("Cookie", newCookies);
        requestTemplate.header(XCSRFTOKEN, Collections.singleton(baoZouSession.getCsrf()));
    }
}
