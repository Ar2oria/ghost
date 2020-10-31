package cc.w0rm.ghost.dto;

import cc.w0rm.ghost.enums.ResolveType;
import lombok.Data;

import java.util.List;

/**
 * @author : xuyang
 * @date : 2020/10/30 5:20 下午
 */

@Data
public class MsgInfoDTO {
    private ResolveType resolveType;
    private List<CommodityDetailDTO> resolveList;
    private String modifiedMsg;
}
