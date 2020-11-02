package cc.w0rm.ghost.entity.resolver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author : xuyang
 * @date : 2020/11/2 12:55 上午
 */

@Getter
@Builder
@AllArgsConstructor
public class Domain {

    private final String domain;
    private final Pattern pattern;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Domain) {
            return super.equals(obj);
        }

        if (obj instanceof String) {
            return (Objects.nonNull(domain) && domain.equals(obj))
                    || (Objects.nonNull(pattern) && pattern.matcher(obj.toString()).find());
        }

        return false;
    }
}
