package by.ddrvld.countdowndeathapp;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

public class Message implements IMessage {
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

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public IUser getUser() {
        return null;
    }

    @Override
    public Date getCreatedAt() {
        return null;
    }
}