package org.zhu.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.zhu.enums.SSEMsgType;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 定义see服务
 */
@Slf4j
public class SSEServer {

    private static final Map<String,SseEmitter> sseClients = new ConcurrentHashMap<>();

    /**
     * 创建sse连接
     * @param userId
     * @return
     */
    public static SseEmitter connect(String userId){
        // 设置超时时间，OL表示不超时(永不过期)，默认设计30秒，超时未完成任务则会抛出异常
        SseEmitter sseEmitter = new SseEmitter(0L);

        // 注册回到方法
        sseEmitter.onTimeout(timeoutCallback(userId));
        sseEmitter.onCompletion(completionCallback(userId));
        sseEmitter.onError(errorCallback(userId));

        sseClients.put(userId, sseEmitter);

        log.info("SSE连接创建成功，连接的用户ID为：{}",userId);

        return sseEmitter;
    }

    /**
     * 发送单个sse消息
     * @param userId
     * @param message
     */
    public static void sendMsg(String userId, String message,SSEMsgType msgType){
        if(CollectionUtils.isEmpty(sseClients)){
            return;
        }

       if(sseClients.containsKey(userId)){
            SseEmitter sseEmitter = sseClients.get(userId);
            // 发送消息
            sendEmitterMessage(sseEmitter,userId,message,msgType);
        }
    }

    public static void sendMsgToAllUsers(String message){
        if(CollectionUtils.isEmpty(sseClients)){
            return;
        }

       // 循环发送
        sseClients.forEach((userId,sseEmitter) -> {
            sendEmitterMessage(sseEmitter,userId,message,SSEMsgType.message);
        });
    }

    /**
     * 发送sse消息
     * @param sseEmitter
     * @param userId
     * @param message
     * @param msgType
     */
    public static void sendEmitterMessage(SseEmitter sseEmitter,
                                          String userId,
                                          String message,
                                          SSEMsgType msgType){
        try {
            SseEmitter.SseEventBuilder msgEvent = SseEmitter.event()
                    .id(userId)
                    .data(message)
                    .name(msgType.type)
                    .reconnectTime(3000);
            sseEmitter.send(msgEvent);
        } catch (IOException e) {
            log.error("SSE异常...{}",e.getMessage());
            remove(userId);


        }
    }

    public static Runnable timeoutCallback(String userId){
        return () -> {
            log.info("SSE超时...");
            // 移除用户链接
            remove(userId);
        };
    }

    public static Runnable completionCallback(String userId){
        return () -> {
            log.info("SSE完成...");
            // 移除用户链接
            remove(userId);
        };
    }

    public static Consumer<Throwable> errorCallback(String userId){
        return Throwable -> {
            log.info("SSE异常...");
            // 移除用户链接
            remove(userId);
        };
    }

    public static void remove(String userId){
        // 删除用户
        sseClients.remove(userId);
        log.info("SSE连接被移除，移除的用户ID为：{}",userId);
    }
}
