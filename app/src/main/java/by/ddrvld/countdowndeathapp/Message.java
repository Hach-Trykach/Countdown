package by.ddrvld.countdowndeathapp;

import java.util.Date;

public class Message {
    public String email;
    public  String userName;
    public String textMessage;
    private long messageTime;

    public Message() {}
    public Message(String email, String userName, String textMessage) {
        this.email = email;
        this.userName = userName;
        this.textMessage = textMessage;
        this.messageTime = new Date().getTime();
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}