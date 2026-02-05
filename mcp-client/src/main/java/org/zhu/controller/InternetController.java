package org.zhu.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zhu.bean.ChatEntity;
import org.zhu.service.ChatService;
import org.zhu.service.DocumentService;
import org.zhu.service.SearXNGService;
import org.zhu.utils.Result;

import java.util.List;

@RestController
@RequestMapping("internet")
public class InternetController {
    @Autowired
    private SearXNGService searXNGService;

    @Autowired
    private ChatService chatService;

    @GetMapping("/test")
    public Object test(@RequestParam("q") String query){
        return searXNGService.search(query);
    }

    /**
     * 结合大模型联网搜索
     * @param chatEntity
     * @param response
     * @return
     */
    @PostMapping("/search")
    public void search(@RequestBody ChatEntity chatEntity, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        chatService.doInternetSearch(chatEntity);
    }
}
