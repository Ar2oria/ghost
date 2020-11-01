package cc.w0rm.ghost.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : xuyang
 * @date : 2020/11/2 2:51 上午
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TklJmDTO implements Serializable {
    private String apikey;
    private String tkl;
}
