package cc.w0rm.ghost.rpc.baozou;

import cc.w0rm.ghost.config.feign.FeignConfig;
import cc.w0rm.ghost.dto.BaozouResponseDTO;
import cc.w0rm.ghost.dto.TklConvertDTO;
import cc.w0rm.ghost.dto.TklInfoDTO;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * @author : xuyang
 * @date : 2020/10/30 1:35 上午
 */

@FeignClient(
        name = "baoZouSerivce",
        url = "${url.baozou}",
        configuration = FeignConfig.class,
        fallback = BaoZouHystrix.class)
public interface BaoZouService {

    @RequestLine(value = "POST /tool/tkl_decrypt")
    @Headers({"Content-Type: application/json",
            "Host: m.mzsmn.com",
            "Origin: http://m.mzsmn.com",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36"})
    BaozouResponseDTO<TklInfoDTO> tklDecrypt(@Param("tkl") String tkl);

    @RequestLine(value = "POST /convert_middle")
    @Headers({"Content-Type: application/json",
            "Host: m.mzsmn.com",
            "Origin: http://m.mzsmn.com",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36"})
    BaozouResponseDTO<?> convertMiddle(TklConvertDTO tklConvertDTO);
}
