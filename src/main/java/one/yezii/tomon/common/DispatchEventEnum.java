package one.yezii.tomon.common;

import java.util.Arrays;

public enum DispatchEventEnum {
    GUILD_CREATE, //创建或加入群组
    GUILD_DELETE,//删除或离开群组
    GUILD_UPDATE,//群组更新
    GUILD_POSITION,//群组位置变化
    CHANNEL_CREATE,//新建频道
    CHANNEL_DELETE,//删除频道
    CHANNEL_UPDATE,//频道更新
    CHANNEL_POSITION,//频道位置变化
    GUILD_ROLE_CREATE,//新建群组角色
    GUILD_ROLE_DELETE,//删除群组角色
    GUILD_ROLE_UPDATE,//群组角色更新
    GUILD_ROLE_POSITION,//群组角色位置变化
    GUILD_MEMBER_ADD,//新增群组成员
    GUILD_MEMBER_REMOVE,//删除群组成员
    GUILD_MEMBER_UPDATE,//群组成员更新
    MESSAGE_CREATE,//新建消息
    MESSAGE_DELETE,//删除消息
    MESSAGE_UPDATE,//消息更新
    MESSAGE_REACTION_ADD,//增加消息反应
    MESSAGE_REACTION_REMOVE,//删除消息反应
    MESSAGE_REACTION_REMOVE_ALL,//删除所有消息反应
    EMOJI_CREATE,//新建群组 Emoji
    EMOJI_DELETE,//删除群组 Emoji
    EMOJI_UPDATE,//群组 Emoji 更新
    VOICE_STATE_UPDATE,//语音状态更新
    USER_TYPING,//打字状态
    USER_PRESENCE_UPDATE,//在线状态更新
    UNKNOWN//未知
    ;

    public static DispatchEventEnum ofString(String s) {
        return Arrays.stream(DispatchEventEnum.values())
                .filter(e -> e.toString().equals(s))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
