package org.zhu.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.zhu.bean.ChatEntity;
import reactor.core.publisher.Flux;


import java.util.List;

public interface ChatService {

    /**
     * 聊天大模型测试
     * @param prompt
     * @return
     */
    public String chatTest(String prompt);

    /**
     * 聊天大模型流式输出测试
     * @param prompt
     * @return
     */
    public Flux<ChatResponse> streamResponse(String prompt);


    /**
     * 聊天大模型流式输出string测试
     * @param prompt
     * @return
     */
    public Flux<String> streamStr(String prompt);


    /**
     * 和大模型交互
     * @param chatEntity
     * @return
     */
    public void doChat(ChatEntity chatEntity);

    /**
     * Rag知识库检索汇总给大模型输出
     * @param chatEntity
     * @param ragContext
     * @return
     */
    public void doChatRagSearch(ChatEntity chatEntity, List<Document> ragContext);


    /**
     * 基于searXNG的实时联网搜索
     * @param chatEntity
     */
    public void doInternetSearch(ChatEntity chatEntity);

}
