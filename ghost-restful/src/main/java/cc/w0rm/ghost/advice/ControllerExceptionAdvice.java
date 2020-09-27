package cc.w0rm.ghost.advice;

import cc.w0rm.ghost.common.dto.BaseResponse;
import cc.w0rm.ghost.common.enums.ResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
@ResponseBody
public class ControllerExceptionAdvice {
    @ExceptionHandler(value = IllegalArgumentException.class)
    public BaseResponse illegalArgumentExceptionHandler(Exception e) {
        log.error("IllegalArgumentException", e);
        return BaseResponse.let(ResponseCodeEnum.FAILURE_50003, e.getMessage());
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public BaseResponse illegalStateExceptionHandler(Exception e) {
        log.warn("IllegalStateException", e);
        return BaseResponse.let(ResponseCodeEnum.FAILURE_50004, e.getMessage());
    }

    @ExceptionHandler(value = NullPointerException.class)
    public BaseResponse nullPointerExceptionHandler(Exception e) {
        log.warn("NullPointerException", e);
        return BaseResponse.let(ResponseCodeEnum.FAILURE_50005, e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public BaseResponse baseExceptionHandler(Exception e) {
        log.error("unknownException", e);
        return BaseResponse.let(ResponseCodeEnum.UNKNOWN_EXCEPTION, e.getMessage());
    }

}
