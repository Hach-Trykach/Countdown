 package by.ddrvld.countdowndeathapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Arrays;
import java.util.Calendar;

import static by.ddrvld.countdowndeathapp.MainActivity.date_of_death;

public class Update extends AppWidgetProvider {

    private static int currentWidgetId;
    private static RemoteViews remoteViews;
    static SharedPreferences settings;
    static final String APP_PREFERENCES = "settings";
    static final String DATE_OF_DEATH = "randomlifetime";
    private static long years, days, hours, mins, secs;
    private PendingIntent service = null;

    private static final String SYNC_CLICKED = "btcwidget_update_action";
    private static final String WAITING_MESSAGE = "Wait for BTC price";

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

//        DateFormat format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
//        CharSequence text = format.format(new Date());

        Intent intent = new Intent(context, Update.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);
        remoteViews.setOnClickPendingIntent(R.id.linear_layout, pendingIntent);

        long currentTime = System.currentTimeMillis() / 1000;
        if (settings.contains(DATE_OF_DEATH)) {
            date_of_death = settings.getLong(DATE_OF_DEATH, 0);
        }
        long timerTime = date_of_death - currentTime;

//        MainActivity mainActivity = new MainActivity();
//        mainActivity.countDateOfDeath();

        years = timerTime / 31536000;
        days = timerTime / 86400 % 365;
        hours = timerTime / 3600 % 24;
        mins = timerTime / 60 % 60;
        secs = timerTime % 60;

        updateUI();
        updateUI2(context);

        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
//        Toast.makeText(context, "Виджет обновлён", Toast.LENGTH_SHORT).show();
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

            remoteViews.setTextViewText(R.id.count, WAITING_MESSAGE);

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
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.setRepeating(AlarmManager.RTC,startTime.getTime().getTime(),1000,service);
    }

    private static void updateUI() {
        if (years >= 10) {
            remoteViews.setTextViewText(R.id.count, String.valueOf(years));
            return;
        } else if (years > 0) {
            remoteViews.setTextViewText(R.id.count, "0" + years);
            return;
        }

        if (days >= 10) {
            remoteViews.setTextViewText(R.id.count, String.valueOf(days));
            return;
        } else if (days > 0) {
            remoteViews.setTextViewText(R.id.count, "0" + days);
            return;
        }

        if (hours >= 10) {
            remoteViews.setTextViewText(R.id.count, String.valueOf(hours));
            return;
        } else if (hours > 0) {
            remoteViews.setTextViewText(R.id.count, "0" + hours);
            return;
        }

        if (mins >= 10) {
            remoteViews.setTextViewText(R.id.count, String.valueOf(mins));
            return;
        } else if (mins > 0) {
            remoteViews.setTextViewText(R.id.count, "0" + mins);
            return;
        }

        if (secs >= 10) {
            remoteViews.setTextViewText(R.id.count, String.valueOf(secs));
        } else if (secs > 0) {
            remoteViews.setTextViewText(R.id.count, "0" + secs);
        }

        if (years == 0 && days == 0 && hours == 0 && mins == 0 && secs == 0) {
            remoteViews.setTextViewText(R.id.count, "BOO");
            remoteViews.setTextViewTextSize(R.id.count, 2, 20);
//            remoteViews.setTextViewText(R.id.day, "O");
//            remoteViews.setTextViewText(R.id.hrs, "O");
//            remoteViews.setTextViewText(R.id.min, "O");
//            remoteViews.setTextViewText(R.id.sec, "O");
//
            remoteViews.setViewVisibility(R.id.text_count, View.GONE);
//            remoteViews.setViewVisibility(R.id.day, View.INVISIBLE);
//            remoteViews.setViewVisibility(R.id.hrs, View.INVISIBLE);
//            remoteViews.setViewVisibility(R.id.min, View.INVISIBLE);
//            remoteViews.setViewVisibility(R.id.sec, View.INVISIBLE);
        }
    }

    private static void updateUI2(Context context) {
        if(secs != 0) remoteViews.setTextViewText(R.id.text_count, GetWord(secs, context.getResources().getString(R.string.text_sec1), context.getResources().getString(R.string.text_sec2), context.getResources().getString(R.string.text_sec3)));
        if(mins != 0) remoteViews.setTextViewText(R.id.text_count, GetWord(mins, context.getResources().getString(R.string.text_min1), context.getResources().getString(R.string.text_min2), context.getResources().getString(R.string.text_min3)));
        if(hours != 0) remoteViews.setTextViewText(R.id.text_count, GetWord(hours, context.getResources().getString(R.string.text_hrs1), context.getResources().getString(R.string.text_hrs2), context.getResources().getString(R.string.text_hrs3)));
        if(days != 0) remoteViews.setTextViewText(R.id.text_count, GetWord(days, context.getResources().getString(R.string.text_day1), context.getResources().getString(R.string.text_day2), context.getResources().getString(R.string.text_day3)));
        if(years != 0) remoteViews.setTextViewText(R.id.text_count, GetWord(years, context.getResources().getString(R.string.text_yrs1), context.getResources().getString(R.string.text_yrs2), context.getResources().getString(R.string.text_yrs3)));

        if(years == 0) {
            remoteViews.setTextColor(R.id.count, context.getResources().getColor(R.color.red));
            remoteViews.setTextColor(R.id.text_count, context.getResources().getColor(R.color.red));
            if(days == 0) {
                remoteViews.setTextColor(R.id.count, context.getResources().getColor(R.color.red));
                remoteViews.setTextColor(R.id.text_count, context.getResources().getColor(R.color.red));
                if(hours == 0) {
                    remoteViews.setTextColor(R.id.count, context.getResources().getColor(R.color.red));
                    remoteViews.setTextColor(R.id.text_count, context.getResources().getColor(R.color.red));
                    if(mins == 0) {
                        remoteViews.setTextColor(R.id.count, context.getResources().getColor(R.color.red));
                        remoteViews.setTextColor(R.id.text_count, context.getResources().getColor(R.color.red));
                        if(secs == 0) {
                            remoteViews.setTextColor(R.id.count, context.getResources().getColor(R.color.red));
                            remoteViews.setTextColor(R.id.text_count, context.getResources().getColor(R.color.red));
                        } else {
                            remoteViews.setTextColor(R.id.count, context.getResources().getColor(R.color.white));
                            remoteViews.setTextColor(R.id.text_count, context.getResources().getColor(R.color.white));
                        }
                    } else {
                        remoteViews.setTextColor(R.id.count, context.getResources().getColor(R.color.white));
                        remoteViews.setTextColor(R.id.text_count, context.getResources().getColor(R.color.white));
                    }
                } else {
                    remoteViews.setTextColor(R.id.count, context.getResources().getColor(R.color.white));
                    remoteViews.setTextColor(R.id.text_count, context.getResources().getColor(R.color.white));
                }
            } else {
                remoteViews.setTextColor(R.id.count, context.getResources().getColor(R.color.white));
                remoteViews.setTextColor(R.id.text_count, context.getResources().getColor(R.color.white));
            }
        } else {
            remoteViews.setTextColor(R.id.count, context.getResources().getColor(R.color.white));
            remoteViews.setTextColor(R.id.text_count, context.getResources().getColor(R.color.white));
        }
    }

    public static String GetWord(Long value, String one, String before_five, String after_five)
    {
        value %= 100;
        if(10 < value && value < 20) return after_five;
        switch(Integer.parseInt(value.toString())%10)
        {
            case 1: return one;
            case 2:
            case 3:
            case 4: return before_five;
            default: return after_five;
        }
    }
}