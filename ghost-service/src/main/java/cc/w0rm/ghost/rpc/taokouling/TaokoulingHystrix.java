package cc.w0rm.ghost.rpc.taokouling;

import cc.w0rm.ghost.dto.TklJmDTO;
import cc.w0rm.ghost.dto.TklResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : xuyang
 * @date : 2020/11/2 2:48 上午
 */
@Slf4j
@Component
public class TaokoulingHystrix implements TaokoulingService {
    @Override
    public TklResponseDTO tklJm(TklJmDTO tklJmDTO) {
        log.error("请求淘口令网，解析淘口令接口失败，请求参数={}", tklJmDTO);
        return null;
    }
}
