package cc.w0rm.ghost.common.dto;

import cc.w0rm.ghost.common.enums.ResponseCodeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse<T> implements Serializable {
    public static final Integer SUCCESS = ResponseCodeEnum.SUCCESS.getCode();
    public static final String SUCCESS_MESSAGE = ResponseCodeEnum.SUCCESS.getMessage();

    public static final Integer DEFAULT_FAILURE = ResponseCodeEnum.UNKNOWN_EXCEPTION.getCode();
    public static final String DEFAULT_FAILURE_MESSAGE = ResponseCodeEnum.UNKNOWN_EXCEPTION.getMessage();

    private Integer code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PageInfo pageInfo;

    public static BaseResponse success() {
        return new BaseResponse(SUCCESS, SUCCESS_MESSAGE, null, null);
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(SUCCESS, SUCCESS_MESSAGE, data, null);
    }

    public static <T> BaseResponse<T> success(T data, PageInfo pageInfo) {
        return new BaseResponse<>(SUCCESS, SUCCESS_MESSAGE, data, pageInfo);
    }

    public static <T> BaseResponse<T> failure(T data) {
        return new BaseResponse<>(DEFAULT_FAILURE, DEFAULT_FAILURE_MESSAGE, data, null);
    }
}
