package org.zhu.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhu.service.ChatService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api")
public class HelloController {

    @Autowired
    private ChatService chatService;

    @GetMapping("hello")
    public String hello(){
        return "hello world";
    }

    @GetMapping("chat")
    public String chat(String msg){
        return chatService.chatTest(msg);
    }

    @GetMapping("chatResponse")
    public Flux<ChatResponse> chatResponse(String msg){
        return chatService.streamResponse(msg);
    }

    @GetMapping("chatStrResponse")
    public Flux<String> chatStrResponse(String msg, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        return chatService.streamStr(msg);
    }
}
