package cc.w0rm.ghost.util;

import cc.w0rm.ghost.enums.MsgHashMode;
import com.google.common.collect.Lists;
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
    private static final String URL_REGEX = "http[-\\[\\]\\d\\w:/.?=&%;,()]+";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
    private static final String TAO_KOU_LING_REGEX = "[\\p{Sc}/(]\\s?(\\w{9,12})\\s?[\\p{Sc}/)]+";
    private static final Pattern TAO_KOU_LING_PATTERN = Pattern.compile(TAO_KOU_LING_REGEX);
    private static final String SPECIFIC_SYMBOL_REGEX = "[亓元个条只件包套枚块片斤米尺寸千克瓶盒箱杯桶罐gGmMlL\\s]*";
    private static final Pattern SPECIFIC_SYMBOL_PATTERN = Pattern.compile(SPECIFIC_SYMBOL_REGEX);
    private static final String ORDER_MSG_REGEX = "^\\d[-.]";
    private static final Pattern ORDER_MSG_PATTERN = Pattern.compile(ORDER_MSG_REGEX);
    private static final List<Pattern> PATTERNS = Lists.newArrayList(ORDER_MSG_PATTERN,
            FILE_PATTERN, URL_PATTERN, TAO_KOU_LING_PATTERN, SPECIFIC_SYMBOL_PATTERN);


    public static Map<String, String> getFile(String msg) {
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

    public static List<String> listUrls(String msg) {
        if (Strings.isBlank(msg)) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>(1);
        Matcher matcher = URL_PATTERN.matcher(msg);
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

    public static boolean isOrderMsg(String msg) {
        if (Strings.isBlank(msg)) {
            return false;
        }

        return ORDER_MSG_PATTERN.matcher(msg).find();
    }

    public static int hashCode(String msg) {
        return hashCode(msg, MsgHashMode.MINIMUM);
    }

    public static int hashCode(String msg, MsgHashMode msgHashMode) {
        if (Strings.isBlank(msg)) {
            return 0;
        }

        switch (msgHashMode) {
            case MINIMUM:
                return msg.hashCode();
            case NORMAL:
                return msg.replaceFirst(FILE_REGEX, "")
                        .hashCode();
            case STRICT:
                return replace(msg).hashCode();
            default:
                return 0;
        }
    }

    private static String replace(String str) {
        if (Strings.isBlank(str)) {
            return Strings.EMPTY;
        }

        Map<String, String> fileMap = getFile(str);
        if (fileMap.size() == 1) {
            String fileStr = (String) fileMap.values().toArray()[0];
            if (fileStr.equals(str)) {
                return fileStr;
            }
        }

        String returnVal = str;
        for (Pattern pattern : PATTERNS) {
            returnVal = pattern.matcher(returnVal).replaceAll("");
        }

        return returnVal;
    }

}
