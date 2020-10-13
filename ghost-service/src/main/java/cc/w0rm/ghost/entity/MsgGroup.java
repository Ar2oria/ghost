package cc.w0rm.ghost.entity;

import com.google.common.collect.ImmutableSet;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author : xuyang
 * @date : 2020/10/13 2:23 下午
 */

@Data
@NoArgsConstructor
public class MsgGroup {
    private String name;
    private ImmutableSet<Producer> producer;
    private ImmutableSet<Consumer> consumer;

    public MsgGroup(String name, Set<Producer> producerSet, Set<Consumer> consumerSet){
        this.name = name;
        this.producer = ImmutableSet.copyOf(producerSet);
        this.consumer = ImmutableSet.copyOf(consumerSet);
    }
}
