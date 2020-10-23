package cc.w0rm.ghost.entity.forward;

/**
 * @author : xuyang
 * @date : 2020/10/17 12:42 上午
 */

public class MsgForwardException extends RuntimeException {
    public MsgForwardException(){
        super();
    }

    public MsgForwardException(String msg){
        super(msg);
    }

    public MsgForwardException(String msg, Exception exp){
        super(msg, exp);
    }

}
