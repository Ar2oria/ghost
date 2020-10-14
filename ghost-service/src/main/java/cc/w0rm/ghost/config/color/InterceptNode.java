package cc.w0rm.ghost.config.color;

import com.forte.qqrobot.intercept.Context;
import com.forte.qqrobot.intercept.Interceptor;
import lombok.Data;

import java.util.Objects;

/**
 * @author : xuyang
 * @date : 2020/10/14 1:10 上午
 */

@Data
@SuppressWarnings("unchecked")
public class InterceptNode {
    private Interceptor intercept;

    private InterceptNode next;


    public boolean intercept(Context context) {
        if (Objects.isNull(intercept)) {
            return false;
        }

        boolean result = intercept.intercept(context);
        if (result && Objects.nonNull(next)) {
            return next.intercept(context);
        } else {
            return result;
        }
    }

}
