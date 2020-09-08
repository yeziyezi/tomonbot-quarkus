package one.yezii.tomon.ws;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WsMessage {
    @JsonIgnore
    private final static ObjectMapper objectMapper = new ObjectMapper();
    public Integer op;
    public ObjectNode d;
    public String e;
    @JsonIgnore
    public WsOpcode opEnum;

    public static WsMessage of(WsOpcode opEnum, ObjectNode objectNode) {
        WsMessage tm = new WsMessage();
        tm.op = opEnum.id;
        tm.d = objectNode;
        tm.opEnum = opEnum;
        return tm;
    }

    public static WsMessage ofString(String s) {
        try {
            WsMessage tm = objectMapper.readValue(s, WsMessage.class);
            tm.opEnum = WsOpcode.ofId(tm.op);
            return tm;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static WsMessage identify(String token) {
        return of(WsOpcode.IDENTIFY, objectMapper.createObjectNode().put("token", token));
    }

    public static WsMessage heartbeat() {
        return of(WsOpcode.HEARTBEAT, objectMapper.createObjectNode().put("ping", "are you ok?"));
    }

    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
