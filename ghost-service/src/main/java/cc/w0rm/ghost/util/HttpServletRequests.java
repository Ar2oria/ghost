package cc.w0rm.ghost.util;

import org.apache.logging.log4j.util.Strings;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : xuyang
 * @date : 2020/9/27 3:27 下午
 */
public class HttpServletRequests {
    public static final String UNKNOWN = "unknown";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    public static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    public static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    public static final String X_Real_IP = "X-Real-IP";

    private HttpServletRequests() {
    }

    public static String getIPAddress(HttpServletRequest request) {
        String ip = null;

        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader(X_FORWARDED_FOR);

        if (isEmpty(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader(PROXY_CLIENT_IP);
        }

        if (isEmpty(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader(WL_PROXY_CLIENT_IP);
        }

        if (isEmpty(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader(HTTP_CLIENT_IP);
        }

        if (isEmpty(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader(X_Real_IP);
        }

        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (Strings.isNotBlank(ipAddresses)) {
            ip = ipAddresses.split(",")[0];
        }

        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private static boolean isEmpty(String ipAddresses) {
        return Strings.isBlank(ipAddresses) || UNKNOWN.equalsIgnoreCase(ipAddresses);
    }
}
