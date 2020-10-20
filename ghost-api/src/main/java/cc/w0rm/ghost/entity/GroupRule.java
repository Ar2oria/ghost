package cc.w0rm.ghost.entity;

import lombok.Data;

/**
 * @author : xuyang
 * @date : 2020/10/13 2:09 上午
 */

@Data
public class GroupRule {
    private String code;
    private Rule producer;
    private Rule consumer;

}
