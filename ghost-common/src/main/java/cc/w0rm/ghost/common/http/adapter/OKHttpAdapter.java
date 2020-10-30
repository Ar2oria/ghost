package cc.w0rm.ghost.common.http.adapter;

import cc.w0rm.ghost.common.http.OKHttpException;
import cc.w0rm.ghost.common.http.listener.FailureListener;
import cc.w0rm.ghost.common.http.listener.SucceedListener;
import cc.w0rm.ghost.common.json.JsonUtil;
import cc.w0rm.ghost.common.util.Strings;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : xuyang
 * @date : 2019-11-25 16:26
 */

public class OKHttpAdapter implements RestAdapter {
    private static final Logger log = LoggerFactory.getLogger(OKHttpAdapter.class);
    private static final String JSON = "application/json; charset=utf-8";
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    private OkHttpClient httpClient = OK_HTTP_CLIENT;

    /**
     * 设置请求超时时间
     * @param timeout 超时时间
     * @param unit 时间单位
     */
    public void setConnectionTimeout(long timeout, TimeUnit unit) {
        httpClient = httpClient.newBuilder().connectTimeout(timeout, unit).build();
    }

    /**
     * 设置OkHttp客户端
     * @param okHttpClient OkHttp客户端
     */
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.httpClient = okHttpClient;
    }

    /**
     * 或得OkHttp客户端
     * @return OkHttp客户端
     */
    public OkHttpClient getOkHttpClient() {
        return httpClient;
    }

    /**
     * get方式请求
     * @param url 请求链接
     * @param header 请求头
     * @return http响应内容
     */
    @Override
    public String doGet(String url, Map<String, String> header) {
        if (Strings.isBlank(url)) {
            throw new IllegalArgumentException("url is empty");
        }

        final Request request = buildGetRequest(url, header);
        return doCall(request);
    }

    private Request buildGetRequest(String url, Map<String, String> header) {
        Headers headers = buildHeader(header);
        return new Request.Builder()
                .url(url)
                .headers(headers)
                .build();
    }

    /**
     * form方式的post请求
     * @param url 请求链接
     * @param header 请求头
     * @param body form请求内容，使用键值对方式存放
     * @return http响应内容
     */
    @Override
    public String doPostByForm(String url, Map<String, String> header, Map<String, Object> body) {
        if (Strings.isBlank(url)) {
            throw new IllegalArgumentException("url is null");
        }

        final Request request = buildFormRequest(url, header, body);
        return doCall(request);
    }

    private Request buildFormRequest(String url, Map<String, String> header, Map<String, Object> body) {
        Headers headers = buildHeader(header);
        RequestBody requestBody = buildFormBody(body);
        return buildPostRequest(url, headers, requestBody);
    }

    private RequestBody buildFormBody(Map<String, Object> body) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (Objects.nonNull(body)) {
            body.forEach((key, val) -> formBuilder.add(key, Strings.valueOf(val)));
        }
        return formBuilder.build();
    }

    /**
     * json方式的post请求
     * @param url 请求链接
     * @param header 请求头
     * @param body json请求内容
     * @return http响应内容
     */
    @Override
    public String doPostByJson(String url, Map<String, String> header, Object body) {
        if (Strings.isBlank(url)) {
            throw new IllegalArgumentException("url is null");
        }
        if (Objects.isNull(body)) {
            body = "{}";
        }
        final Request request = buildJsonRequest(url, header, body);
        return doCall(request);
    }

    private Request buildJsonRequest(String url, Map<String, String> header, Object body) {
        Headers headers = buildHeader(header);
        RequestBody requestBody = buildJsonBody(body);
        return buildPostRequest(url, headers, requestBody);
    }


    private RequestBody buildJsonBody(Object body) {
        MediaType mediaType = MediaType.parse(JSON);
        String jsonBody = JsonUtil.writeValueAsString(body);
        return RequestBody.create(mediaType, jsonBody);
    }

    private Request buildPostRequest(String url, Headers headers, RequestBody requestBody) {
        return new Request.Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build();
    }

    private Headers buildHeader(Map<String, String> head) {
        Headers.Builder headBuilder = new Headers.Builder();
        head.forEach(headBuilder::add);
        return headBuilder.build();
    }

    private String doCall(Request request) {
        final Call call = httpClient.newCall(request);
        String responseBody = Strings.EMPTY;
        Response response = null;
        try {
            response = call.execute();
            if (Objects.nonNull(response.body())) {
                responseBody = response.body().string();
                log.info("请求url：{}, 返回内容为：{} ", request.url(), responseBody);
            }
        } catch (IOException e) {
            log.warn("请求url：{}, 出现网络异常，e:{}", request.url(), e);
            throw new OKHttpException(e);
        } finally {
            if (Objects.nonNull(response)) {
                response.close();
            }
        }
        return responseBody;
    }

    /**
     * get异步请求
     * @param url 请求链接
     * @param header 请求头
     * @param succeedListener 请求成功时的回调函数
     * @param failureListener 请求失败时的回调函数
     */
    public void doGetAsync(String url, Map<String, String> header, SucceedListener succeedListener, FailureListener failureListener) {
        if (Strings.isBlank(url)) {
            failureListener.call(null, new IllegalArgumentException("url is empty"));
            return;
        }
        final Request request = buildGetRequest(url, header);
        doCallAsync(request, succeedListener, failureListener);
    }

    /**
     * form方式的post请求，异步
     * @param url 请求链接
     * @param header 请求头
     * @param body form内容，使用键值对方式存放
     * @param succeedListener 请求成功时的回调函数
     * @param failureListener 请求失败时的回调函数
     */
    public void doPostByFormAsync(String url, Map<String, String> header, Map<String, Object> body, SucceedListener succeedListener, FailureListener failureListener) {
        if (Strings.isBlank(url)) {
            failureListener.call(null, new IllegalArgumentException("url is empty"));
            return;
        }
        final Request request = buildFormRequest(url, header, body);
        doCallAsync(request, succeedListener, failureListener);
    }

    /**
     * json方式的post请求，异步
     * @param url 请求链接
     * @param header 请求头
     * @param body json内容
     * @param succeedListener 请求成功时的回调函数
     * @param failureListener 请求失败时的回调函数
     */
    public void doPostByJsonAsync(String url, Map<String, String> header, Object body, SucceedListener succeedListener, FailureListener failureListener) {
        if (Strings.isBlank(url)) {
            failureListener.call(null, new IllegalArgumentException("url is empty"));
            return;
        }
        final Request request = buildJsonRequest(url, header, body);
        doCallAsync(request, succeedListener, failureListener);
    }

    private void doCallAsync(Request request, SucceedListener succeedListener, FailureListener failureListener) {
        final Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failureListener.call(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                succeedListener.call(call, response);
            }
        });
    }
}
