package com.lavenir.communication.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/http")
public class HttpCtl {

    @RequestMapping("/httpPost")
    public String httpPost(@RequestBody String content){
        System.out.println("content:"+content);
        return "success";
    }
}
