package cc.w0rm.ghost.entity.forward;

import java.io.Serializable;

/**
 * @author : xuyang
 * @date : 2020/10/18 11:50 上午
 */
public interface IndexAble<T> extends Serializable, Comparable<IndexAble<T>> {
    T getId();
}
