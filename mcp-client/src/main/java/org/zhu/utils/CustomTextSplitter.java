package org.zhu.utils;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.List;

/**
 * 自定义文本分割器
 */
public class CustomTextSplitter extends TextSplitter {

    @Override
    protected List<String> splitText(String text) {
        return List.of(split(text));
    }

    public String[] split(String text){
        return text.split("\\s*\\R\\s*\\R\\s*");
    }
}
