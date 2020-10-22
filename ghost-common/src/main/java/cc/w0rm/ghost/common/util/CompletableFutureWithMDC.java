package cc.w0rm.ghost.common.util;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author : xuyang
 * @date : 2020/10/22 1:28 下午
 */

public class CompletableFutureWithMDC {

    public static CompletableFuture<Void> runAsyncWithMdc(Runnable runnable, ExecutorService executorService, Map mdc){
        return CompletableFuture.runAsync(new RunnableWithMDC(runnable, mdc), executorService);
    }

    public static CompletableFuture<Void> runAsyncWithMdc(Runnable runnable, ExecutorService executorService){
        return CompletableFuture.runAsync(new RunnableWithMDC(runnable), executorService);
    }

}
