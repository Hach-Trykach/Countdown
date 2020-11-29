package by.ddrvld.countdowndeathapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public Long imei;
    public Long dateOfDeath;
    public Boolean noAds = false;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(Long imei, Long dateOfDeath, Boolean noAds) {
        this.imei = imei;
        this.dateOfDeath = dateOfDeath;
        this.noAds = noAds;
    }

    public long getImei() {
        return imei;
    }

    public long getDateOfDeath() {
        return dateOfDeath;
    }

    public boolean getNoAds() {
        return noAds;
    }

}