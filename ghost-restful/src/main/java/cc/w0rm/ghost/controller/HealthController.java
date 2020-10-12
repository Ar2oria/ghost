package cc.w0rm.ghost.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/healthCheck", produces = "application/json; charset=utf-8")
public class HealthController {

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String healthCheck(){
        return "ok";
    }
}
