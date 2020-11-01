package cc.w0rm.ghost.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TklInfoDTO implements Serializable {
	private String surplus;
	private Double rates;
	@JsonProperty("goods_id")
	private String goodsId;
	@JsonProperty("end_price")
	private Double endPrice;
	@JsonProperty("month_sale")
	private Double monthSale;
	private String title;
	@JsonProperty("two_hour_sale")
	private String twoHourSale;
	@JsonProperty("pir_url")
	private String pirUrl;
	private Double money;
	@JsonProperty("today_sale")
	private String todaySale;
	private Double price;
	@JsonProperty("activity_id")
	private String activityId;
	@JsonProperty("coupon_money")
	private Double couponMoney;
	private String desc;
}
