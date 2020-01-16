package by.ddrvld.countdownapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Timer;
import java.util.TimerTask;

public class DateActivity extends Activity {

    boolean end;

    static SharedPreferences settings;
    static final String APP_PREFERENCES = "settings";
    static final String RANDOMLIFETIME = "randomlifetime";
    private ImageView moreAppsBtn;

    Long randomLifeTime;
    Long currentTime = System.currentTimeMillis() / 1000;

    Long fullDays = 364L, fullHours = 23L, fullMins = 59L, fullSecs = 59L;
    Long years, days, hours, mins, secs;

    TextView tvYrs, tvDay, tvHrs, tvMin, tvSec;
    TextView textYrs, textDay, textHrs, textMin, textSec;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

//        Long lll = 352662060043475L;
//        final Long IMEI = Long.parseLong(lll.toString());
        final Long IMEI = Long.parseLong(getIMEI(getApplicationContext()));

        MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(DateActivity.this, R.raw.countdown);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
        mediaPlayer.start();

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

        moreAppsBtn = findViewById(R.id.more_apps_button);

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (settings.contains(RANDOMLIFETIME)) {
            randomLifeTime = settings.getLong(RANDOMLIFETIME, 0);
        } else {
            String imeiString = "";
            for(int i = 8; i < 15; i++)
                imeiString += Long.toString(IMEI).charAt(i);

            if(Character.getNumericValue(Long.toString(IMEI).charAt(8)) <= 1)
                randomLifeTime = Long.parseLong(String.format("16%s0", imeiString));
            else randomLifeTime = Long.parseLong(String.format("3%s00", imeiString));

            if(randomLifeTime < currentTime + (3600 * 24) * 4) randomLifeTime = currentTime + ((3600L * 24) * 4) + 1111;
//            else if(randomLifeTime > currentTime + 1982459975L) randomLifeTime = currentTime + 1982459975L;

//            System.out.println("\nIMEI: " + IMEI);
//            System.out.println("\nIMEI String: " + imeiString);
//            System.out.println("\nrandomLifeTime: " + randomLifeTime);
        }
        Long timerTime = randomLifeTime - currentTime;

        years = timerTime / 31536000;
        days = timerTime / 86400 % 365;
        hours = timerTime / 3600 % 24;
        mins = timerTime / 60 % 60;
        secs = timerTime % 60;

        setValues();

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("2915B28E56B33B9CC3D2C5D421E9FE3E")
                .build();
        mAdView.loadAd(adRequest);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        moreAppsBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch (View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setAlpha(1f);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=DDRVLD+Lab."));
                        startActivity(intent);
                        break;
                    default:
                        view.setAlpha(1f);
                        break;
                }
                return true;
            }
        });
    }

    public void theEnd() {
    }

    public String getIMEI(Context context){

        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;
    }

    public String GetWord(Long value, String one, String before_five, String after_five)
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

                        textYrs.setText(GetWord(years, getResources().getString(R.string.text_yrs1), getResources().getString(R.string.text_yrs2), getResources().getString(R.string.text_yrs3)));
                        textDay.setText(GetWord(days, getResources().getString(R.string.text_day1), getResources().getString(R.string.text_day2), getResources().getString(R.string.text_day3)));
                        textHrs.setText(GetWord(hours, getResources().getString(R.string.text_hrs1), getResources().getString(R.string.text_hrs2), getResources().getString(R.string.text_hrs3)));
                        textMin.setText(GetWord(mins, getResources().getString(R.string.text_min1), getResources().getString(R.string.text_min2), getResources().getString(R.string.text_min3)));
                        textSec.setText(GetWord(secs, getResources().getString(R.string.text_sec1), getResources().getString(R.string.text_sec2), getResources().getString(R.string.text_sec3)));

                        if(years == 0) {
                            tvYrs.setTextColor(getResources().getColor(R.color.red));
                            textYrs.setTextColor(getResources().getColor(R.color.red));
                            if(days == 0) {
                                tvDay.setTextColor(getResources().getColor(R.color.red));
                                textDay.setTextColor(getResources().getColor(R.color.red));
                                if(hours == 0) {
                                    tvHrs.setTextColor(getResources().getColor(R.color.red));
                                    textHrs.setTextColor(getResources().getColor(R.color.red));
                                    if(mins == 0) {
                                        tvMin.setTextColor(getResources().getColor(R.color.red));
                                        textMin.setTextColor(getResources().getColor(R.color.red));
                                        if(secs == 0) {
                                            tvSec.setTextColor(getResources().getColor(R.color.red));
                                            textSec.setTextColor(getResources().getColor(R.color.red));
                                        }
                                    }
                                }
                            }
                        }
                        if(end) theEnd();
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(RANDOMLIFETIME, randomLifeTime);
        editor.apply();
    }
}