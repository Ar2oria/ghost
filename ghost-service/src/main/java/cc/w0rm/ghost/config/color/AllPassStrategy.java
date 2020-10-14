package cc.w0rm.ghost.config.color;

import cc.w0rm.ghost.entity.ConfigRole;
import com.forte.qqrobot.intercept.Context;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/14 4:18 下午
 */
@Component
public class AllPassStrategy implements InterceptStrategy {
    @Override
    public boolean intercept(Context context, ConfigRole configRole) {
        return true;
    }
}
