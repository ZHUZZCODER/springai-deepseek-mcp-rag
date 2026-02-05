package org.zhu.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zhu.bean.ChatEntity;
import org.zhu.service.ChatService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("doChat")
    public void doChat(@RequestBody ChatEntity chatEntity){
        chatService.doChat(chatEntity);
    }


}
