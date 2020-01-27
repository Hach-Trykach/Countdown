package by.ddrvld.countdownapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Receiver extends BroadcastReceiver {

//    MediaPlayer mp;

    @Override
    public void onReceive(Context context, Intent intent) {
//        mp = MediaPlayer.create(context, R.raw.krik);
//        mp.start();

//        SharedPreferences.Editor editor = settings.edit();
//        editor.putInt(IMEI_STRING, 100);
//        editor.apply();

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getApplicationContext().getPackageName() + "/" + R.raw.krik);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(context.getResources().getString(R.string.app_name))
            .setContentText(context.getResources().getString(R.string.user_agreement_broken))
            .setLights(0xff0000ff, 100, 100)
            .setVibrate(new long[] { 200, 3000})
            .setOngoing(true)
            .setTimeoutAfter(30000)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }
}