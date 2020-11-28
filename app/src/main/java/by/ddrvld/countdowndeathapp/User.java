package by.ddrvld.countdowndeathapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public Long imei;
    public Long dateOfDeath;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(Long imei, Long dateOfDeath) {
        this.imei = imei;
        this.dateOfDeath = dateOfDeath;
    }

}