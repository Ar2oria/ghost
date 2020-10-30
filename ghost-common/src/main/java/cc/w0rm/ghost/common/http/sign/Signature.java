package cc.w0rm.ghost.common.http.sign;

import cc.w0rm.ghost.common.http.RequestProperties;

import java.util.Map;

/**
 * @author : xuyang
 * @date : 2019-09-19 20:51
 */
public interface Signature {
    /**
     * 对get方法进行签名
     * @param myRequestProperties 请求参数
     * @return 请求url
     */
    String signatureGet(RequestProperties myRequestProperties);

    /**
     * 对form进行签名
     * @param myRequestProperties 请求参数
     * @param body form请求内容
     * @return 签名后的form内容
     */
    Map<String, Object> signatureForm(RequestProperties myRequestProperties, Map<String, Object> body);

    /**
     * 对json进行签名
     * @param myRequestProperties 请求参数
     * @param body json请求内容
     * @return 签名后的json内容
     */
    Object signatureJson(RequestProperties myRequestProperties, Object body);
}
