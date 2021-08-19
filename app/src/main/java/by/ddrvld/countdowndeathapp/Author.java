package by.ddrvld.countdowndeathapp;

import com.stfalcon.chatkit.commons.models.IUser;

public class Author implements IUser {

    /*...*/
    String id = "ID";
    String name = "NAME";
    String avatar = "AVATAR";

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}
