package cc.w0rm.ghost.entity.forward;

import cc.w0rm.ghost.api.Coordinator;
import cc.w0rm.ghost.config.CoordinatorConfig;
import cn.hutool.core.collection.CollUtil;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
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
    private ForwardStrategy msgExpireStrategy;

    private static final ScheduledExecutorService SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(5,
            new ThreadFactoryBuilder().setNameFormat("ReorderMsgForwardStrategy-ScheduledThreadPool").build());

    @Autowired
    private Coordinator coordinator;


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
                this.msgExpireStrategy = coordinatorConfig.getMsgExpireStrategy();

                if (roomSize == 0){
                    throw new IllegalStateException("初始化转发策略失败： 房间数不能为0");
                }
                if (roomSize <= waitCount){
                    throw new IllegalStateException("初始化转发策略失败：房间数必须大于等待数");
                }

                this.roomList = new CircleIndexArray<>(roomSize);
            }
        }

        SCHEDULED_THREAD_POOL_EXECUTOR.scheduleWithFixedDelay(() -> {
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
                    log.debug("ReorderMsgForwardStrategy: find expire rooms[{}]", expireRooms);
                    expireRooms.forEach(room -> {
                        if (!room.isCleaned()) {
                            forward(room);
                        }
                    });
                }
            } catch (Exception exp) {
                log.error("定时转发任务执行异常", exp);
            }
        }, 0L, this.interval / 2, TimeUnit.MILLISECONDS);
    }

    @Override
    public void forward(MsgGet msgGet) {
        log.debug("ReorderMsgForwardStrategy: receive msg[{}]({})", msgGet.getId(), msgGet.getMsg());
        long msgTime = msgGet.getTimeToLocalDateTime()
                .atZone(ZoneOffset.systemDefault())
                .toInstant().toEpochMilli();
        Room room = findRoom(msgTime);
        if (room == null || !room.tryPut(msgGet)) {
            msgExpireStrategy.forward(msgGet);
        }
        log.debug("ReorderMsgForwardStrategy: msg[{}] ==> room[{}]", msgGet.getId(), room);
    }


    private void forward(Room room) {
        if (room == null || room.isCleaned()) {
            return;
        }

        List<MsgGet> queue = room.clean();
        if (!CollUtil.isEmpty(queue)) {
            for (MsgGet msgGet : queue) {
                try {
                    super.forward(msgGet);
                    log.debug("ReorderMsgForwardStrategy: forward msg[{}] success", msgGet.getId());
                } catch (Exception exp) {
                    log.error("转发消息失败，消息id:{}", msgGet.getId(), exp);
                }
            }
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
        if (nowIdx == msgIdx) {
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
