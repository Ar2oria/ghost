package cc.w0rm.ghost.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xuyang
 * @date : 2020/10/31 3:44 下午
 */

@Getter
@AllArgsConstructor
public enum  TextType {

    URL(0, "链接"),
    TAOKOULING(1, "淘口令"),
    SOURCE_TEXT(2, "原始文本")

    ;
    private final Integer code;
    private final String desc;
}
