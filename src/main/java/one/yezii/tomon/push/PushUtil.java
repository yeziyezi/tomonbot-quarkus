package one.yezii.tomon.push;

import one.yezii.tomon.common.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

public class PushUtil {
    private final static Logger logger = LoggerFactory.getLogger(PushUtil.class);

    public static void pushBaseMessage(String channelId, String content) {
        String url = String.format(Constant.BASE_URL + "/channels/%s/messages", channelId);
        try {
            HttpUtil.doPost(url, BaseMessage.newMessage(content).toString());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("push message error", e);
        }
    }
}
