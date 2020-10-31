package cc.w0rm.ghost.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaozouResponseDTO<T>{
	private int code;
	private T data;
	private String errmsg;
}
