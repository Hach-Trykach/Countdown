package by.ddrvld.countdowndeathapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.TELEPHONY_SERVICE;

public class Update extends AppWidgetProvider {

    RemoteViews remoteViews;
    static SharedPreferences settings;
    static final String APP_PREFERENCES = "settings";
    static final String DATE_OF_DEATH = "randomlifetime";
    long timerTime;
    long years, days, hours, mins, secs;

    final String LOG_TAG = "myLogs";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(LOG_TAG, "onEnabled");
    }

//    @Override
//    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
//                         int[] appWidgetIds) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds));
//    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(LOG_TAG, "onDisabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int i=0; i<appWidgetIds.length; i++){
//            int currentWidgetId = appWidgetIds[i];

            settings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

            DateFormat format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
            CharSequence text = format.format(new Date());

            Intent intent = new Intent(context, Update.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);
            remoteViews.setOnClickPendingIntent(R.id.linearLayout, pendingIntent);

            long date_of_death = 0;
            long currentTime = System.currentTimeMillis() / 1000;
            if (settings.contains(DATE_OF_DEATH)) {
                date_of_death = settings.getLong(DATE_OF_DEATH, 0);
            }
            timerTime = date_of_death - currentTime;

            years = timerTime / 31536000;
            days = timerTime / 86400 % 365;
            hours = timerTime / 3600 % 24;
            mins = timerTime / 60 % 60;
            secs = timerTime % 60;

            updateUI(context);

            // Tell the widget manager
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
            Toast.makeText(context, "Виджет обновлён", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(Context context) {
        if (years >= 10) remoteViews.setTextViewText(R.id.yrs, String.valueOf(years));
        else remoteViews.setTextViewText(R.id.yrs, "0" + years);

        if (days >= 10) remoteViews.setTextViewText(R.id.day, String.valueOf(days));
        else remoteViews.setTextViewText(R.id.day, "0" + days);

        if (hours >= 10) remoteViews.setTextViewText(R.id.hrs, String.valueOf(hours));
        else remoteViews.setTextViewText(R.id.hrs, "0" + hours);

        if (mins >= 10) remoteViews.setTextViewText(R.id.min, String.valueOf(mins));
        else remoteViews.setTextViewText(R.id.min, "0" + mins);

        if (secs >= 10) remoteViews.setTextViewText(R.id.sec, String.valueOf(secs));
        else remoteViews.setTextViewText(R.id.sec, "0" + secs);

        if (years == 0 && days == 0 && hours == 0 && mins == 0 && secs == 0) {
            remoteViews.setTextViewText(R.id.yrs, "B");
            remoteViews.setTextViewText(R.id.yrs,"O");
            remoteViews.setTextViewText(R.id.yrs,"O");
            remoteViews.setTextViewText(R.id.yrs,"O");
            remoteViews.setTextViewText(R.id.yrs,"O");

//            textYrs.setVisibility(View.INVISIBLE);
//            textDay.setVisibility(View.INVISIBLE);
//            textHrs.setVisibility(View.INVISIBLE);
//            textMin.setVisibility(View.INVISIBLE);
//            textSec.setVisibility(View.INVISIBLE);
        }

//        textYrs.setText(GetWord(years, getResources().getString(R.string.text_yrs1), getResources().getString(R.string.text_yrs2), getResources().getString(R.string.text_yrs3)));
//        textDay.setText(GetWord(days, getResources().getString(R.string.text_day1), getResources().getString(R.string.text_day2), getResources().getString(R.string.text_day3)));
//        textHrs.setText(GetWord(hours, getResources().getString(R.string.text_hrs1), getResources().getString(R.string.text_hrs2), getResources().getString(R.string.text_hrs3)));
//        textMin.setText(GetWord(mins, getResources().getString(R.string.text_min1), getResources().getString(R.string.text_min2), getResources().getString(R.string.text_min3)));
//        textSec.setText(GetWord(secs, getResources().getString(R.string.text_sec1), getResources().getString(R.string.text_sec2), getResources().getString(R.string.text_sec3)));

        if(years == 0) {
            remoteViews.setTextColor(R.id.yrs, context.getResources().getColor(R.color.red));
//            textYrs.setTextColor(getResources().getColor(R.color.red));
            if(days == 0) {
                remoteViews.setTextColor(R.id.day, context.getResources().getColor(R.color.red));
//                textDay.setTextColor(getResources().getColor(R.color.red));
                if(hours == 0) {
                    remoteViews.setTextColor(R.id.hrs, context.getResources().getColor(R.color.red));
//                    textHrs.setTextColor(getResources().getColor(R.color.red));
                    if(mins == 0) {
                        remoteViews.setTextColor(R.id.min, context.getResources().getColor(R.color.red));
//                        textMin.setTextColor(getResources().getColor(R.color.red));
                        if(secs == 0) {
                            remoteViews.setTextColor(R.id.sec, context.getResources().getColor(R.color.red));
//                            textSec.setTextColor(getResources().getColor(R.color.red));
                        }
                    }
                }
            }
        }
    }
}