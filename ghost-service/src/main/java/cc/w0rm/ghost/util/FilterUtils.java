package cc.w0rm.ghost.util;

import java.util.regex.Matcher;

/**
 * @author panyupeng
 * @date 2020-10-28 10:40
 */
public class FilterUtils {
    
    private static final int CHINESE_THRESHOLD = 40;
    
    public static boolean isFilterByVideo(String msg) {
        return msg.contains("视频");
    }
    
    public static boolean isFilterByTB(String msg) {
        return MsgUtil.TAO_KOU_LING_PATTERN.matcher(msg).find();
    }
    
    public static boolean isFilterByChineseCount(String msg) {
        int chineseCount = 0;
        Matcher matcher = MsgUtil.CHINESESE_PATTERN.matcher(msg);
        while (matcher.find()) {
            chineseCount++;
        }
        if (chineseCount == 0) {
            return false;
        }
        if (msg.startsWith("@")) {
            return chineseCount < CHINESE_THRESHOLD;
        }
        if (chineseCount < CHINESE_THRESHOLD) {
            return MsgUtil.FILE_PATTERN.matcher(msg).find() || msg.contains("http");
        }
        return true;
    }
}