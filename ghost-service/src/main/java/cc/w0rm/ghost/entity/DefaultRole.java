package cc.w0rm.ghost.entity;

import com.forte.qqrobot.bot.BotInfo;
import com.forte.qqrobot.bot.BotSender;
import com.forte.qqrobot.bot.LoginInfo;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.util.Set;

/**
 * @author : xuyang
 * @date : 2020/10/13 2:28 下午
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public class DefaultRole extends ConfigRole implements ClientRole {
    private BotInfo botInfo;

    public DefaultRole(BotInfo botInfo, Set<String> blackSet, Set<String> whiteSet) {
        super(botInfo.getBotCode(), blackSet, whiteSet);

        this.botInfo = botInfo;
    }

    @Override
    public String getBotCode() {
        return botInfo.getBotCode();
    }

    @Override
    public String getPath() {
        return botInfo.getPath();
    }

    @Override
    public LoginInfo getInfo() {
        return botInfo.getInfo();
    }

    @Override
    public BotSender getSender() {
        return botInfo.getSender();
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        botInfo.close();
    }
}
