package org.zhu.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zhu.bean.ChatEntity;
import org.zhu.service.ChatService;
import org.zhu.service.DocumentService;
import org.zhu.utils.Result;

import java.util.List;

@RestController
@RequestMapping("rag")
public class RagController {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private ChatService chatService;

    /**
     * 加载文档到redis的rag知识库
     * @param file
     * @return
     */
    @RequestMapping("/uploadRagDoc")
    public Result uploadRagDoc(@RequestParam("file")MultipartFile file) {
        // file.getOriginalFilename(); // 文件名
        // file.getResource(); // springboot获取文件
        List<Document> documentList = documentService.loadText(file.getResource(),file.getOriginalFilename());
        return Result.ok(documentList);
    }

    /**
     * 根据用户提问搜索知识库
     * @param question
     * @return
     */
    @GetMapping("/doSearch")
    public Result doSearch(@RequestParam("question") String question) {
        return Result.ok(documentService.doSearch(question));
    }

    /**
     * rag搜索，结合模型优化知识库内容输出
     * @return
     */
    @PostMapping("/search")
    public void search(@RequestBody ChatEntity chatEntity, HttpServletResponse response) {
        // 查询知识库
        List<Document> list = documentService.doSearch(chatEntity.getMessage());
        response.setCharacterEncoding("UTF-8");
        chatService.doChatRagSearch(chatEntity,list);
    }
}
