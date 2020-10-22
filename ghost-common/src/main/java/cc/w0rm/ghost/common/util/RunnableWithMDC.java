package cc.w0rm.ghost.common.util;

import com.google.common.base.Preconditions;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author : xuyang
 * @date : 2020/10/22 1:12 下午
 */
public class RunnableWithMDC implements Runnable {
    private Runnable runnable;
    private Map MdcMap;

    public RunnableWithMDC(Runnable runnable) {
        this(runnable, MDC.getCopyOfContextMap());
    }

    public RunnableWithMDC(Runnable runnable, Map MdcMap) {
        Preconditions.checkArgument(Objects.nonNull(runnable));
        this.runnable = runnable;
        this.MdcMap = MdcMap;
    }

    @Override
    public void run() {
        if (MdcMap != null && MdcMap.size() > 0) {
            MDC.setContextMap(MdcMap);
        } else {
            MDC.put("request_id", UUID.randomUUID().toString());
        }
        try {
            runnable.run();
        } finally {
            MDC.clear();
        }
    }
}
