package com.midasit.midascafe.base;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping(value = "/")
public class SwaggerController {

    @RequestMapping("/")
    public String homeRedirect() {
        return "redirect:/swagger-ui/index.html";
    }
}