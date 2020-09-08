package one.yezii.tomon.ws;

import one.yezii.tomon.common.DispatchEventEnum;
import one.yezii.tomon.push.PushUtil;
import one.yezii.tomon.ws.dispatch.MessageCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static one.yezii.tomon.common.CommonBean.objectMapper;

public class DispatchMessageHandler {

    private final Logger logger = LoggerFactory.getLogger(DispatchMessageHandler.class);

    public void handle(WsSessionContext context) {
        if (context.getMessage().isEmpty()) {
            return;
        }
        WsMessage message = context.getMessage().get();
        switch (DispatchEventEnum.ofString(message.e)) {
            case MESSAGE_CREATE:
                MessageCreate messageCreate = objectMapper.convertValue(message.d, MessageCreate.class);
                String channelId = messageCreate.getChannel_id();
                switch (messageCreate.getContent().trim()) {
                    case "咕咕":
                    case "白咕咕":
                    case "Ptilopsis":
                        PushUtil.pushBaseMessage(channelId, "在呢");
                        break;
                    case "十连":
                        PushUtil.pushBaseMessage(channelId, "开发中");
                        break;
                    default:
                        break;
                }
                return;
            default:
                return;
        }
    }
}
