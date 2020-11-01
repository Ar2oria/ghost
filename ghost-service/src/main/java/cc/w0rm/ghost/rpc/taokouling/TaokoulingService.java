package cc.w0rm.ghost.rpc.taokouling;

import cc.w0rm.ghost.config.feign.FeignConfig;
import cc.w0rm.ghost.dto.TklJmDTO;
import cc.w0rm.ghost.dto.TklResponseDTO;
import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * @author : xuyang
 * @date : 2020/11/2 2:48 上午
 */
@FeignClient(
        name = "baoZouSerivce",
        url = "${url.taokouling}",
        configuration = FeignConfig.class,
        fallback = TaokoulingHystrix.class)
public interface TaokoulingService {

    @RequestLine(value = "POST /tkl/tkljm")
    @Headers(value = {"Content-Type: application/json"})
    TklResponseDTO tklJm(TklJmDTO tklJmDTO);

}
