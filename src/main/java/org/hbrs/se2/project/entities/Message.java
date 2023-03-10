package org.hbrs.se2.project.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name="message", schema="mid9db")
public class Message {

    private int messageid;
    private String content;
    private Instant timestamp;
    private int conversationid;
    private boolean read;
    private Integer userid;
    private Conversation conversation;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="messageid")
    public int getMessageid() {
        return messageid;
    }

    public void setMessageid(int messageid) {
        this.messageid = messageid;
    }

    @Basic
    @Column(name="content", columnDefinition="TEXT")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Basic
    @Column(name="timestamp")
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    @Column(name="conversationid")
    public int getConversationid() {
        return conversationid;
    }

    public void setConversationid(int conversationid) {
        this.conversationid = conversationid;
    }

    @Basic
    @Column(name="read")
    public boolean getRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Basic
    @Column(name="userid")
    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversationid", insertable = false, updatable = false)
    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

}
