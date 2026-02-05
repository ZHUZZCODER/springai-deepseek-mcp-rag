package org.zhu.service.impl;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zhu.bean.ChatEntity;
import org.zhu.bean.ChatResponseEntity;
import org.zhu.bean.SearchResult;
import org.zhu.enums.SSEMsgType;
import org.zhu.service.ChatService;
import org.zhu.service.SearXNGService;
import org.zhu.utils.SSEServer;
import reactor.core.publisher.Flux;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private ChatClient chatClient;

    @Autowired
    private SearXNGService searXNGService;

    // 聊天记忆功能
    private ChatMemory chatMemory;

    private String systemPrompt = "你是一个非常聪明的人工智能助手，可以帮我解决很多问题，我为你取一个名字，你的名字叫'LaBuBu'";

    /**
     * 提示词的三大类型
     * 1. 系统提示（System）
     * 2. 用户提示（User）
     *  3. 助手提示（Assistant）
     */

    /**
     * 构造函数，传入 ChatClient.Builder 对象
     *
     * @param chatClientBuilder ChatClient.Builder 对象
     *                           // 构造器注入，自动配置方式(推荐)  MCP
     */
    // public ChatServiceImpl(ChatClient.Builder chatClientBuilder) {
    //  this.chatClient = chatClientBuilder.defaultSystem(systemPrompt).build();
    // public ChatServiceImpl(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools) {// ToolCallbackProvider mcpTools集成mcp
    public ChatServiceImpl(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools,ChatMemory chatMemory) {// ToolCallbackProvider mcpTools集成mcp

        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(mcpTools) // 集成mcpTools
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()) // 开启聊天记忆
                // .defaultSystem(systemPrompt)
                // .defaultSystem("你是一个MCP小助手，可根据需求调用不同的MCP工具，优化回答效果")
                .build();

    }

    @Override
    public String chatTest(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Override
    public Flux<ChatResponse> streamResponse(String prompt) {
        return chatClient.prompt(prompt)
                .stream()
                .chatResponse();
    }

    @Override
    public Flux<String> streamStr(String prompt) {
        return chatClient.prompt(prompt)
                .stream()
                .content();
    }

    @Override
    public void doChat(ChatEntity chatEntity) {
        String userId = chatEntity.getCurrentUserName();
        String prompt = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();
        Flux<String> stringFlux = chatClient.prompt(prompt)
                .stream()
                .content();
        List<String> list = stringFlux.toStream().map(chatResponse ->{
          String content = chatResponse.toString();
          SSEServer.sendMsg(userId,content, SSEMsgType.add);
          log.info("content: {}",content);
          return content;
        }).collect(Collectors.toList());

        // 拼接历史消息
        String fullContent = list.stream().collect(Collectors.joining());
        // 封装成传递给前端格式
        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(fullContent,botMsgId);
        // 发送消息
        SSEServer.sendMsg(userId, JSONUtil.toJsonStr(chatResponseEntity), SSEMsgType.finish);
    }



    // Dify 智能体引擎构建平台
    // RAG提示词模板，用于基于知识库内容回答问题
    private static final String RAG_PROMPT = """
            基于上下文的知识库内容回答问题：
            【上下文】
            {context}
            
            【问题】
            {question}
            
            【输出】
            如果没有查到，请回复：不知道
            如果查到，请回复具体的内容。不相关的近似内容不必提到。
            """;
    @Override
    public void doChatRagSearch(ChatEntity chatEntity, List<Document> ragContext) {
        String userId = chatEntity.getCurrentUserName();
        String question = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();

        // ***把rag搜索出来的东西和问题给大模型

        // 构建提示词
        String context = null;
        if(ragContext != null && ragContext.size() > 0){
            context = ragContext.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n"));
        }

        // 组装提示词
        Prompt prompt = new Prompt(
                RAG_PROMPT.replace("{context}",context)
                        .replace("{question}",question));
        System.out.println("prompt: "+ prompt.toString());

        Flux<String> stringFlux = chatClient.prompt(prompt)
                .stream()
                .content();
        List<String> list = stringFlux.toStream().map(chatResponse ->{
            String content = chatResponse.toString();
            SSEServer.sendMsg(userId,content, SSEMsgType.add);
            log.info("content: {}",content);
            return content;
        }).collect(Collectors.toList());

        // 拼接历史消息
        String fullContent = list.stream().collect(Collectors.joining());
        // 封装成传递给前端格式
        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(fullContent,botMsgId);
        // 发送消息
        SSEServer.sendMsg(userId, JSONUtil.toJsonStr(chatResponseEntity), SSEMsgType.finish);
    }

    // 定义SearXNG搜索的提示词模板，用于构建基于互联网搜索结果的回答
    private static final String SEARXNG_PROMPT = """
            你是一个互联网搜索大师，请基于以下互联网返回的结果作为上下文，根据你的理解结合用户的提问综合后，生成并且输出专业的回答：
            【上下文】
            {context}
            
            【问题】
            {question}
            
            【输出】
            如果没有查到，请回复：不知道
            如果查到，请回复具体的内容。
            """;

    /**
     * 基于searXNG的实时联网搜索
     * @param chatEntity
     */
    @Override
    public void doInternetSearch(ChatEntity chatEntity) {
        String userId = chatEntity.getCurrentUserName();
        String question = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();

        List<SearchResult>  searchResults = searXNGService.search(question);
        String finalPrompt = buildSearXngPrompt(question,searchResults);
        Flux<String> stringFlux = chatClient.prompt(finalPrompt)
                .stream()
                .content();
        List<String> list = stringFlux.toStream().map(chatResponse ->{
            String content = chatResponse.toString();
            SSEServer.sendMsg(userId,content, SSEMsgType.add);
            log.info("content: {}",content);
            return content;
        }).collect(Collectors.toList());

        // 拼接历史消息
        String fullContent = list.stream().collect(Collectors.joining());
        // 封装成传递给前端格式
        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(fullContent,botMsgId);
        // 发送消息
        SSEServer.sendMsg(userId, JSONUtil.toJsonStr(chatResponseEntity), SSEMsgType.finish);
    }

    // 构建searXNG的提示词
    private static String buildSearXngPrompt(String question, List<SearchResult> searchResults){
        StringBuilder context = new StringBuilder();
        searchResults.forEach(searchResult -> {
            context.append(String.format("<context>\n[来源] %s \n [摘要] %s \n </context>\n",
                    searchResult.getUrl(),
                    searchResult.getContent()));
        });

        return SEARXNG_PROMPT
                .replace("{context}",context)
                .replace("{question}",question);
    }
}
