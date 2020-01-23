package by.ddrvld.countdownapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import com.getbase.floatingactionbutton.FloatingActionButton;
//import com.mikepenz.materialdrawer.Drawer;
//import com.mikepenz.materialdrawer.DrawerBuilder;
//import com.mikepenz.materialdrawer.MiniDrawer;
//import com.mikepenz.materialdrawer.interfaces.ICrossfader;
//import com.mikepenz.materialdrawer.model.DividerDrawerItem;
//import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
//import com.mikepenz.materialdrawer.model.SectionDrawerItem;
//import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    static SharedPreferences settings;
    static final String APP_PREFERENCES = "settings";
    private final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

    boolean end;
    static final String RANDOMLIFETIME = "randomlifetime";
    static final String LAST_RATING_DAY = "last_rating_day";
    private ImageView moreAppsBtn;

    int lastRatingDay;

    Long timerTime, randomLifeTime;
    Long currentTime = System.currentTimeMillis() / 1000;

    Long fullDays = 364L, fullHours = 23L, fullMins = 59L, fullSecs = 59L;
    Long years, days, hours, mins, secs;

    TextView tvYrs, tvDay, tvHrs, tvMin, tvSec;
    TextView textYrs, textDay, textHrs, textMin, textSec;

    private AdView mAdView;
//    private Drawer drawerResult;

    private PendingIntent pIntent1;
    private PendingIntent pIntent2;

    private int PERIOD = 1000;

    private final int BTN_COLOR_MATCH = 1;
    private final int BTN_JUMP_UP = 2;
    private final int BTN_CHRISTMAS_GAME = 3;
    private final int BTN_CHRISTMAS_TREE = 4;
    private final int BTN_BARLEY_BREAK = 5;

    private AlarmManager am;
    private NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_of_use);

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (settings.contains(RANDOMLIFETIME)) {
            onCreateActivityDate();
        }
        else setContentView(R.layout.terms_of_use);

        Button accept_and_continue_Btn = findViewById(R.id.accept_and_continue);
        if(accept_and_continue_Btn != null) {
            accept_and_continue_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogAcceptAndContinue();
                }
            });
        }
    }

    private void onCreateActivityDate() {
        setContentView(R.layout.activity_date);

        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + 4000, pIntent1);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 3000, 5000, pIntent2);



        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (settings.contains(LAST_RATING_DAY)) {
            lastRatingDay = settings.getInt(LAST_RATING_DAY, 0);

            Calendar cal = Calendar.getInstance();
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

            if(dayOfMonth != lastRatingDay) {
                TimerRating();
            }
        }
        else TimerRating();

////        AccountHeader accountHeader = initializeAccountHeader();
//        drawerResult = new DrawerBuilder()
//                .withActivity(this)
//                .withToolbar(toolbar)
//                .withRootView(R.id.drawer_layout)
//                .withSliderBackgroundColorRes(R.color.transparent)
////                .withGenerateMiniDrawer(true)
//                .withActionBarDrawerToggleAnimated(true)
//                .addDrawerItems(initializeDrawerItems())
////                .addStickyDrawerItems(initializeDrawerItems())
//                .withOnDrawerItemClickListener(onClicksLis)
////                .withAccountHeader(accountHeader)
//                .build();

//        Long lll = 352662060043475L;
//        final Long IMEI = Long.parseLong(lll.toString());
        final Long IMEI = Long.parseLong(getIMEI());

        MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.countdown);
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

        if (settings.contains(RANDOMLIFETIME)) {
            randomLifeTime = settings.getLong(RANDOMLIFETIME, 0);
            timerTime = randomLifeTime - currentTime;
        } else {
            String imeiString = "";
            for (int i = 8; i < 15; i++)
                imeiString += Long.toString(IMEI).charAt(i);

            if (Character.getNumericValue(Long.toString(IMEI).charAt(8)) <= 1)
                randomLifeTime = Long.parseLong(String.format("16%s0", imeiString));
            else randomLifeTime = Long.parseLong(String.format("3%s00", imeiString));

            if (randomLifeTime < currentTime + (3600 * 24) * 4)
                randomLifeTime = currentTime + ((3600L * 24) * 4) + 1111;
//            else if(randomLifeTime > currentTime + 1982459975L) randomLifeTime = currentTime + 1982459975L;

//            System.out.println("\nIMEI: " + IMEI);
//            System.out.println("\nIMEI String: " + imeiString);
//            System.out.println("\nrandomLifeTime: " + randomLifeTime);

            timerTime = randomLifeTime - currentTime;
//            FirebaseDatabase.getInstance().getReference().push().setValue(IMEI);
        }
