package cc.w0rm.ghost.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author : xuyang
 * @date : 2020/10/13 8:27 下午
 */

@Data
@AllArgsConstructor
public class Factory<T> {
    private T producer;
    private T consumer;
}
