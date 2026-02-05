package org.zhu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.zhu.service.DocumentService;
import org.zhu.utils.CustomTextSplitter;

import java.util.List;

@Service
@RequiredArgsConstructor // 只是生成构造器
public class DocumentServiceImpl implements DocumentService {

    private final RedisVectorStore redisVectorStore;

    /**
     * 加载文档并且读取数据进行保存到知识库
     * @param resource
     * @param fileName
     */
    @Override
    public List<Document> loadText(Resource resource, String fileName) {
        // 加载读取文档
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("fileName", fileName);
        List<Document> documentList = textReader.get();

        // System.out.println("documentList: "+documentList);

        // 切分文档
        // 默认文本切分器
        // TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        // List<Document> list = tokenTextSplitter.apply(documentList);

        // 自定义文本切分器
        CustomTextSplitter customTextSplitter = new CustomTextSplitter();
        List<Document> list = customTextSplitter.apply(documentList);

        System.out.println("list: "+list);

        // 向量存储
        redisVectorStore.add(list);

        return documentList;
    }

    /**
     * 根据用户提问搜索知识库
     * @param doSearch
     * @return
     */
    @Override
    public List<Document> doSearch(String question) {
        return redisVectorStore.similaritySearch(question);
    }
}
