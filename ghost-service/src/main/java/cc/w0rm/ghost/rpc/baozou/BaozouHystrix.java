package cc.w0rm.ghost.rpc.baozou;

import cc.w0rm.ghost.dto.BaozouResponseDTO;
import cc.w0rm.ghost.dto.TklConvertDTO;
import cc.w0rm.ghost.dto.TklInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

 /**
 * @author : xuyang
 * @date : 2020/10/30 2:02 上午
 */

 @Slf4j
@Component
public class BaozouHystrix implements BaozouService {
    @Override
    public BaozouResponseDTO<TklInfoDTO> tklDecrypt(String tkl) {
        log.error("请求暴走工具箱，解析淘口令接口失败，请求参数 tkl={},", tkl);
        return null;
    }

    @Override
    public BaozouResponseDTO<Object> convertMiddle(TklConvertDTO tklConvertDTO) {
        log.error("请求暴走工具箱，淘口令转换接口失败，请求参数 ={}", tklConvertDTO);
        return null;
    }
}
