package cc.w0rm.ghost.config.role;

import com.forte.qqrobot.beans.messages.QQCodeAble;

import java.util.Set;

/**
 * @author : xuyang
 * @date : 2020/10/13 4:33 下午
 */
public interface ConfigAble extends QQCodeAble {
   /**
    * 获得黑名单
    * @return
    */
   Set<String> getBlackSet();

   /**
    * 获得白名单
    * @return
    */
   Set<String> getWhiteSet();
}
