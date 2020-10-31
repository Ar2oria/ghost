package cc.w0rm.ghost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TklConvertDTO{
	@JsonProperty("activity_id")
	private String activityId;
	@JsonProperty("goods_id")
	private String goodsId;
	private String action;
	private String pid;
	private String title;
	@JsonProperty("pic_url")
	private String picUrl;
}
