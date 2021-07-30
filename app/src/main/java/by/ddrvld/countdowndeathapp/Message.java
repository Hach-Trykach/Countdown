package by.ddrvld.countdowndeathapp;

import java.util.Date;

import static by.ddrvld.countdowndeathapp.ChatActivity.mDatabaseReference;

public class Message {
    public String email;
    public  String userName;
    public String textMessage;
    public long messageTime;
    public String messageID;

    public Message() {}
    public Message(String email, String userName, String textMessage, String messageID) {
        this.email = email;
        this.userName = userName;
        this.textMessage = textMessage;
        this.messageTime = new Date().getTime();
        this.messageID = messageID;
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

    public String getMessageID() {
        return messageID;
    }
}

class AppSettings {
    public int intervalBetweenMessages;

    public void AppSettings() {}
    public void AppSettings(int intervalBetweenMessages) {
        this.intervalBetweenMessages = intervalBetweenMessages;
    }

    public int getIntervalBetweenMessages() {
        return Integer.parseInt(mDatabaseReference.child("settings").child("intervalBetweenMessages").getKey());
    }
}