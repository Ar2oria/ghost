package cc.w0rm.ghost.util;

import cc.w0rm.ghost.enums.DetectionMode;
import org.apache.logging.log4j.util.Strings;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : xuyang
 * @date : 2020/10/22 6:36 下午
 */
public class MsgUtil {
    private static final String FILE_REGEX = "\\[CQ:image,file=\\{(.+)}[.].*]";
    private static final Pattern FILE_PATTERN = Pattern.compile(FILE_REGEX);
    private static final String SHORT_URL_REGEX = "http[\\d\\w:/.]+";
    private static final Pattern SHORT_URL_PATTERN = Pattern.compile(SHORT_URL_REGEX);
    private static final String TAO_KOU_LING_REGEX = "[\\p{Sc}/(]\\s?(\\w{9,12})\\s?[\\p{Sc}/)]+";
    private static final Pattern TAO_KOU_LING_PATTERN = Pattern.compile(TAO_KOU_LING_REGEX);

    public static Map<String, String> getFiles(String msg) {
        if (Strings.isBlank(msg)) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>(1);
        Matcher matcher = FILE_PATTERN.matcher(msg);
        while (matcher.find()) {
            String id = matcher.group(1);
            result.put(id, matcher.group(0));
        }

        return result;
    }

    public static List<String> listShortUrls(String msg) {
        if (Strings.isBlank(msg)) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>(1);
        Matcher matcher = SHORT_URL_PATTERN.matcher(msg);
        while (matcher.find()) {
            String url = matcher.group(0);
            result.add(url);
        }

        return result;
    }

    public static Map<String, String> getTaoKouLing(String msg) {
        if (Strings.isBlank(msg)) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>(1);
        Matcher matcher = TAO_KOU_LING_PATTERN.matcher(msg);
        while (matcher.find()) {
            String code = matcher.group(1);
            result.put(code, matcher.group(0));
        }

        return result;
    }

    public static int hashCode(String msg){
        return hashCode(msg, DetectionMode.STANDARD);
    }
    public static int hashCode(String msg, DetectionMode detectionMode) {
        if (Strings.isBlank(msg)) {
            return 0;
        }

        switch (detectionMode){
            case MINIMUM:
                return msg.hashCode();
            case STANDARD:
                return msg.replaceFirst(FILE_REGEX, "").hashCode();
            case STRICT:
                return msg.replaceAll(TAO_KOU_LING_REGEX, "")
                        .replaceAll(SHORT_URL_REGEX, "")
                        .replaceAll(FILE_REGEX, "")
                        .hashCode();
            default:
                return 0;
        }
    }

}
