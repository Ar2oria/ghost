package cc.w0rm.ghost.common.http.listener;


import cc.w0rm.ghost.common.http.OKHttpException;
import okhttp3.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : xuyang
 * @date : 2019-09-19 19:33
 */

public class DefaultFailureListener implements FailureListener {

    private static final Logger log = LoggerFactory.getLogger(DefaultFailureListener.class);
    /**
     * 失败时的调用
     * @param call
     * @param e
     */
    @Override
    public void call(Call call, Exception e) {
        log.error("请求：{}, 发生网络异常", call.request(), e);
        throw new OKHttpException(e.toString());
    }
}