//        FirebaseDatabase.getInstance().getReference().push().setValue(randomLifeTime);

        years = timerTime / 31536000;
        days = timerTime / 86400 % 365;
        hours = timerTime / 3600 % 24;
        mins = timerTime / 60 % 60;
        secs = timerTime % 60;

        setValues();

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("2915B28E56B33B9CC3D2C5D421E9FE3E")
                .addTestDevice("1D5297D5D4A3A977DCE0D970B2D4F83A")
                .build();
        mAdView.loadAd(adRequest);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        moreAppsBtn.setOnTouchListener(new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    view.setAlpha(0.5f);
//                    break;
//                case MotionEvent.ACTION_UP:
//                    view.setAlpha(1f);
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=DDRVLD+Lab."));
//                    startActivity(intent);
//                    break;
//                default:
//                    view.setAlpha(1f);
//                    break;
//            }
//            return true;
//        }
//    });

        final FloatingActionButton color_match = findViewById(R.id.color_match);
        color_match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.colormatch"));
                startActivity(intent);
            }
        });

        final FloatingActionButton jump_up = findViewById(R.id.jump_up);
        jump_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.jumpup"));
                startActivity(intent);
            }
        });

        final FloatingActionButton christmas_game = findViewById(R.id.christmas_game);
        christmas_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.christmasgame"));
                startActivity(intent);
            }
        });

        final FloatingActionButton christmas_tree = findViewById(R.id.christmas_tree);
        christmas_tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.christmastree"));
                startActivity(intent);
            }
        });

        final FloatingActionButton barley_break = findViewById(R.id.barley_break);
        barley_break.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.barleybreak"));
                startActivity(intent);
            }
        });
    }

//    private Drawer.OnDrawerItemClickListener onClicksLis = new Drawer.OnDrawerItemClickListener() {
//        @Override
//        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//            if(drawerItem.getIdentifier() == BTN_COLOR_MATCH)
//            {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.colormatch"));
//                startActivity(intent);
//                return true;
//            }
//            else if(drawerItem.getIdentifier() == BTN_JUMP_UP)
//            {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.jumpup"));
//                startActivity(intent);
//                return true;
//            }
//            else if(drawerItem.getIdentifier() == BTN_CHRISTMAS_GAME)
//            {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.christmasgame"));
//                startActivity(intent);
//                return true;
//            }
//            else if(drawerItem.getIdentifier() == BTN_CHRISTMAS_TREE)
//            {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.christmastree"));
//                startActivity(intent);
//                return true;
//            }
//            else if(drawerItem.getIdentifier() == BTN_BARLEY_BREAK)
//            {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.barleybreak"));
//                startActivity(intent);
//                return true;
//            }
//            return false;
//        }
//    };

//    private IDrawerItem[] initializeDrawerItems() {
//        return new IDrawerItem[] {
//
//            new SectionDrawerItem(),
//
//            new SecondaryDrawerItem()
////                    .withName(R.string.color_match)
////                    .withTextColorRes(R.color.white)
//                    .withIcon(R.drawable.img_color_match)
//                    .withIdentifier(BTN_COLOR_MATCH),
//            new DividerDrawerItem(),
//
//            new SecondaryDrawerItem()
////                    .withName(R.string.jump_up)
////                    .withTextColorRes(R.color.white)
//                    .withIcon(R.drawable.img_jump_up)
//                    .withIdentifier(BTN_JUMP_UP),
//                new DividerDrawerItem(),
//
//            new SecondaryDrawerItem()
////                    .withName(R.string.christmas_game)
////                    .withTextColorRes(R.color.white)
//                    .withIcon(R.drawable.img_christmas_game)
//                    .withIdentifier(BTN_CHRISTMAS_GAME),
//                new DividerDrawerItem(),
//
//            new SecondaryDrawerItem()
////                    .withName(R.string.christmas_tree)
////                    .withTextColorRes(R.color.white)
//                    .withIcon(R.drawable.img_christmas_tree)
//                    .withIdentifier(BTN_CHRISTMAS_TREE),
//                new DividerDrawerItem(),
//
//            new SecondaryDrawerItem()
////                    .withName(R.string.barley_break)
////                    .withTextColorRes(R.color.white)
//                    .withIcon(R.drawable.img_barley_break)
//                    .withIdentifier(BTN_BARLEY_BREAK)
//        };
//    }

