package cc.w0rm.ghost.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo implements Serializable {
    private Integer pageNum;
    private Integer pageSize;
    private Long total;
}
