package bitc.full502.projectbq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping(value = {"/**/{path:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html"; // build 폴더의 index.html을 가리킴
    }
}