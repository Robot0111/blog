package com.leolxthy.blog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class helloWorld {

    @RequestMapping("/")
    String home(){

        return "Hello World!";
    }
}
