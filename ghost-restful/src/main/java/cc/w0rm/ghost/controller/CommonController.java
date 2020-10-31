package cc.w0rm.ghost.controller;

import cc.w0rm.ghost.api.MsgResolver;
import cc.w0rm.ghost.common.dto.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : xuyang
 * @date : 2020/10/31 1:19 上午
 */

@RestController
@RequestMapping(value = "/util")
public class CommonController {

    @Autowired
    private MsgResolver msgResolver;

    @GetMapping("/convert")
    public BaseResponse<?> convertMsg(String msg, String group){
        return BaseResponse.success(msgResolver.resolve(msg, group));
    }

}
