package one.yezii.tomon;

import java.util.Arrays;

public enum TomonWsOpcode {
    DISPATCH(0),            //接收	业务事件的分发
    HEARTBEAT(1),           //发送、接收	心跳包发起，即 ping
    IDENTIFY(2),            //发送、接收	鉴权，socket 建立后确定用户身份
    HELLO(3),               //接收	服务器发来的初始化信息
    HEARTBEAT_ACK(4),       //发送、接收	心跳包响应，即 pong
    VOICE_STATE_UPDATE(5);  //发送、接收	语音服务信令
    public int id;

    TomonWsOpcode(int id) {
        this.id = id;
    }

    public static TomonWsOpcode ofId(int id) {
        return Arrays.stream(TomonWsOpcode.values())
                .filter(e -> e.id == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("error SocketOpcode id"));
    }
}
