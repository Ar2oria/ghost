package cc.w0rm.ghost.config.color;

import cc.w0rm.ghost.config.role.ConfigRole;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/10/14 4:23 下午
 */
@Component
public class WhiteStrategy extends ColorStrategy implements InterceptStrategy {
    @Override
    public boolean strategy(String qq, String group, ConfigRole configRole) {
        if (!configRole.getQQCode().equals(qq) && !isGlobalInterceptCode(configRole.getQQCode())) {
            return true;
        }
        return configRole.getWhiteSet().contains(group);
    }
}
