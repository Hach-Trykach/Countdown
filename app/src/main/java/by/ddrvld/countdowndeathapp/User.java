package by.ddrvld.countdowndeathapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String uniqueKey;
    public Long dateOfDeath;
    public Boolean noAds = false;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uniqueKey, Long dateOfDeath, Boolean noAds) {
        this.uniqueKey = uniqueKey;
        this.dateOfDeath = dateOfDeath;
        this.noAds = noAds;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public long getDateOfDeath() {
        return dateOfDeath;
    }

    public boolean getNoAds() {
        return noAds;
    }

}