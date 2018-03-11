package com.google.ar.core.examples.java.helloar.model;


import java.util.Date;

public class JournalMessage {
    private String messageText;
    private long messageTime;

    public JournalMessage(String messageText) {
        this.messageText = messageText;
        this.messageTime = new Date().getTime();
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
