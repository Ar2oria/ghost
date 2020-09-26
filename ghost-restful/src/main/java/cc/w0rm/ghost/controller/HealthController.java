package cc.w0rm.ghost.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/healthCheck", produces = "application/json; charset=utf-8")
public class HealthController {

    @GetMapping
    @PostMapping
    public String healthCheck(){
        return "ok";
    }
}
