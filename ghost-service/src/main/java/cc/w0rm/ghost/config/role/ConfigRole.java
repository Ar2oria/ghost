package cc.w0rm.ghost.config.role;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * @author : xuyang
 * @date : 2020/10/13 4:46 下午
 */

@EqualsAndHashCode
@ToString
public class ConfigRole implements ConfigAble {
    private String code;
    private Set<String> blackSet;
    private Set<String> whiteSet;

    public ConfigRole(String code){
        this.code = code;
        this.blackSet = new HashSet<>();
        this.whiteSet = new HashSet<>();
    }

    public ConfigRole(String code, Set<String> blackSet, Set<String> whiteSet) {
        this.code = code;
        this.blackSet = blackSet;
        this.whiteSet = whiteSet;
    }


    @Override
    public String getQQCode() {
        return code;
    }

    @Override
    public Set<String> getBlackSet() {
        return blackSet;
    }

    @Override
    public Set<String> getWhiteSet() {
        return whiteSet;
    }
}
