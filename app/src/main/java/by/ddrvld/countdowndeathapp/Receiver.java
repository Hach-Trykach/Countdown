package by.ddrvld.countdowndeathapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Receiver extends BroadcastReceiver {

    SoundPool soundPool;
    int krik;

    @Override
    public void onReceive(Context context, Intent intent) {

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
//                .setAudioAttributes(attributes)
                .build();
        krik = soundPool.load(context, R.raw.krik, 2);

        playSound(krik);

//        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getApplicationContext().getPackageName() + "/" + R.raw.krik);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(context.getResources().getString(R.string.app_name))
            .setContentText(context.getResources().getString(R.string.user_agreement_broken))
            .setLights(0xff0000ff, 100, 100)
            .setVibrate(new long[] { 200, 3000})
            .setOngoing(true)
            .setTimeoutAfter(30000)
//            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }

    public void playSound(int soundID) {
//        soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) -> soundPool.play(sampleId, 1.0f, 1.0f, 0, 0, 1.0f));
        soundPool.play(soundID, 1.0f, 1.0f, 0, 0, 1.0f);
    }
}