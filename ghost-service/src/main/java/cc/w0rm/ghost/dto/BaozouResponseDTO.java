package cc.w0rm.ghost.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaozouResponseDTO<T> implements Serializable {
	private int code;
	private T data;
	private String errmsg;
}
