package one.yezii.tomon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TomonWsMessage {
    @JsonIgnore
    private final static ObjectMapper objectMapper = new ObjectMapper();
    public Integer op;
    public ObjectNode d;
    public String e;
    @JsonIgnore
    public TomonWsOpcode opEnum;

    public static TomonWsMessage of(TomonWsOpcode opEnum, ObjectNode objectNode) {
        TomonWsMessage tm = new TomonWsMessage();
        tm.op = opEnum.id;
        tm.d = objectNode;
        tm.opEnum = opEnum;
        return tm;
    }

    public static TomonWsMessage ofString(String s) {
        try {
            TomonWsMessage tm = objectMapper.readValue(s, TomonWsMessage.class);
            tm.opEnum = TomonWsOpcode.ofId(tm.op);
            return tm;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static TomonWsMessage identify(String token) {
        return of(TomonWsOpcode.IDENTIFY, objectMapper.createObjectNode().put("token", token));
    }

    public static TomonWsMessage heartbeat() {
        return of(TomonWsOpcode.HEARTBEAT, objectMapper.createObjectNode().put("ping", "are you ok?"));
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