//    private AccountHeader initializeAccountHeader() {
//        IProfile profile = new ProfileDrawerItem()
//            .withName(R.string.color_match)
////            .withEmail("dudarev.vlad@gmail.com")
//            .withIcon((getResources().getDrawable(R.drawable.img_color_match)));
//
//        return new AccountHeaderBuilder()
//            .withActivity(this)
//            .withHeaderBackground(R.color.grey)
//            .addProfiles(profile)
//            .build();
//    }

    private void DialogAcceptAndContinue() {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);

        Button dialogButtonYes = dialog.findViewById(R.id.dialogButtonYes);
        Button dialogButtonNo = dialog.findViewById(R.id.dialogButtonNo);

        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                setContentView(R.layout.activity_wait);

                createInterstitialAd();
            }
        });
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void DialogRating() {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rating_app);

        Button dialogButtonYes = dialog.findViewById(R.id.dialogButtonYes);
        Button dialogButtonNo = dialog.findViewById(R.id.dialogButtonNo);

        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.countdownapp"));
                startActivity(intent);
            }
        });
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

                Calendar cal = Calendar.getInstance();
                lastRatingDay = cal.get(Calendar.DAY_OF_MONTH);

                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(LAST_RATING_DAY, lastRatingDay);
                editor.apply();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void createInterstitialAd() { //Создаём межстраничное объявление
        final InterstitialAd interstitial;

        interstitial = new InterstitialAd(MainActivity.this);
        interstitial.setAdUnitId("ca-app-pub-7528412641056592/2188777318");
        AdRequest adRequesti = new AdRequest.Builder()
                .addTestDevice("2915B28E56B33B9CC3D2C5D421E9FE3E")
                .addTestDevice("1D5297D5D4A3A977DCE0D970B2D4F83A")
                .build();
        interstitial.loadAd(adRequesti);
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitial.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                PermissionRequest();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                PermissionRequest();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                PermissionRequest();
            }
        });
    }

    public void PermissionRequest() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ErrorReadPhoneStateDialog();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            Timer();
        }
    }

    public void ErrorReadPhoneStateDialog()
    {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.error_read_phone_state_dialog);

        TextView titleDialog = dialog.findViewById(R.id.titleDialog);

        titleDialog.setText(Html.fromHtml(getString(R.string.app_name)));

        TextView messageDialog = dialog.findViewById(R.id.messageDialog);
        messageDialog.setText(R.string.read_phone_state_text);

        Button dialogButtonYes = dialog.findViewById(R.id.dialogButtonYes);

        dialogButtonYes.setText(R.string.understand);

        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Timer();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    PermissionRequest();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void TimerRating() {
        final Thread logoTimer = new Thread() {
            int logoTimer = 0;
            public void run() {
                try {
                    while (logoTimer < 5000) { // 180000
                        sleep(1000);
                        logoTimer += 1000;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogRating();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        };
        logoTimer.start();
    }

    public void Timer() {
        final Thread logoTimer = new Thread() {
            int logoTimer = 0;
            public void run() {
                try {
                    while (logoTimer < 5000) {
                        sleep(100);
                        logoTimer += 1000;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onCreateActivityDate();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        };
        logoTimer.start();
    }

    public void theEnd() {
        sendInAppNotification();
    }

    public String getIMEI() {

        TelephonyManager mngr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String imei = mngr.getImei();
        return imei;
    }

    private String GetWord(Long value, String one, String before_five, String after_five)
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
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (PERIOD == 1000) {
                        if (secs > 0) secs--;
                        else {
                            if (mins > 0) mins--;
                            else {
                                if (hours > 0) hours--;
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
                    }
                    else {
                        if (years > 0) years--;
                        else {
                            if (days > 0) days--;
                            else {
//                                if (hours > 0) hours--;
//                                else {
//                                    if (mins > 0) mins--;
//                                    else {
//                                        if (secs > 0) secs--;
//                                        else {
//                                        }
//                                    }
//                                }
                                PERIOD = 1000;
                                sendInAppNotification();
                                timer.cancel();
                                setValues();
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                }
            }, 0, PERIOD);
        }

//    private void restartNotify() {
//        AlarmManager manager = (AlarmManager)getSystemService(
//                Context.ALARM_SERVICE);
//
//
//// На случай, если мы ранее запускали активити, а потом поменяли время,
//// откажемся от уведомления
//        am.cancel(pendingIntent);
//// Устанавливаем разовое напоминание
//        Date date = new Date();
//        long millis = date.getTime();
//        am.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
//    }

    private void updateUI() {
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

    private void sendInAppNotification() {
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.krik);
        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(MainActivity.this, getResources().getString(R.string.app_name))
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.user_agreement_broken))
                .setSound(soundUri)
                .setLights(0xff0000ff, 100, 100)
                .setVibrate(new long[] { 200, 3000})
                .setOngoing(true)
                .setTimeoutAfter(30000)
//                .setDefaults(Notification.DEFAULT_LIGHTS)
//                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(MainActivity.this);
        notificationManager.notify(0, builder.build());

//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("My channel description");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed() {
//        if(drawerResult != null && drawerResult.isDrawerOpen()) {
//            drawerResult.closeDrawer();
//        } else {
            super.onBackPressed();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = settings.edit();
        if(randomLifeTime != null)
            editor.putLong(RANDOMLIFETIME, randomLifeTime);
        editor.apply();
    }
}
//в Alarm Manager помещаем следующий PendingIntent
//PendingIntent.getBroadcast( context, 1, new Intent( context, AlarmBroadcastReceiver.class), 0);