package cc.w0rm.ghost.common.enums;

public enum ResponseCodeEnum {
    // 状态码

    SUCCESS(0, "请求成功"),
    UNKNOWN_EXCEPTION(50000, "未知异常"),
    FAILURE_50001(50001, "用户身份认证失败"),
    FAILURE_50002(50002, "权限不足"),
    FAILURE_50003(50003, "参数错误"),
    FAILURE_50004(50004, "状态异常"),
    FAILURE_50005(50005,"空指针异常")

    ;

    ResponseCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
