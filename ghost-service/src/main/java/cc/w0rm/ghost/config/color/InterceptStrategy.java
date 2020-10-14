package cc.w0rm.ghost.config.color;

import cc.w0rm.ghost.entity.ConfigRole;
import com.forte.qqrobot.intercept.Context;

/**
 * @author : xuyang
 * @date : 2020/10/14 4:15 下午
 */

public interface InterceptStrategy {
    boolean intercept(Context context, ConfigRole configRole);

    default boolean isGlobalInterceptCode(String str){
        return "#".equals(str);
    }
}
