package by.ddrvld.countdowndeathapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Update extends AppWidgetProvider {

    private static int currentWidgetId;
    private static RemoteViews remoteViews;
    static SharedPreferences settings;
    static final String APP_PREFERENCES = "settings";
    static final String DATE_OF_DEATH = "randomlifetime";
    private static long timerTime;
    private static long years, days, hours, mins, secs;
    private PendingIntent service = null;

    private static final String SYNC_CLICKED    = "btcwidget_update_action";
    private static final String WAITING_MESSAGE = "Wait for BTC price";
    public static final int httpsDelayMs = 300;

    final String LOG_TAG = "myLogs";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(LOG_TAG, "onEnabled");
    }

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
        RemoteViews remoteViews;
        ComponentName watchWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);
        watchWidget = new ComponentName(context, Update.class);

        //при клике на виджет в систему отсылается вот такой интент, описание метода ниже
        remoteViews.setOnClickPendingIntent(R.id.linear_layout, getPendingSelfIntent(context, SYNC_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        //обновление всех экземпляров виджета
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    //этот метод выполняется, когда пора обновлять виджет
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)  {
        currentWidgetId = appWidgetId;
        settings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        DateFormat format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
        CharSequence text = format.format(new Date());

        Intent intent = new Intent(context, Update.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);
        remoteViews.setOnClickPendingIntent(R.id.linear_layout, pendingIntent);

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
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        Toast.makeText(context, "Виджет обновлён", Toast.LENGTH_SHORT).show();
    }

    //создание интента
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    //этот метод ловит интенты, срабатывает когда интент создан нажатием на виджет и
    //запускает обновление виджета
    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);
            watchWidget = new ComponentName(context, Update.class);

            remoteViews.setTextViewText(R.id.yrs, WAITING_MESSAGE);

            //updating widget
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }
        final AlarmManager manager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        final Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);

        final Intent i = new Intent(context, WidgetUpdateService.class);
        if (service == null)
        {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        manager.setRepeating(AlarmManager.RTC,startTime.getTime().getTime(),1000,service);
    }

    private static void updateUI(Context context) {
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