package by.ddrvld.countdowndeathapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String uniqueKey;
    public Long dateOfDeath;
    public Boolean noAds = false;
    public String nameAndSurname;
    public String dateOfBirth;
    public String placeOfBirth;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uniqueKey, Long dateOfDeath, Boolean noAds, String nameAndSurname, String dateOfBirth, String placeOfBirth) {
        this.uniqueKey = uniqueKey;
        this.dateOfDeath = dateOfDeath;
        this.noAds = noAds;
        this.nameAndSurname = nameAndSurname;
        this.dateOfBirth = dateOfBirth;
        this.placeOfBirth = placeOfBirth;
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

    public String getNameAndSurname() {
        return nameAndSurname;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

}