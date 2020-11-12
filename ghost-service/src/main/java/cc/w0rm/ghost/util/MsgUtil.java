package cc.w0rm.ghost.util;

import cc.w0rm.ghost.enums.MsgHashMode;
import com.simplerobot.modules.utils.KQCodeUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : xuyang
 * @date : 2020/10/22 6:36 下午
 */
public class MsgUtil {
    public static final String CHINESE = "[\\u4e00-\\u9fa5]";
    public static final Pattern CHINESESE_PATTERN = Pattern.compile(CHINESE);
    public static final String FILE_REGEX = "\\[CQ:image,file=\\{(.+)}[.].*]";
    public static final Pattern FILE_PATTERN = Pattern.compile(FILE_REGEX);

    public static final String SHORT_URL_REGEX = "http[\\d\\w:/.]+";
    public static final Pattern SHORT_URL_PATTERN = Pattern.compile(SHORT_URL_REGEX);
    public static final String URL_REGEX = "http(s?)://([-.\\w\\d]+)[-\\d\\w/.?=&%]*";
    public static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public static final String CHINESE_REGEX = "[\\u4e00-\\u9fa5]+";
    public static final Pattern CHINESE_PATTERN = Pattern.compile(CHINESE_REGEX);

    public static final String TAO_KOU_LING_REGEX = "([\\p{Sc}/()]|[\\uD83C\\uDF00-\\uD83D\\uDDFF]|[\\uD83E\\uDD00-\\uD83E\\uDDFF]|[\\uD83D\\uDE00-\\uD83D\\uDE4F])\\s?([a-zA-Z0-9]{11})\\s?([\\p{Sc}/()]+|[\\uD83C\\uDF00-\\uD83D\\uDDFF]|[\\uD83E\\uDD00-\\uD83E\\uDDFF]|[\\uD83D\\uDE00-\\uD83D\\uDE4F])";
    public static final Pattern TAO_KOU_LING_PATTERN = Pattern.compile(TAO_KOU_LING_REGEX);
    public static final String TAOBAO_CLICK_URL_REGEX = "https://s[.]click[.]taobao[.]com/\\w{5,9}";
    public static final Pattern TAOBAO_CLICK_URL_PATTERN = Pattern.compile(TAOBAO_CLICK_URL_REGEX);
    public static final String TAOBAO_SHORT_URL_REGEX = "https://m[.]tb[.]cn/h[.]\\w{5,9}";
    public static final Pattern TAOBAO_SHORT_URL_PATTERN = Pattern.compile(TAOBAO_SHORT_URL_REGEX);

    public static final String JD_SHORT_URL_REGEX = "https://u[.]jd[.]com/\\w{5,9}";
    public static final Pattern JD_SHORT_URL_PATTERN = Pattern.compile(JD_SHORT_URL_REGEX);
    public static final String JD_COUPON_URL_REGEX = "https://coupon[.]m[.]jd[.]com/[-\\[\\]\\d\\w:/.?=&%;,()]+";
    public static final Pattern JD_COUPON_URL_PATTERN = Pattern.compile(JD_COUPON_URL_REGEX);

    public static final String SPECIFIC_SYMBOL_REGEX = "[劵券卷quan亓元]*";
    public static final Pattern SPECIFIC_SYMBOL_PATTERN = Pattern.compile(SPECIFIC_SYMBOL_REGEX);

    public static final String AT_ALL_REGEX = "com[.]simplerobot[.]modules[.]utils[.]AtAll@[\\d\\w]+";
    public static final String AT_ALL_MSG = KQCodeUtils.INSTANCE.toCq("at", "qq=all");
    public static final Pattern AT_ALL_PATTERN = Pattern.compile(AT_ALL_REGEX);

    public static final String ELEME_REGEX ="饿了[么嘛吗嚒妈马]";
    public static final Pattern ELEME_PATTERN = Pattern.compile(ELEME_REGEX);

    public static final String MEITUAN_REGEX = "美团";
    public static final Pattern MEITUAN_PATTERN = Pattern.compile(MEITUAN_REGEX);

    public static final String C88_10_REGEX = "[\\u4e00-\\u9fa5]88-10|88-10[\\u4e00-\\u9fa5]";
    public static final Pattern C88_10_PATTERN = Pattern.compile(C88_10_REGEX);

    public static Map<String, String> getFile(String msg) {
        if (Strings.isBlank(msg)) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>(1);
        Matcher matcher = FILE_PATTERN.matcher(msg);
        while (matcher.find()) {
            result.put(matcher.group(), matcher.group(1));
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

    public static Map<String, String> listUrls(String msg) {
        if (Strings.isBlank(msg)) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>(1);
        Matcher matcher = URL_PATTERN.matcher(msg);
        while (matcher.find()) {
            result.put(matcher.group(), matcher.group(2));
        }

        return result;
    }


    public static Map<String, String> getTaokouling(String msg) {
        if (Strings.isBlank(msg)) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>(1);
        Matcher matcher = TAO_KOU_LING_PATTERN.matcher(msg);
        while (matcher.find()) {
            String code = "￥" + matcher.group(2) + "￥";
            result.put(matcher.group(), code);
        }

        return result;
    }

    public static String replaceAtAll(String msg) {
        if (Strings.isBlank(msg)) {
            return msg;
        }

        return AT_ALL_PATTERN.matcher(msg).replaceAll(AT_ALL_MSG);
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


    public static String replace(String str) {
        if (Strings.isBlank(str)) {
            return Strings.EMPTY;
        }

        // 1. 对只包含一张图片的消息单独处理
        Matcher matcher = FILE_PATTERN.matcher(str);
        if (matcher.find()) {
            String image = matcher.group();
            if (str.trim().equals(image)) {
                return matcher.group(1);
            }
        }

        // 2. 全部字符替换
        StringBuilder stringBuilder = new StringBuilder();
        Matcher chn = CHINESE_PATTERN.matcher(str);
        while (chn.find()) {
            stringBuilder.append(chn.group());
        }

        String chnMsg = stringBuilder.toString();
        if (Strings.isBlank(chnMsg)) {
            return Strings.EMPTY;
        }

        return SPECIFIC_SYMBOL_PATTERN.matcher(chnMsg)
                .replaceAll("");
    }

    public static List<String> listTbClickUrls(String msg) {
        if (Strings.isBlank(msg)) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>(1);
        Matcher matcher = TAOBAO_CLICK_URL_PATTERN.matcher(msg);
        while (matcher.find()) {
            result.add(matcher.group(0));
        }

        return result;
    }

    public static List<String> listTbShortUrls(String msg) {
        if (Strings.isBlank(msg)) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>(1);
        Matcher matcher = TAOBAO_SHORT_URL_PATTERN.matcher(msg);
        while (matcher.find()) {
            result.add(matcher.group(0));
        }

        return result;
    }

    public static List<String> listJdShortUrls(String msg) {
        if (Strings.isBlank(msg)) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>(1);
        Matcher matcher = JD_SHORT_URL_PATTERN.matcher(msg);
        while (matcher.find()) {
            result.add(matcher.group(0));
        }

        return result;
    }

    public static List<String> listJdCouponUrls(String msg) {
        if (Strings.isBlank(msg)) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>(1);
        Matcher matcher = JD_COUPON_URL_PATTERN.matcher(msg);
        while (matcher.find()) {
            result.add(matcher.group(0));
        }

        return result;
    }

}
