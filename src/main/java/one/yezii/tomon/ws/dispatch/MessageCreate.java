/**
 * Copyright 2020 bejson.com
 */
package one.yezii.tomon.ws.dispatch;

import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2020-09-08 22:18:16
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class MessageCreate {

    private List<String> attachments;
    private Author author;
    private String channel_id;
    private String content;
    private String edited_timestamp;
    private List<String> embeds;
    private Forward forward;
    private String guild_id;
    private String id;
    private Member member;
    private List<String> mentions;
    private String nonce;
    private boolean pinned;
    private List<String> reactions;
    private String reply;
    private List<String> stamps;
    private Date timestamp;
    private int type;

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEdited_timestamp() {
        return edited_timestamp;
    }

    public void setEdited_timestamp(String edited_timestamp) {
        this.edited_timestamp = edited_timestamp;
    }

    public List<String> getEmbeds() {
        return embeds;
    }

    public void setEmbeds(List<String> embeds) {
        this.embeds = embeds;
    }

    public Forward getForward() {
        return forward;
    }

    public void setForward(Forward forward) {
        this.forward = forward;
    }

    public String getGuild_id() {
        return guild_id;
    }

    public void setGuild_id(String guild_id) {
        this.guild_id = guild_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public boolean getPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public List<String> getReactions() {
        return reactions;
    }

    public void setReactions(List<String> reactions) {
        this.reactions = reactions;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public List<String> getStamps() {
        return stamps;
    }

    public void setStamps(List<String> stamps) {
        this.stamps = stamps;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}