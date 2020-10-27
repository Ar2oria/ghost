package cc.w0rm.ghost.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author panyupeng
 * @date 2020-10-26 23:50
 */
@Slf4j
public class HttpUtils {
    
    public static String get(String uri, Map<String, String> requestParams, Map<String, String> header) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 参数
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            params.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue()));
        }
        String paramStr = "";
        if (params.length() > 0) {
            paramStr = "?" + params.substring(1);
        }
        // 创建Get请求
        HttpGet httpGet = new HttpGet(uri + paramStr);
        // 响应模型
        CloseableHttpResponse response = null;
        String result = null;
        try {
            // 配置信息
            RequestConfig requestConfig = RequestConfig.custom()
                // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(5000)
                // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(5000)
                // socket读写超时时间(单位毫秒)
                .setSocketTimeout(5000)
                // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(true).build();
            // 将上面的配置信息 运用到这个Get请求里
            httpGet.setConfig(requestConfig);
            // 添加header
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            if (null != response.getEntity()) {
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (ParseException | IOException e) {
            log.error("httpClient error!{},{}", uri, paramStr);
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
