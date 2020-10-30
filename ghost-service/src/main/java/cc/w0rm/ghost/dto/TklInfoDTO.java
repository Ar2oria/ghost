package cc.w0rm.ghost.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TklInfoDTO implements Serializable {
	private String surplus;
	private Double rates;
	private String goodsId;
	private Double endPrice;
	private Double monthSale;
	private String title;
	private Double twoHourSale;
	private String pirUrl;
	private Double money;
	private Double todaySale;
	private Double price;
	private String activityId;
	private Double couponMoney;
	private String desc;
}
