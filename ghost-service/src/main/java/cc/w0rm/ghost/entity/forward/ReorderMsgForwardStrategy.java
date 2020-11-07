package cc.w0rm.ghost.entity.forward;

import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.common.util.CompletableFutureWithMDC;
import cc.w0rm.ghost.common.util.RunnableWithMDC;
import cc.w0rm.ghost.config.CoordinatorConfig;
import cn.hutool.core.collection.CollUtil;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.Uninterruptibles;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : xuyang
 * @date : 2020/10/16 11:53 下午
 */
@Slf4j
@Component
public class ReorderMsgForwardStrategy extends DefaultForwardStrategy implements ForwardStrategy {
    private static final long START_TIME = Instant.now().toEpochMilli();
    private CircleIndexArray<Room> roomList;

    private long interval;
    private int waitCount;
    private int roomSize;
    private ExpireStrategy msgExpireStrategy;

    private static final ScheduledExecutorService SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(4,
            new ThreadFactoryBuilder().setNameFormat("ReorderMsgForwardStrategy-ScheduledThreadPool").build());

    @Autowired
    private Coordinator coordinator;

    @Autowired
    private Map<String, ExpireStrategy> expireStrategyMap;


    public ReorderMsgForwardStrategy() {
        interval = 5000L;
        waitCount = 2;
        roomSize = 5;
        msgExpireStrategy = new MsgExpireStrategy();
        roomList = new CircleIndexArray<>(roomSize);
    }

    @PostConstruct
    public void init() {
        if (coordinator != null) {
            CoordinatorConfig coordinatorConfig = coordinator.getConfig();
            if (coordinatorConfig != null) {
                this.interval = coordinatorConfig.getIntervalTime();
                this.waitCount = coordinatorConfig.getWaitCount();
                this.roomSize = coordinatorConfig.getRoomSize();
                String expireStrategy = coordinatorConfig.getExpireStrategy();
                if (Strings.isNotBlank(expireStrategy)) {
                    this.msgExpireStrategy = expireStrategyMap.get(expireStrategy);
                    if (msgExpireStrategy == null) {
                        throw new IllegalArgumentException("消息过期策略获取失败，请检查参数");
                    }
                }

                if (roomSize == 0) {
                    throw new IllegalStateException("初始化转发策略失败： 房间数不能为0");
                }
                if (roomSize <= waitCount) {
                    throw new IllegalStateException("初始化转发策略失败：房间数必须大于等待数");
                }

                this.roomList = new CircleIndexArray<>(roomSize);
            }
        }

        SCHEDULED_THREAD_POOL_EXECUTOR.scheduleWithFixedDelay(new RunnableWithMDC(() -> {
            try {
                long curIdx = curIdx();
                long lastIdx = roomList.lastIdx();
                if (lastIdx < curIdx) {
                    Room room = new Room(curIdx);
                    roomList.add(room);
                    log.debug("ReorderMsgForwardStrategy: scheduled add room[{}]", room);
                }

                long expireIdx = curIdx - waitCount;
                List<Room> expireRooms = roomList.range(expireIdx);
                if (CollUtil.isNotEmpty(expireRooms)) {
                    expireRooms.forEach(room -> {
                        if (!room.isCleaned()) {
                            log.debug("ReorderMsgForwardStrategy: find expire rooms[{}]", room);
                            forward(room, (roomSize - (curIdx - room.getId()) - 1) * interval);
                        }
                    });
                }
            } catch (Exception exp) {
                log.error("定时转发任务执行异常", exp);
            }
        }), 0L, this.interval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void forward(MsgGet msgGet) {
        long msgTime = msgGet.getTimeToLocalDateTime()
                .atZone(ZoneOffset.systemDefault())
                .toInstant().toEpochMilli();
        int length = msgGet.getMsg().length();
        length = Math.min(length, 5);
        log.debug("ReorderMsgForwardStrategy:[{}] receive new (msg[{}]time[{}][{}])",
                Instant.now().toEpochMilli(),
                msgGet.getId(),
                msgTime,
                msgGet.getMsg().substring(0, length));

        Room room = findRoom(msgTime);
        log.debug("ReorderMsgForwardStrategy: msg[{}] ==> room[{}]", msgGet.getId(), room);

        if (room == null || curIdx() - getTimeIdx(msgTime) > waitCount) {
            log.warn("msg[{}] is expired, use expire strategy", msgGet.getId());
            msgExpireStrategy.accept(msgGet);
        } else if (!room.tryPut(msgGet)) {
            while (room.getFlag() != 1) {
                Uninterruptibles.sleepUninterruptibly(10, TimeUnit.MILLISECONDS);
            }
            trueForward(msgGet, interval);
        }
    }


    private void forward(Room room, long waitTime) {
        if (room == null || room.isCleaned()) {
            return;
        }

        List<MsgGet> queue = room.clean();
        if (!CollUtil.isEmpty(queue)) {
            for (MsgGet msgGet : queue) {
                trueForward(msgGet, waitTime / room.getMsgCount() * 2);
            }
        }
        room.setFlag(1);
        log.debug("room[{}] all msg is forward", room.getId());
    }

    private void trueForward(MsgGet msgGet, long waitTime) {
        Runnable runnable = new RunnableWithMDC(() -> super.forward(msgGet));

        CompletableFuture<Void> future = CompletableFutureWithMDC.runAsyncWithMdc(runnable, SCHEDULED_THREAD_POOL_EXECUTOR)
                .whenComplete((v, exp) -> {
                    if (exp != null) {
                        log.error("转发消息[{}]，消费者执行中出现未知异常", msgGet.getId(), exp);
                    }
                });
        try {
            future.get(waitTime, TimeUnit.MILLISECONDS);
            log.debug("ReorderMsgForwardStrategy: forward msg[{}] success", msgGet.getId());
        } catch (Exception exp) {
            log.error("转发消息[{}]，等待超时，请查看网络是否出现异常或代码中出现死循环", msgGet.getId(), exp);
        }
    }

    private Room findRoom(long msgTime) {
        long now = Instant.now().toEpochMilli();
        long nowIdx = getTimeIdx(now);
        long lastIdx = roomList.lastIdx();

        Room lastRoom;
        if (nowIdx > lastIdx) {
            lastRoom = new Room(nowIdx);
            boolean add = roomList.add(lastRoom);
            if (!add) {
                lastRoom = roomList.last();
            }
        } else {
            lastRoom = roomList.last();
        }

        Room returnRoom;
        long msgIdx = getTimeIdx(msgTime);
        if (msgIdx > nowIdx) {
            throw new MsgForwardException("消息时间序列异常，当前时间:" + now + ", 消息时间:" + msgTime);
        } else if (nowIdx == msgIdx) {
            returnRoom = lastRoom;
        } else {
            returnRoom = roomList.firstLowerOrEqual(msgIdx);
        }

        return returnRoom;
    }

    private long curIdx() {
        return getTimeIdx(Instant.now().toEpochMilli());
    }

    private long getTimeIdx(long time) {
        long intervalTime = Math.max(0L, time - START_TIME);
        return intervalTime / this.interval;
    }
}
