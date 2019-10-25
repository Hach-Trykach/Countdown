package by.ddrvld.countdown;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DateActivity extends Activity {

    boolean end;

    static SharedPreferences settings;
    static final String APP_PREFERENCES = "settings";
    static final String YEARS = "years";
    static final String DAYS = "days";
    static final String HOURS = "hours";
    static final String MINS = "mins";
    static final String SECS = "sces";

    Long randomLifeTime;
    Long currentTime = System.currentTimeMillis()/1000;

    int fullDays = 364, fullHours = 23, fullMins = 59, fullSecs = 59;

    int nowYears, nowDays, nowHours, nowMins, nowSecs;
    int lifeYears, lifeDays, lifeHours, lifeMins, lifeSecs;
    int years, days, hours, mins, secs;

    TextView tvYrs, tvDay, tvHrs, tvMin, tvSec;
    TextView textYrs, textDay, textHrs, textMin, textSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        tvYrs = findViewById(R.id.yrs);
        tvDay = findViewById(R.id.day);
        tvHrs = findViewById(R.id.hrs);
        tvMin = findViewById(R.id.min);
        tvSec = findViewById(R.id.sec);

        textYrs = findViewById(R.id.text_yrs);
        textDay = findViewById(R.id.text_day);
        textHrs = findViewById(R.id.text_hrs);
        textMin = findViewById(R.id.text_min);
        textSec = findViewById(R.id.text_sec);

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (settings.contains(YEARS) || settings.contains(DAYS) || settings.contains(HOURS) || settings.contains(MINS) || settings.contains(SECS)) {
            years = settings.getInt(YEARS, 0);// Получаем число из настроек
            days = settings.getInt(DAYS, 0);// Получаем число из настроек
            hours = settings.getInt(HOURS, 0);// Получаем число из настроек
            mins = settings.getInt(MINS, 0);// Получаем число из настроек
            secs = settings.getInt(SECS, 0);// Получаем число из настроек
        }
        else {
            randomLifeTime = 216000 + (long) (Math.random() * (1527999157L - 216000));

            nowYears = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date(currentTime * 1000L)));
            nowDays = Integer.parseInt(new SimpleDateFormat("ddd").format(new Date(currentTime * 1000L)));
            nowHours = Integer.parseInt(new SimpleDateFormat("hh").format(new Date(currentTime * 1000L)));
            nowMins = Integer.parseInt(new SimpleDateFormat("mm").format(new Date(currentTime * 1000L)));
            nowSecs = Integer.parseInt(new SimpleDateFormat("ss").format(new Date(currentTime * 1000L)));

            lifeYears = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date((currentTime + randomLifeTime) * 1000L)));
            lifeDays = Integer.parseInt(new SimpleDateFormat("ddd").format(new Date((currentTime + randomLifeTime) * 1000L)));
            lifeHours = Integer.parseInt(new SimpleDateFormat("hh").format(new Date((currentTime + randomLifeTime) * 1000L)));
            lifeMins = Integer.parseInt(new SimpleDateFormat("mm").format(new Date((currentTime + randomLifeTime) * 1000L)));
            lifeSecs = Integer.parseInt(new SimpleDateFormat("ss").format(new Date((currentTime + randomLifeTime) * 1000L)));

            years = lifeYears - nowYears;
            days = lifeDays - nowDays;
            hours = lifeHours - nowHours;
            mins = lifeMins - nowMins;
            secs = lifeSecs - nowSecs;
        }
        setValues();
    }

    public void theEnd() {
    }

    private void setValues() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if(secs > 0) secs--;
                else {
                    if(mins > 0) mins--;
                    else {
                        if(hours > 0) hours--;
                        else {
                            if (days > 0) days--;
                            else {
                                if (years > 0) years--;
                                else {
                                    end = true;
                                    return;
                                }
                                days = fullDays;
                            }
                            hours = fullHours;
                        }
                        mins = fullMins;
                    }
                    secs = fullSecs;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(years >= 10) tvYrs.setText("" + years);
                        else tvYrs.setText("0" + years);

                        if(days >= 10) tvDay.setText("" + days);
                        else tvDay.setText("0" + days);

                        if(hours >= 10) tvHrs.setText("" + hours);
                        else tvHrs.setText("0" + hours);

                        if(mins >= 10) tvMin.setText("" + mins);
                        else tvMin.setText("0" + mins);

                        if(secs >= 10) tvSec.setText("" + secs);
                        else tvSec.setText("0" + secs);

                        Log.d("days", "" + days);

                        if(end) theEnd();
                    }
                });
            }
        }, 0, 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(YEARS, years);
        editor.putInt(DAYS, days);
        editor.putInt(HOURS, hours);
        editor.putInt(MINS, mins);
        editor.putInt(SECS, secs);
        editor.apply();
    }
}