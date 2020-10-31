package cc.w0rm.ghost.common.http.sign;


import cc.w0rm.ghost.common.http.RequestProperties;

import java.util.Map;

/**
 * @author : xuyang
 * @date : 2019-09-26 18:28
 */

public class RestSign implements Signature {


    @Override
    public String signatureGet(RequestProperties myRequestProperties) {
        StringBuilder builder = new StringBuilder(myRequestProperties.getUrl());
        Map<String, Object> args = myRequestProperties.getArgs();
        if (args != null && args.size() > 0) {
            builder.append("?");
            args.forEach((k, v) -> builder.append(k)
                    .append("=")
                    .append(v)
                    .append("&"));
        }
        return builder.toString();
    }

    @Override
    public Map<String, Object> signatureForm(RequestProperties myRequestProperties, Map<String, Object> body) {
        return body;
    }

    @Override
    public Object signatureJson(RequestProperties myRequestProperties, Object body) {
        return body;
    }
}
