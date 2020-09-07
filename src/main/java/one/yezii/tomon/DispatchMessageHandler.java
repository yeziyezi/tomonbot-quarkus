package one.yezii.tomon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class DispatchMessageHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(DispatchMessageHandler.class);

    public void handle(WsSessionContext context) {
        if (context.getMessage().isEmpty()) {
            return;
        }
        WsMessage message = context.getMessage().get();
        switch (DispatchEventEnum.ofString(message.e)) {
            case MESSAGE_CREATE:
                ObjectNode objectNode = message.d;
                if (objectNode.get("content").asText().trim().equals("咕咕")) {
                    //用HTTP方式推消息
                    String channelId = objectNode.get("channel_id").asText();
                    String url = String.format(Constant.BASE + "/channels/%s/messages", channelId);
                    try {
                        HttpResponse<String> response = HttpUtil
                                .doPost(url, objectMapper.createObjectNode().put("content", "在呢")
                                        .put("nonce", String.valueOf(SnowflakeGenerator.next())).toString());
                        logger.info("response:" + response.body());
                    } catch (IOException | InterruptedException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
                return;
            default:
                return;
        }
    }
}
