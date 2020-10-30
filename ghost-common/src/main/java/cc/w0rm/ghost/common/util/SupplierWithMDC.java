package cc.w0rm.ghost.common.util;

import com.google.common.base.Preconditions;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author : xuyang
 * @date : 2020/10/30 11:06 下午
 */
public class SupplierWithMDC<U> implements Supplier<U> {
    private final Supplier<U> supplier;
    private final Map MdcMap;

    public SupplierWithMDC(Supplier<U> supplier) {
        this(supplier, MDC.getCopyOfContextMap());
    }

    public SupplierWithMDC(Supplier<U> supplier, Map MdcMap) {
        Preconditions.checkArgument(Objects.nonNull(supplier));
        this.supplier = supplier;
        this.MdcMap = MdcMap;
    }

    @Override
    public U get() {
        if (MdcMap != null && MdcMap.size() > 0) {
            MDC.setContextMap(MdcMap);
        } else {
            MDC.put("request_id", UUID.randomUUID().toString());
        }

        try {
            return supplier.get();
        } finally {
            MDC.clear();
        }
    }
}
