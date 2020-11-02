package cc.w0rm.ghost.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TklResponseDTO implements Serializable {
	private String ret;
	private String msg;
	private String picUrl;
	private int code;
	private String validDate;
	private String url;
	private String content;
}
