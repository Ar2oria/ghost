package cc.w0rm.ghost.config.color;

import cc.w0rm.ghost.config.role.ConfigRole;
import cn.hutool.core.collection.CollUtil;
import com.forte.qqrobot.intercept.Context;
import com.forte.qqrobot.intercept.Interceptor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/14 4:25 下午
 */
@Component
public class InterceptContext {
    @Resource
    private List<InterceptStrategy> strategyList;

    private Map<Class<?>, InterceptStrategy> strategyMap;

    @PostConstruct
    public void init(){
        strategyMap = strategyList.stream()
                .collect(Collectors.toMap(InterceptStrategy::getClass, Function.identity()));
    }

    public Interceptor getInterceptStrategy(ConfigRole configRole){
        InterceptStrategy strategy = null;
        if (CollUtil.isEmpty(configRole.getBlackSet()) && CollUtil.isEmpty(configRole.getWhiteSet())) {
            strategy = strategyMap.get(AllPassStrategy.class);
        } else if (CollUtil.isNotEmpty(configRole.getWhiteSet())) {
            strategy = strategyMap.get(WhiteStrategy.class);
        } else if (CollUtil.isNotEmpty(configRole.getBlackSet())) {
            strategy = strategyMap.get(BlackStrategy.class);
        }

        final InterceptStrategy st = strategy;
        return (Interceptor<Object, Context<Object>>) context -> st.intercept(context, configRole);
    }

}
