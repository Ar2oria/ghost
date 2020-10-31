package cc.w0rm.ghost.common.http.listener;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author : xuyang
 * @date : 2019-09-04 18:31
 */
public interface SucceedListener {
    /**
     * 成功时的调用
     * @param call
     * @param response
     */
    void call(Call call, Response response);
}
