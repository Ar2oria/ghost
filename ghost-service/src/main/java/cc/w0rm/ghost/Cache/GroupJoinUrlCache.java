package cc.w0rm.ghost.Cache;

import com.google.common.collect.Maps;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author panyupeng
 * @date 2020-10-27 00:18
 */
public class GroupJoinUrlCache {
    
    public static Map<String,String> group2Url = Maps.newHashMap();
    
    @PostConstruct
    private void init(){
    
    }
    
}
