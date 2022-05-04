package com.midasit.midascafe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class RootController {

    @GetMapping("/health.html")
    @ResponseBody
    public Boolean healthCheck() {
        return true;
    }

    @GetMapping("/robots.txt")
    public void robots(HttpServletResponse response) throws IOException {
        response.getWriter()
                .println("User-agent: *\n" + "Disallow: /");
    }
}
