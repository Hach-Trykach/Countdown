package by.ddrvld.countdowndeathapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    SoundPool soundPool;
    int krik;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
//                .setAudioAttributes(attributes)
                .build();
        krik = soundPool.load(this, R.raw.krik, 2);

        playSound(krik);

        // Create Notification
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

//        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.krik);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_launcher)
//                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.user_agreement_broken))
//                .setSound(soundUri)
                .setLights(0xff0000ff, 100, 100)
                .setVibrate(new long[] { 200, 3000})
                .setOngoing(true)
                .setTimeoutAfter(30000)

                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public void playSound(int soundID) {
//        soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) -> soundPool.play(sampleId, 1.0f, 1.0f, 0, 0, 1.0f));
        soundPool.play(soundID, 1.0f, 1.0f, 0, 0, 1.0f);
    }
}
