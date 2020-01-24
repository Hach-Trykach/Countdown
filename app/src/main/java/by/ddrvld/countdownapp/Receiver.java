package by.ddrvld.countdownapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Receiver extends BroadcastReceiver {

    MediaPlayer mp;

    @Override
    public void onReceive(Context context, Intent intent) {
        mp = MediaPlayer.create(context, R.raw.krik);
        mp.start();

        MainActivity.PERIOD = 100;

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(context, "Countdown")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getResources().getString(R.string.user_agreement_broken))
                .setLights(0xff0000ff, 100, 100)
                .setVibrate(new long[] { 200, 3000})
                .setOngoing(true)
                .setTimeoutAfter(30000)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }
}