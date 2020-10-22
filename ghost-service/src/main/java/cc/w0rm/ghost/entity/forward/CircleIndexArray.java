package cc.w0rm.ghost.entity.forward;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author : xuyang
 * @date : 2020/10/18 10:49 上午
 */

/**
 * 循环数组，元素需要带有记号
 * @param <T>
 */
public class CircleIndexArray<T extends IndexAble<Long>> {
    /**
     * 尾指针
     */
    private volatile int tail;

    /**
     * 最后一个元素的记号
     */
    private volatile long lastIdx;
    /**
     * 数组元素
     */
    private final IndexAble<Long>[] elementData;

    @SuppressWarnings("unchecked")
    public CircleIndexArray(int size) {
        this.tail = 0;
        this.lastIdx = Long.MIN_VALUE;
        this.elementData = new IndexAble[size];
    }

    /**
     * 返回循环数组末尾的元素，不存在元素返回null
     * @return
     */
    public T last() {
        if (lastIdx == Long.MIN_VALUE){
            return null;
        }

        return elemAt(elementData, tail - 1);
    }

    /**
     * 返回最后一个元素的记号，不存在元素返回Long.MIN_VALUE
     * @return
     */
    public long lastIdx() {
        return lastIdx;
    }

    /**
     * 获得所有小于等于记号的元素
     * @param index
     * @return
     */
    public List<T> range(long index) {
        if (index > lastIdx) {
            return Collections.emptyList();
        }

        /*
         * 防止多线程下冲突，复制一份数据
         */
        int copyTail = tail;
        IndexAble<Long>[] copyData = Arrays.copyOf(elementData, elementData.length);

        /*
         * 查找第一个小于等于记号的元素的位置
         */
        int findStart = copyTail - 1;
        int idx = circleFindIndexFirstLowerOrEqual(copyData, findStart, index);
        if (idx == -1) {
            return Collections.emptyList();
        }

        /*
         * 返回list
         */
        ArrayList<T> result = Lists.newArrayListWithCapacity(copyData.length);
        int i = copyTail;
        while (i != idx) {
            if (copyData[i] != null) {
                result.add(elemAt(copyData, i));
            }
            i = (i + 1) % copyData.length;
        }
        result.add(elemAt(copyData, i));

        return result;
    }

    /**
     * 先循环数组中添加元素
     * @param data
     * @return
     */
    public synchronized boolean add(T data) {
        if (data == null || data.getId() <= lastIdx) {
            return false;
        }

        this.elementData[tail] = data;
        this.lastIdx = data.getId();
        this.tail = (tail + 1) % elementData.length;
        return true;
    }

    /**
     * 设置记号位置的元素，如果没有找到记号返回false
     * @param index
     * @param data
     * @return
     */
    public synchronized boolean set(long index, T data) {
        if (data == null || data.getId() > lastIdx) {
            return false;
        }

        int position = position(index);
        if (position != -1) {
            this.elementData[position] = data;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 返回指定记号位置的元素
     * @param index
     * @return
     */
    public T at(long index) {
        if (index > lastIdx) {
            return null;
        }
        T t = firstLowerOrEqual(index);
        return t != null && t.getId() == index ? t : null;
    }

    /**
     * 通过记号返回数组下标
     * @param index
     * @return
     */
    private int position(long index) {
        if (index > lastIdx) {
            return -1;
        }

        int copyTail = this.tail;
        IndexAble<Long>[] copyData = Arrays.copyOf(elementData, elementData.length);
        return circleFindIndex(copyData, copyTail - 1, index);
    }

    /**
     * 找到第一个小于等于记号的元素
     * @param index
     * @return
     */
    public T firstLowerOrEqual(long index) {
        if (index > lastIdx) {
            return null;
        }

        int copyTail = this.tail;
        IndexAble<Long>[] copyData = Arrays.copyOf(elementData, elementData.length);
        int idx = circleFindIndexFirstLowerOrEqual(copyData, copyTail - 1, index);
        return idx == -1 ? null : elemAt(copyData, idx);
    }


    /**
     * 循环查找等于指定记号的元素
     * @param array 数组
     * @param start 起始查找位置
     * @param index 记号
     * @return
     */
    private int circleFindIndex(IndexAble<Long>[] array, int start, long index) {
        if (array == null || start < 0 || start >= array.length) {
            return -1;
        }

        int idx = circleFindIndexFirstLowerOrEqual(array, start, index);
        if (idx == -1) {
            return -1;
        } else {
            IndexAble<Long> indexAble = array[idx];
            return indexAble.getId() == index ? idx : -1;
        }
    }

    /**
     * 查找第一个小于或者等于记号的元素
     * @param array 数组
     * @param start 起始位置
     * @param index 记号
     * @return
     */
    private int circleFindIndexFirstLowerOrEqual(IndexAble<Long>[] array, int start, long index) {
        if (array == null || start < 0 || start >= array.length) {
            return -1;
        }

        int realIndex = start;
        while (array[realIndex] != null
                && array[realIndex].getId() > index
                && realIndex != start + 1) {
            realIndex = (elementData.length + realIndex - 1) % elementData.length;
        }

        IndexAble<Long> obj = array[realIndex];
        return obj != null
                && obj.getId() <= index ? realIndex : -1;
    }

    @SuppressWarnings("unchecked")
    private T elemAt(IndexAble<Long>[] array, int idx) {
        idx = (idx + elementData.length) % elementData.length;
        return array == null ? null : (T) array[idx];
    }

}
