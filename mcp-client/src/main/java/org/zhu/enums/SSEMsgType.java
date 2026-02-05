package org.zhu.enums;

/**
 * 发送SSE的消息类型
 */
public enum SSEMsgType {
    message("message","单词发送的普通类型消息"),
    add("add", "消息追加, 适合于流失stream推送"),
    finish("finish", "消息完成"),
    custom_event("custom_event", "自定义事件"),
    done("done", "完成");

    public final String type;
    public final String value;

    SSEMsgType(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
