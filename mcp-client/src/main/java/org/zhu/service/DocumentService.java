package org.zhu.service;


import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DocumentService {

    /**
     * 加载文档并且读取数据进行保存到知识库
     * @param resource
     * @param fileName
     */
    public List<Document> loadText(Resource resource, String fileName);

    /**
     * 根据用户提问搜索知识库(相似)
     * @param doSearch
     * @return
     */
    public List<Document> doSearch(String question);
}
