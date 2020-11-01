package cc.w0rm.ghost.entity.resolver.detect;

import cc.w0rm.ghost.enums.TextType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : xuyang
 * @date : 2020/10/31 3:34 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreTestText {
    private TextType textType;
    private String find;
    private String source;
}
