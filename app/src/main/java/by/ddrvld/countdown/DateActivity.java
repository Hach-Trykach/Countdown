package by.ddrvld.countdown;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class DateActivity extends Activity {

    boolean end;

    static SharedPreferences settings;
    static final String APP_PREFERENCES = "settings";
    static final String RANDOMLIFETIME = "randomlifetime";

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

        if (settings.contains(RANDOMLIFETIME)) {
            randomLifeTime = settings.getLong(RANDOMLIFETIME, 0);
        } else {
            final String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            int android_id_int = 0;
            for (int i = 0; i < android_id.length(); ++i) {
                char ch = android_id.charAt(i);
                int n = (int) ch + (int) 'a' + 1;
                android_id_int += Integer.valueOf(n);
            }
            String hash = MD5(android_id);
            int hash_int = 0;
            for (int i = 0; i < hash.length(); ++i) {
                char ch = hash.charAt(i);
                int n = (int) ch + (int) 'a' + 1;
                hash_int += Integer.valueOf(n);
            }

            Calendar cal = Calendar.getInstance();
            int first = cal.get(Calendar.DAY_OF_MONTH);
            if(first >= 3) first = Math.round(first / 3);

//            first = 1;
//            System.out.println("FIRST: " + first);

            Long result;
            result = Long.parseLong(String.format("%d%d%d", first, hash_int, android_id_int));

            randomLifeTime = 1574338153L + result;
            while (randomLifeTime < currentTime) randomLifeTime += (android_id_int + hash_int);
            while (randomLifeTime > 3238171085L) randomLifeTime -= (android_id_int + hash_int);
        }

        Long timerTime = randomLifeTime - currentTime;

        years = timerTime / 31536000;
        days = timerTime / 86400 % 365;
        hours = timerTime / 3600 % 24;
        mins = timerTime / 60 % 60;
        secs = timerTime % 60;

        setValues();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-7528412641056592/4698457402");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public static String MD5(String s) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
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