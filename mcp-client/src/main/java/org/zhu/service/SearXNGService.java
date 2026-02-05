package org.zhu.service;

import org.zhu.bean.SearchResult;

import java.util.List;

public interface SearXNGService {

    /**
     * 调用本地搜索引擎searxng进行搜索
     * @param query
     * @return
     */
    public List<SearchResult> search(String query);
}
