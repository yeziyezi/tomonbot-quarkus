package one.yezii.tomon.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import one.yezii.tomon.common.CommonBean;
import one.yezii.tomon.common.SnowflakeGenerator;

public class BaseMessage {
    private String content;
    private String nonce;

    public static BaseMessage newMessage(String content) {
        BaseMessage baseMessage = new BaseMessage();
        baseMessage.content = content;
        baseMessage.nonce = SnowflakeGenerator.nextString();
        return baseMessage;
    }

    public String getContent() {
        return content;
    }

    public String getNonce() {
        return nonce;
    }

    @Override
    public String toString() {
        try {
            return CommonBean.objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
