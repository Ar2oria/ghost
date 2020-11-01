package cc.w0rm.ghost.entity.resolver.detect;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author : xuyang
 * @date : 2020/10/31 3:42 下午
 */

public interface Detector {
    @Nonnull
    List<PreTestText> detect(String msg);
}
