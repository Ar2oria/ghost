package cc.w0rm.ghost.util;

import org.apache.logging.log4j.util.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : xuyang
 * @date : 2020/10/22 6:36 下午
 */
public class MsgUtil {
    public static final String FILE_REGEX = "\\[CQ:image,file=\\{(.+)}[.].*]";
    private static final Pattern FILE_PATTERN = Pattern.compile(FILE_REGEX);


    public static int hashCode(String msg) {
        if (Strings.isBlank(msg)) {
            return 0;
        }

        int hash = 0;
        Matcher matcher = FILE_PATTERN.matcher(msg);
        while (matcher.find()) {
            String group = matcher.group(1);
            hash = hash * 31 + group.hashCode();
        }

        if (hash > 0) {
            return hash;
        } else {
            return msg.hashCode();
        }
    }

}
