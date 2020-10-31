package cc.w0rm.ghost.common.http.listener;

import okhttp3.Call;

/**
 * @author : xuyang
 * @date : 2019-09-04 18:29
 */
public interface FailureListener {
    /**
     * 失败时的调用
     * @param call
     * @param e
     */
    void call(Call call, Exception e);
}
