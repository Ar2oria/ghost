package cc.w0rm.ghost.common.http.listener;

import cc.w0rm.ghost.common.http.OKHttpException;
import cc.w0rm.ghost.common.util.Strings;
import okhttp3.Call;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * @author : xuyang
 * @date : 2019-09-19 19:34
 */


public class DefaultSucceedListener implements SucceedListener {

    private static final Logger log = LoggerFactory.getLogger(DefaultSucceedListener.class);

    /**
     * 成功时的调用
     * @param call
     * @param response
     */
    @Override
    public void call(Call call, Response response) {
        String body = null;
        try {
            body = Objects.isNull(response.body()) ? Strings.EMPTY : response.body().string();
        } catch (IOException e) {
            log.error("请求：{}，读取返回内容失败！", call.request(), e);
            throw new OKHttpException(e.toString());
        }
        log.info("请求：{}成功，返回内容为：{}", call.request(), body);
    }
}
