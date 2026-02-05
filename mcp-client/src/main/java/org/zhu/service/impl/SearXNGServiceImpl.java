package org.zhu.service.impl;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zhu.bean.SearXNGResponse;
import org.zhu.bean.SearchResult;
import org.zhu.service.SearXNGService;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearXNGServiceImpl implements SearXNGService {

    @Value("${internet.websearch.searxng.url}")
    private String SEARXNG_URL;

    @Value("${internet.websearch.searxng.counts}")
    private Integer COUNTS;

    private final OkHttpClient okHttpClient;

    /**
     * searXNG搜索
     * @param query
     * @return
     */
    @Override
    public List<SearchResult> search(String query) {
        // 构建url
        HttpUrl url = HttpUrl.get(SEARXNG_URL).newBuilder()
                .addQueryParameter("q", query)
                .addQueryParameter("format", "json")
                // .addQueryParameter("count", COUNTS.toString())
                .build();
        log.info("搜索的url地址: {}", url.url());
        // okHttpClient.newCall()

        // 构建request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 发送请求
        try (Response response = okHttpClient.newCall(request).execute();) {
            // 判断请求是否成功还是失败
            if(!response.isSuccessful()) throw new  RuntimeException("请求失败: HTTP" + response.code());

            // 获得响应的数据
            if(response.body() != null){
                String responseBody = response.body().string();
                log.info("响应数据: {}", responseBody);
                SearXNGResponse searXNGResponse = JSONUtil.toBean(responseBody,SearXNGResponse.class);
                // return searXNGResponse.getResults();

                // 处理搜索结果，排序并限制数量后返回
                return dealResults(searXNGResponse.getResults());
            }
            log.error("搜索失败：{}",response.message());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Collections.emptyList();
    }

    /**
     * 处理结果集，截取限制个数
     * @param results
     * @return
     */
    private List<SearchResult> dealResults(List<SearchResult> results){
        return results.subList(0,Math.min(COUNTS,results.size()))
               .parallelStream()
               .sorted(Comparator.comparing(SearchResult::getScore).reversed())
               .limit(COUNTS)
                .toList();
    }
}
