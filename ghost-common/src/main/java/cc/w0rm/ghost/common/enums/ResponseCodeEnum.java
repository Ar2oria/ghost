package cc.w0rm.ghost.common.enums;

public enum ResponseCodeEnum {
    // 状态码
    UNKNOWN_EXCEPTION(50000, "未知异常"),
    SUCCESS(0, "请求成功"),
    SUCCESS_40003(40001, "用户身份认证失败"),
    SUCCESS_40004(40002, "权限不足"),
    SUCCESS_40005(40003, "参数错误"),
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
