package cc.w0rm.ghost.entity.forward;

import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author : xuyang
 * @date : 2020/10/16 11:54 下午
 */
@ToString
public class Room implements IndexAble<Long> {
    private final long index;
    private volatile boolean clean = false;
    private volatile int msgCount;
    private final ConcurrentHashMap<String, Group> groupMap;

    public Room(long index) {
        this.index = index;
        this.msgCount = 0;
        this.groupMap = new ConcurrentHashMap<>();
    }

    public boolean isCleaned() {
        return clean;
    }

    public boolean tryPut(MsgGet msgGet) {
        if (clean) {
            return false;
        }

        String key;
        if (msgGet instanceof GroupMsg) {
            GroupMsg groupMsg = (GroupMsg) msgGet;
            key = groupMsg.getGroup();
        } else {
            key = msgGet.getThisCode();
        }

        Group group = groupMap.computeIfAbsent(key, k -> new Group(msgGet.getTime()));
        return group.put(msgGet);
    }

    public synchronized List<MsgGet> clean() {
        if (clean) {
            return Collections.emptyList();
        }

        this.clean = true;

        List<Group> collect = groupMap.values().stream()
                .sorted().collect(Collectors.toList());

        return collect.stream().flatMap(group ->
                group.getMsgList().stream()
        ).collect(Collectors.toList());
    }

    public int getMsgCount() {
        return this.msgCount;
    }

    @Override
    public Long getId() {
        return this.index;
    }

    @Override
    public int compareTo(@NotNull IndexAble<Long> o) {
        return (int) (getId() - o.getId());
    }

    @Data
    class Group implements Comparable<Group> {
        private Long time;
        private HashSet<MsgGetWrap> msgWraps;

        public Group(Long time) {
            this.msgWraps = new HashSet<>();
            this.time = time;
        }

        public synchronized boolean put(MsgGet msgGet) {
            msgCount++;
            return msgWraps.add(new MsgGetWrap(msgGet));
        }

        public synchronized List<MsgGet> getMsgList() {
            return msgWraps.stream()
                    .sorted()
                    .map(MsgGetWrap::getMsgGet)
                    .collect(Collectors.toList());
        }


        @Override
        public int compareTo(@NotNull Group o) {
            return (int) (getTime() - o.getTime());
        }


        @Data
        @AllArgsConstructor
        class MsgGetWrap implements Comparable<MsgGetWrap> {
            private MsgGet msgGet;

            @Override
            public int compareTo(@NotNull MsgGetWrap o) {
                int cmp = (int) (msgGet.getTime() - o.getMsgGet().getTime());
                return cmp == 0 ? msgGet.getId().compareTo(o.getMsgGet().getId()) : cmp;
            }


            @Override
            public int hashCode() {
                return msgGet.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof MsgGetWrap) {
                    MsgGetWrap other = (MsgGetWrap) obj;
                    return other.getMsgGet().getId().equals(this.getMsgGet().getId());
                }
                return false;
            }

        }
    }
}
