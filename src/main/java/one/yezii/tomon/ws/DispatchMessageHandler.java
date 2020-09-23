package one.yezii.tomon.ws;

import one.yezii.tomon.common.DispatchEventEnum;
import one.yezii.tomon.function.drawcard.Operator;
import one.yezii.tomon.function.drawcard.Pool;
import one.yezii.tomon.function.drawcard.PoolContext;
import one.yezii.tomon.push.PushUtil;
import one.yezii.tomon.ws.dispatch.MessageCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                        poolListMessage += "输入 卡池序号d次数 进行抽卡，如：1d10\n";
                        PushUtil.pushBaseMessage(channelId, poolListMessage);
                        return;
                }
                //检测到抽卡
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
                    if (drawTimes <= 10) {
                        for (int i = 0; i < drawTimes; i++) {
                            Operator operator = pool.draw();
                            sb.append(getOperatorDisplayString(operator));
                            if (pool.isUp(operator)) {
                                sb.append("[UP]");
                            }
                            sb.append("\n");
                        }
                    } else {
                        //剩余抽卡次数
                        int drawTimesTemp = drawTimes;
                        int decadeDrawTimes = 0;

                        while (drawTimesTemp >= 10) {
                            Map<Integer, Integer> totalMap = new HashMap<>();
                            totalMap.put(3, 0);
                            totalMap.put(4, 0);
                            totalMap.put(5, 0);
                            totalMap.put(6, 0);
                            drawTimesTemp -= 10;
                            int fourStarCommon = 0;
                            int threeStarCommon = 0;
                            sb.append(String.format("第%d次十连", ++decadeDrawTimes)).append("\n");
                            List<Operator> upFourList = new ArrayList<>();
                            List<Operator> upFiveList = new ArrayList<>();
                            List<Operator> commonFiveList = new ArrayList<>();
                            List<Operator> commonSixList = new ArrayList<>();
                            List<Operator> upSixList = new ArrayList<>();
                            for (int i = 0; i < 10; i++) {
                                Operator operator = pool.draw();
                                totalMap.put(operator.getStar(), totalMap.get(operator.getStar()) + 1);
                                if (pool.isUp(operator)) {
                                    switch (operator.getStar()) {
                                        case 6:
                                            upSixList.add(operator);
                                            break;
                                        case 5:
                                            upFiveList.add(operator);
                                            break;
                                        case 4:
                                            upFourList.add(operator);
                                            break;
                                    }
                                    continue;
                                }
                                switch (operator.getStar()) {
                                    case 6:
                                        commonSixList.add(operator);
                                        break;
                                    case 5:
                                        commonFiveList.add(operator);
                                        break;
                                    case 4:
                                        fourStarCommon++;
                                        break;
                                    case 3:
                                        threeStarCommon++;
                                        break;
                                }
                            }
                            upSixList.stream().map(this::getOperatorDisplayString)
                                    .forEach(s -> sb.append(s).append("[UP]").append("\n"));
                            commonSixList.stream().map(this::getOperatorDisplayString)
                                    .forEach(s -> sb.append(s).append("\n"));
                            upFiveList.stream().map(this::getOperatorDisplayString)
                                    .forEach(s -> sb.append(s).append("[UP]").append("\n"));
                            commonFiveList.stream().map(this::getOperatorDisplayString)
                                    .forEach(s -> sb.append(s).append("\n"));
                            upFourList.stream().map(this::getOperatorDisplayString)
                                    .forEach(s -> sb.append(s).append("[UP]").append("\n"));
                            if (fourStarCommon != 0) {
                                sb.append("✩✩✩✩").append("x").append(fourStarCommon).append("\t");
                            }
                            if (threeStarCommon != 0) {
                                sb.append("✩✩✩").append("x").append(threeStarCommon).append("\n");
                            }
                            if (totalMap.get(3) == 10) {
                                sb.append("是被神选中的刀客塔呢");
                            }
                        }
                        while (drawTimesTemp > 0) {
                            drawTimesTemp--;
                            Operator operator = pool.draw();
                            sb.append(getOperatorDisplayString(operator));
                            if (pool.isUp(operator)) {
                                sb.append("[UP]");
                            }
                            sb.append("\n");
                        }
                    }
                    PushUtil.pushBaseMessage(channelId, sb.toString());
                }
        }
    }

    private String getOperatorDisplayString(Operator operator) {
        String star;
        switch (operator.getStar()) {
            case 6:
                star = "★";
                break;
            case 5:
                star = "✮";
                break;
            default:
                star = "✩";
        }
        return operator.getName()
                + " ".repeat(Math.max(0, 6 - operator.getName().length()))
                + (operator.getName().length() == 4 ? " " : "\t")
                + star.repeat(Math.max(0, operator.getStar()));
    }
}
