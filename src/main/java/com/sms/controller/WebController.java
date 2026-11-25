package com.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "forward:/index.html";
    }

    @GetMapping("/admin")
    public String admin() {
        return "forward:/admin-dashboard.html";
    }

    @GetMapping("/faculty")
    public String faculty() {
        return "forward:/faculty-dashboard.html";
    }

    @GetMapping("/student")
    public String student() {
        return "forward:/student-dashboard.html";
    }
}
