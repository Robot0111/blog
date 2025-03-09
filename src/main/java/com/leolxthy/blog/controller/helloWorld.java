package com.leolxthy.blog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class helloWorld {

    private final JdbcTemplate jdbcTemplate;

    public helloWorld(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @RequestMapping("/home")
    String home(){
        return "home";
    }
//
//    @GetMapping("/")
//    String home(Model model){
//        return "login";
//    }
    @GetMapping("/login")
    String login(){
        return "login";
    }
    @GetMapping("/admin")
    String index(){
        return "admin";
    }

}