package cc.w0rm.ghost.entity.resolver.detect;

import cc.w0rm.ghost.common.util.Strings;
import cc.w0rm.ghost.enums.TextType;
import cc.w0rm.ghost.util.MsgUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/31 3:53 下午
 */
@Component
public class TklDetector implements Detector {
    @Override
    public @Nonnull List<PreTestText> detect(String msg) {
        if (Strings.isBlank(msg)){
            return Collections.emptyList();
        }

        Map<String, String> taokouling = MsgUtil.getTaokouling(msg);

        return taokouling.keySet().stream()
                .map(tkl->{
                    PreTestText preTestText = new PreTestText();
                    preTestText.setTextType(TextType.TAOKOULING);
                    preTestText.setFind(tkl);
                    preTestText.setSource(taokouling.get(tkl));
                    return preTestText;
                }).collect(Collectors.toList());
    }
}
