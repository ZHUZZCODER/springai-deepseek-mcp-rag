package org.zhu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.zhu.enums.SSEMsgType;
import org.zhu.service.ChatService;
import org.zhu.utils.SSEServer;

@RestController
@RequestMapping("sse")
public class SSEController {

    /**
     * 获取sse连接请求
     * @return
     */
    @GetMapping(path = "connect",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam String userId) {
        return SSEServer.connect(userId);
    }

    /**
     * SSE发送单个消息
     * @param userId
     * @param message
     * @return
     */
    @GetMapping(path = "sendMessage")
    public Object sendMessage(@RequestParam String userId,@RequestParam String message) {
        SSEServer.sendMsg(userId,message, SSEMsgType.message);
        return "success";
    }

    @GetMapping(path = "sendMessageAdd")
    public Object sendMessageAdd(@RequestParam String userId,@RequestParam String message) throws InterruptedException {
        // 写一个for循环
        for (int i = 0; i < 10; i++) {
            Thread.sleep(200);
            SSEServer.sendMsg(userId,message, SSEMsgType.add);
        }
        return "success";
    }

    /**
     * SSE发送所有用户消息
     * @param message
     * @return
     */
    @GetMapping("sendMessageAll")
    public Object sendMessageAll(@RequestParam String message) {
        SSEServer.sendMsgToAllUsers(message);
        return "success";
    }
}
