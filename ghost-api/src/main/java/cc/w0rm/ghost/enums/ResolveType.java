package cc.w0rm.ghost.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xuyang
 * @date : 2020/10/30 5:24 下午
 */

@Getter
@AllArgsConstructor
public enum  ResolveType {
    UNSUPPORT_URL(-2, "不支持的链接"),
    NONE(-1, "废话"),
    SUCCESS(0, "解析成功"),

    ;
    private Integer code;
    private String desc;
}
