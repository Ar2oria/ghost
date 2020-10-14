package cc.w0rm.ghost.config.color;

import cc.w0rm.ghost.entity.ConfigRole;
import com.forte.qqrobot.intercept.Context;
import com.forte.qqrobot.sender.intercept.SendContext;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/14 4:18 下午
 */
@Component
public class AllPassStrategy implements InterceptStrategy {
    @Override
    public boolean intercept(Context context, ConfigRole configRole) {

        if (context instanceof SendContext) {
            SendContext sendContext = (SendContext) context;
            if ("sendGroupMsg".equals(sendContext.getMethod().getName())) {
                Object[] params = sendContext.getParams();
                String accountMap = (String) params[0];
                String[] kv = accountMap.split(":");
                String group = kv[1];
                params[0] = group;
                sendContext.setParams(params);
            }
        }

        return true;
    }
}
