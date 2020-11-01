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
 * @date : 2020/10/31 3:46 下午
 */

@Component
public class UrlDetector implements Detector {

    @Override
    public @Nonnull List<PreTestText> detect(String msg) {
        if (Strings.isBlank(msg)){
            return Collections.emptyList();
        }
        Map<String, String> urlMap = MsgUtil.listUrls(msg);

        return urlMap.keySet().stream()
                .map(domain->{
                    PreTestText preTestText = new PreTestText();
                    preTestText.setTextType(TextType.URL);
                    preTestText.setFind(domain);
                    preTestText.setSource(urlMap.get(domain));
                    return preTestText;
                }).collect(Collectors.toList());
    }
}
