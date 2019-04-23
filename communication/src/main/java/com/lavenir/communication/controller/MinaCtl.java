package com.lavenir.communication.controller;

import com.lavenir.communication.utils.MinaClient;
import com.lavenir.communication.utils.MinaClientHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mina")
public class MinaCtl {

    @RequestMapping("/startClient")
    public String startClient(){
        String hostname = "127.0.0.1";
        int port = 10026;
        MinaClient.start(hostname, port);
        return "success";
    }

    @RequestMapping("/sendMessage")
    public String sendMessage(@RequestParam String message){
        MinaClientHandler.sessionWrite(message);
        return "success";
    }
}
