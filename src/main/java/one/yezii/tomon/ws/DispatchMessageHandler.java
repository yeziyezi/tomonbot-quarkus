package one.yezii.tomon.ws;

import one.yezii.tomon.common.DispatchEventEnum;
import one.yezii.tomon.function.drawcard.Operator;
import one.yezii.tomon.function.drawcard.Pool;
import one.yezii.tomon.function.drawcard.PoolContext;
import one.yezii.tomon.push.PushUtil;
import one.yezii.tomon.ws.dispatch.MessageCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                //不处理bot发言
                if (messageCreate.getAuthor().getIs_bot()) {
                    return;
                }
                String channelId = messageCreate.getChannel_id();
                String messageContent = messageCreate.getContent().trim();
                switch (messageContent) {
                    case "咕咕":
                    case "白咕咕":
                    case "Ptilopsis":
                        PushUtil.pushBaseMessage(channelId, "在呢");
                        return;
                    case "舟游抽卡":
                    case "方舟抽卡":
                    case "咕咕抽卡":
                        String poolListMessage = PoolContext.getPoolListMessage();
                        poolListMessage = "方舟抽卡Beta\n" + poolListMessage;
                        poolListMessage += "回复 卡池序号d次数 进行抽卡，如：1d50\n";
                        PushUtil.pushBaseMessage(channelId, poolListMessage);
                        return;
                }
                Matcher matcher = Pattern.compile("(\\d)d(\\d{1,20})").matcher(messageContent);
                if (matcher.find()) {
                    int poolNum = Integer.parseInt(matcher.group(1));
                    int drawTimes = Integer.parseInt(matcher.group(2));
                    if (poolNum < 0 || poolNum > PoolContext.getPools().size() + 1) {
                        PushUtil.pushBaseMessage(channelId, "没有找到卡池" + poolNum + "哦");
                        return;
                    }
                    if (drawTimes > 500 || drawTimes < 1) {
                        PushUtil.pushBaseMessage(channelId, "一次只可以抽1-500次哦");
                        return;
                    }
                    Pool pool = PoolContext.getPools().get(poolNum - 1);
                    StringBuilder sb = new StringBuilder("抽卡结果\n");
                    for (int i = 0; i < drawTimes; i++) {
                        Operator operator = pool.draw();
                        sb.append(operator.getName());
                        sb.append("★".repeat(Math.max(0, operator.getStar())));
                        sb.append("\n");
                    }
                    PushUtil.pushBaseMessage(channelId, sb.toString());
                }
        }
    }
}
