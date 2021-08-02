package by.ddrvld.countdowndeathapp;

public class AppSettings {
    public int intervalBetweenMessages;

    public AppSettings() {}
    public AppSettings(int intervalBetweenMessages) {
        this.intervalBetweenMessages = intervalBetweenMessages;
    }

    public int getIntervalBetweenMessages() {
//        return Integer.parseInt(mDatabaseReference.child("settings").child("intervalBetweenMessages").getKey());
        return intervalBetweenMessages;
    }
}
