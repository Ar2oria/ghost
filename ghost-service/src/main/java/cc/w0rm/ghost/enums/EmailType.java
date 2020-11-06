package cc.w0rm.ghost.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  EmailType {

    INCREASE(0, "增加"),
    DECREASE(1, "减少")

    ;
    private final Integer code;
    private final String desc;
}
