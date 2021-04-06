package by.ddrvld.countdowndeathapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

import static by.ddrvld.countdowndeathapp.Update.GetWord;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler,TextSwitcher.ViewFactory /*implements IUnityAdsListener*/ {
    static SharedPreferences settings;
    static final String APP_PREFERENCES = "settings";

    static final String DATE_OF_DEATH = "randomlifetime";
    static final String PERIOD_SETTINGS = "period";
    static final String LAST_RATING_DAY = "last_rating_day";
    static final String NAME_AND_SURNAME = "name_and_surname";
    static final String DATE_OF_BIRTH = "date_of_birth";
    static final String PLACE_OF_BIRTH = "place_of_birth";
//    static final String ADS_STATUS_FOR_SOON_DYING = "AdsStatusForSoonDying";
    static final String NO_ADS = "no_ads";
    static Boolean noAds = false;
    static String nameAndSurname;
    static String dateOfBirth;
    static String placeOfBirth;

    int lastRatingDay;

    public static long timerTime, date_of_death;
    long currentTime = System.currentTimeMillis() / 1000;

    static long years, days, hours, mins, secs;

    TextSwitcher tvYrs, tvDay, tvHrs, tvMin, tvSec;
    TextView textYrs, textDay, textHrs, textMin, textSec;

    private Drawer drawerResult;

    public static int PERIOD;

//    private boolean AdsStatusForSoonDying = false;

    private final int BTN_COLOR_MATCH = 1;
    private final int BTN_JUMP_UP = 2;
    private final int BTN_CHRISTMAS_GAME = 3;
    private final int BTN_CHRISTMAS_TREE = 4;
    private final int BTN_BARLEY_BREAK = 5;
    private final int BTN_SHOPPING_CALCULATOR = 6;
    private final int BTN_OUR_APPS = 7;
    private final int BTN_SHARE = 8;
    private final int BTN_CHANGE_UR_FATE = 9;
    private final int BTN_DISABLE_ADS = 10;

    private Toolbar toolbar;

    private WebView webView;

    //    private FakeReviewManager manager;
    private ReviewManager manager;
    private ReviewInfo reviewInfo;

    BillingProcessor mBillingProcessor;
    private final static String CHANGE_YOUR_FATE = "change_your_fate";
    private final static String DISABLE_ADS = "disable_ads";
    private final static String GPLAY_LICENSE = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlh3IXfvwhrH43ZO3anu7x7mbf3oT9JqAOD+3bTKocpYtvBexKwCiKhv9CrhAkZNaY48sfM80PtnVFAlqljPAcj9UthtHR94YCOSWL/F1SJB8FWxGa94d/JHc4ivuOLw0aNkoh6EdJX+0MH61FFI444bwmMYSKEjZLCkVcoddxq0CMdFcZTb3j4UsWhpgf2OMDLvEPn+qKqYVtrdKnoMd/vK9RTcC6iHvNNssAtBbQUEiA2SPA45shVgxK/jfxshNt96/jzhQUyvGiwYgwOVWrd6gXqkj5oiafzDGZkc6QTknMU2fYovy5FI1h8rKj3PfXaxR1sG5l+CavTj2s1F+QwIDAQAB";

    FirebaseAnalytics firebaseAnalytics;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myReference;
    int clicks_on_date = 0;

    SoundPool soundPool;
    int countdown;
    int krik;

    private TextView textViewBirthday;
    private TextInputEditText textInptEdtTxtName, textInptEdtTxtPlaceOfBirth;
    private DatePickerDialog.OnDateSetListener dateSetListener;

//    public MainActivity() {
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.black, getTheme()));

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myReference = firebaseDatabase.getReference();

//        AudioAttributes attributes = new AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
//                .setAudioAttributes(attributes)
                .build();
        countdown = soundPool.load(this, R.raw.countdown, 1);
        krik = soundPool.load(this, R.raw.krik, 2);

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (hasConnection(getApplicationContext())) {
            readFromDatabase();
            showMsg("hasConnection true");
        } else {
            if(settings.contains(DATE_OF_DEATH)) {
                onCreateActivityDate();
            } else {
                EnterInformationDialog();
            }
            showMsg("hasConnection false");
        }
    }

    private void onCreateActivityTermOfUse() {
        setContentView(R.layout.terms_of_use);

        Button accept_and_continue_Btn = findViewById(R.id.accept_and_continue);
        if (accept_and_continue_Btn != null) {
            accept_and_continue_Btn.setOnClickListener(view -> DialogAcceptAndContinue());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onCreateActivityDate() {
        setContentView(R.layout.activity_date);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        }

        if (settings.contains(LAST_RATING_DAY)) {
            lastRatingDay = settings.getInt(LAST_RATING_DAY, 0);

            Calendar cal = Calendar.getInstance();
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

            if (dayOfMonth != lastRatingDay && lastRatingDay <= 31) {
                TimerRating();
            }
        } else TimerRating();
        if (settings.contains(PERIOD_SETTINGS)) {
            PERIOD = settings.getInt(PERIOD_SETTINGS, 0);
        } else PERIOD = 1000; // 1000

        tvYrs = findViewById(R.id.yrs);
        tvYrs.setFactory(this);
        tvDay = findViewById(R.id.day);
        tvDay.setFactory(this);
        tvHrs = findViewById(R.id.hrs);
        tvHrs.setFactory(this);
        tvMin = findViewById(R.id.min);
        tvMin.setFactory(this);
        tvSec = findViewById(R.id.sec);
        tvSec.setFactory(this);

        Animation inAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        Animation resize = AnimationUtils.loadAnimation(this, R.anim.resize);

        tvYrs.setInAnimation(inAnimation);
        tvYrs.setOutAnimation(outAnimation);
        tvDay.setInAnimation(inAnimation);
        tvDay.setOutAnimation(outAnimation);
        tvHrs.setInAnimation(inAnimation);
        tvHrs.setOutAnimation(outAnimation);
        tvMin.setInAnimation(inAnimation);
        tvMin.setOutAnimation(outAnimation);
        tvSec.setInAnimation(inAnimation);
        tvSec.setOutAnimation(outAnimation);

        textYrs = findViewById(R.id.text_yrs);
        textDay = findViewById(R.id.text_day);
        textHrs = findViewById(R.id.text_hrs);
        textMin = findViewById(R.id.text_min);
        textSec = findViewById(R.id.text_sec);

        webView = findViewById(R.id.oneMinBanner);

        if (settings.contains(NO_ADS)) {
            noAds = settings.getBoolean(NO_ADS, false);
        }
        if (noAds)
            noAds(true);
        else
            adsInitialization();

        tvYrs.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                tvYrs.setTextSize(90);
                tvYrs.startAnimation(resize);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
//                tvYrs.setTextSize(88);
                clicksOnDate();
            }
            return true;
        });

        tvDay.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                tvDay.setTextSize(90);
                tvDay.startAnimation(resize);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
//                tvDay.setTextSize(88);
                clicksOnDate();
            }
            return true;
        });

        tvHrs.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                tvHrs.setTextSize(90);
                tvHrs.startAnimation(resize);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
//                tvHrs.setTextSize(88);
                clicksOnDate();
            }
            return true;
        });

        tvMin.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                tvMin.setTextSize(90);
                tvMin.startAnimation(resize);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
//                tvMin.setTextSize(88);
                clicksOnDate();
            }
            return true;
        });

        tvSec.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                tvSec.setTextSize(90);
                tvSec.startAnimation(resize);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
//                tvSec.setTextSize(88);
                clicksOnDate();
            }
            return true;
        });

//        UnityAds.initialize (this, unityGameID, this, testMode);

        mBillingProcessor = new BillingProcessor(this, GPLAY_LICENSE, this);
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(this);
        if (isAvailable) {
            mBillingProcessor.initialize();
        } else {
//            mProgress.setVisibility(View.GONE);
//            showMsg(getString(R.string.billing_not_available));
        }

//        manager = new FakeReviewManager(this);
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();
            } else {
                // There was some problem, continue regardless of the result.
            }
        });

        if (settings.contains(DATE_OF_DEATH)) {
            if(!hasConnection(getApplicationContext()))
                date_of_death = settings.getLong(DATE_OF_DEATH, 0);

//            if (settings.contains(ADS_STATUS_FOR_SOON_DYING)) {
//                AdsStatusForSoonDying = settings.getBoolean(ADS_STATUS_FOR_SOON_DYING, false);
//                if(AdsStatusForSoonDying) {
//                    if(!noAds)
//                        createInterstitialAd_For_Soon_Dying();
//                    SharedPreferences.Editor editor = settings.edit();
//                    AdsStatusForSoonDying = false;
//                    editor.putBoolean(ADS_STATUS_FOR_SOON_DYING, AdsStatusForSoonDying);
//                    editor.apply();
//                }
//            }
        }

        startMainTimer();

//        relativeLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
//            public void onSwipeTop() {
//
//                final Animation animationFlipIn = AnimationUtils.loadAnimation(MainActivity.this,
//                        android.R.anim.slide_in_left);
//                relativeLayout.startAnimation(animationFlipIn);
////                Toast.makeText(MainActivity.this, "OnSwipeTouchListener: top", Toast.LENGTH_SHORT).show();
//
////                ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
////                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
////                    @Override
////                    public void onAnimationUpdate(ValueAnimator animation) {
////                        linearLayout.setAlpha((Float) animation.getAnimatedValue());
////                    }
////                });
////                animator.start();
//            }
//            public void onSwipeBottom() {
//            //                Toast.makeText(MainActivity.this, "OnSwipeTouchListener: bottom", Toast.LENGTH_SHORT).show();
//                final Animation animationFlipOut = AnimationUtils.loadAnimation(MainActivity.this,
//                        android.R.anim.slide_out_right);
//                relativeLayout.startAnimation(animationFlipOut);
//            }
//            public void onSwipeRight() {
////                Toast.makeText(MainActivity.this, "OnSwipeTouchListener: right", Toast.LENGTH_SHORT).show();
//
//                if(!layoutStatus) {
//                    layoutStatus = true;
//                    ObjectAnimator.ofFloat(linearLayout, View.X, 0, 800).start();
//                    ObjectAnimator.ofFloat(floatingMenu, View.X, 0, 800).start();
//                }
//            }
//            public void onSwipeLeft() {
////                Toast.makeText(MainActivity.this, "OnSwipeTouchListener: left", Toast.LENGTH_SHORT).show();
//
//                if(layoutStatus) {
//                    layoutStatus = false;
//                    ObjectAnimator.ofFloat(linearLayout, View.X, 800, 0).start();
//                    ObjectAnimator.ofFloat(floatingMenu, View.X, 800, 0).start();
//                }
//            }
//
//        });

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

//        final FloatingActionButton color_match = findViewById(R.id.color_match);
//        color_match.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                timerTime = 60L; // uncomment for testing
////                years = timerTime / 31536000; // uncomment for testing
////                days = timerTime / 86400 % 365; // uncomment for testing
////                hours = timerTime / 3600 % 24; // uncomment for testing
////                mins = timerTime / 60 % 60; // uncomment for testing
////                secs = timerTime % 60; // uncomment for testing
//
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.colormatch"));
//                startActivity(intent);
//            }
//        });
//
//        final FloatingActionButton jump_up = findViewById(R.id.jump_up);
//        jump_up.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.jumpup"));
//                startActivity(intent);
//            }
//        });
//
//        final FloatingActionButton christmas_game = findViewById(R.id.christmas_game);
//        christmas_game.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.christmasgame"));
//                startActivity(intent);
//            }
//        });
//
//        final FloatingActionButton christmas_tree = findViewById(R.id.christmas_tree);
//        christmas_tree.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.christmastree"));
//                startActivity(intent);
//            }
//        });
//
//        final FloatingActionButton barley_break = findViewById(R.id.barley_break);
//        barley_break.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.barleybreak"));
//                startActivity(intent);
//            }
//        });
//
//        final FloatingActionButton shopping_calculator = findViewById(R.id.shopping_calculator);
//        shopping_calculator.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.notes"));
//                startActivity(intent);
//            }
//        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.krik);
            NotificationChannel channel = new NotificationChannel("1", getResources().getString(R.string.user_agreement_broken), NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription(getResources().getString(R.string.user_agreement_broken));
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            AudioAttributes attribute = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            channel.setSound(soundUri, attribute);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

//        FirebaseMessaging.getInstance().subscribeToTopic("general")
//            .addOnCompleteListener(task -> {
//                String msg = "Successfull";
//                if(!task.isSuccessful()) {
//                    msg = "Failed";
//                }
//                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show();
//            });

        DrawerBuilder();

        startAdsTimer();
    }

    private void clicksOnDate() {
        clicks_on_date++;
        Bundle bundle = new Bundle();
        firebaseAnalytics.logEvent("clicks_on_date", bundle);

        if (clicks_on_date >= 1) {
            clicks_on_date = 0;
            mBillingProcessor.consumePurchase(CHANGE_YOUR_FATE);
            showChangeYourFateDialog();
        }
    }

    private void changeDateOfDeath() {
        if(years > 0) {
            date_of_death = getRandomNumberInRange(600000, 17280000);
        }
        else {
            date_of_death = getRandomNumberInRange(34560000, 1382400000);
        }
        date_of_death += currentTime;

        updateValueInDatabase();
    }

    public void countDateOfDeath() {
        timerTime = date_of_death - currentTime;

        years = timerTime / 31536000;
        days = timerTime / 86400 % 365;
        hours = timerTime / 3600 % 24;
        mins = timerTime / 60 % 60;
        secs = timerTime % 60;

        playSound(countdown);
    }

    private void updateValueInDatabase() {
        User user = new User(getUniqueKey(), date_of_death, noAds, nameAndSurname, dateOfBirth, placeOfBirth);
        myReference
                .child("users")
                .child(getUniqueKey())
                .setValue(user);
        Log.d("TAG", date_of_death + "|" + noAds + "|" + nameAndSurname + "|" + dateOfBirth + "|" + placeOfBirth);
    }

    private int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private long getRandomLongInRange(long min, long max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return min + (long) (Math.random() * (max - min));
    }

//    MediaPlayer mp;
//    private void playStartSound(int soundID) {
//        releaseMP();
//
//        try {
//            mp = new MediaPlayer();
//            mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
//            mp = MediaPlayer.create(this, soundID);
//    //        mp.prepareAsync();
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
//                // Permission is granted
//                final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//                final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
//                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
//                mp.start();
//                mp.setOnCompletionListener(mp1 -> mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, originalVolume, 0));
//            } else {
//                mp.start();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public void playSound(int soundID) {
//        mp = MediaPlayer.create(this, soundID);
//        mp.setOnCompletionListener(MediaPlayer::release);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
//            // Permission is granted
//            final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//            final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
//            mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
//            mp.start();
//            mp.setOnCompletionListener(mp1 -> mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, originalVolume, 0));
//        } else {
//            mp.start();
//        }
//    }

    public void playSound(int soundID) {
//        soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) -> soundPool.play(sampleId, 1.0f, 1.0f, 0, 0, 1.0f));
        soundPool.play(soundID, 1.0f, 1.0f, 0, 0, 1.0f);
    }

//    private void releaseMP() {
//        if (mp != null) {
//            try {
//                mp.release();
//                mp = null;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void adsInitialization() {
        int banner = getRandomNumberInRange(1, 4);
        switch (banner)
        {
            case 1:
            {
                AdView mAdView = findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice("2915B28E56B33B9CC3D2C5D421E9FE3E")
                        .addTestDevice("1D5297D5D4A3A977DCE0D970B2D4F83A")
                        .addTestDevice("F8E3AC49CB906029A3F3B414144CFB18")
                        .build();
                mAdView.loadAd(adRequest);

                MobileAds.initialize(this, initializationStatus -> {
                });
            }
            default:
            {
//                webView.setVisibility(View.VISIBLE);
                // включаем поддержку JavaScript
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new MyWebViewClient());
//                webView.loadData("<html><body>Hello, Android</body></html>","text/html", "UTF-8");
//                webView.loadUrl("file:///android_asset/index.html");
                webView.loadUrl("file:///android_asset/index.html");
            }
        }
    }

    private final Drawer.OnDrawerItemClickListener onClicksLis = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            if(drawerItem.getIdentifier() == BTN_COLOR_MATCH)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.colormatch"));
                startActivity(intent);
                return true;
            }
            else if(drawerItem.getIdentifier() == BTN_JUMP_UP)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.jumpup"));
                startActivity(intent);
                return true;
            }
//            else if(drawerItem.getIdentifier() == BTN_CHRISTMAS_GAME)
//            {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.christmasgame"));
//                startActivity(intent);
//                return true;
//            }
            else if(drawerItem.getIdentifier() == BTN_CHRISTMAS_TREE)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.christmastree"));
                startActivity(intent);
                return true;
            }
            else if(drawerItem.getIdentifier() == BTN_BARLEY_BREAK)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.barleybreak"));
                startActivity(intent);
                return true;
            }
            else if(drawerItem.getIdentifier() == BTN_SHOPPING_CALCULATOR)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.notes"));
                startActivity(intent);
                return true;
            }
            else if(drawerItem.getIdentifier() == BTN_OUR_APPS) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=DDRVLD+Lab."));
                startActivity(intent);
                return true;
            }
            else if(drawerItem.getIdentifier() == BTN_SHARE)
            {
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text_1) +
                        (years > 0 ? " " + years + " " + GetWord(years,
                                getResources().getString(R.string.text_yrs1),
                                getResources().getString(R.string.text_yrs2),
                                getResources().getString(R.string.text_yrs3)) : "") +
                        (days > 0 ? " " + days + " " + GetWord(days,
                                getResources().getString(R.string.text_day1),
                                getResources().getString(R.string.text_day2),
                                getResources().getString(R.string.text_day3)) : "") +
                        (hours > 0 ? " " + hours + " " + GetWord(hours,
                                getResources().getString(R.string.text_hrs1),
                                getResources().getString(R.string.text_hrs2),
                                getResources().getString(R.string.text_hrs3)) : "") +
                        (mins > 0 ? " " + mins + " " + GetWord(mins,
                                getResources().getString(R.string.text_min1),
                                getResources().getString(R.string.text_min2),
                                getResources().getString(R.string.text_min3)) : "") +
                        ".\n" + getResources().getString(R.string.share_text_2) +
                        "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
                try {
                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.app_name)));
                } catch (android.content.ActivityNotFoundException ex) {
//                  Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.some_error), Snackbar.LENGTH_SHORT).show();
                }
                return true;
            }
            else if(drawerItem.getIdentifier() == BTN_CHANGE_UR_FATE) {
//                if(user == null) {
//                    // Configure Google Sign In
//                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                            .requestIdToken(getString(R.string.default_web_client_id))
//                            .requestEmail()
//                            .requestProfile()
//                            .build();
//
//                    // Build a GoogleSignInClient with the options specified by gso.
//                    mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
//
//                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                    startActivityForResult(signInIntent, RC_SIGN_IN);
//                }
//                else {
//                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
//                    startActivity(intent);
//                }
//                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.coming_soon), Snackbar.LENGTH_SHORT).show();
                mBillingProcessor.purchase(MainActivity.this, CHANGE_YOUR_FATE);
                return true;
            }
            else if(drawerItem.getIdentifier() == BTN_DISABLE_ADS) {
                Bundle bundle = new Bundle();
                firebaseAnalytics.logEvent("click_on_disable_ads_button", bundle);
                mBillingProcessor.purchase(MainActivity.this, DISABLE_ADS);
                return true;
            }
//            else if(drawerItem.getIdentifier() == BTN_CHAT) {
//                if(user == null) {
//                    // Configure Google Sign In
//                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                            .requestIdToken(getString(R.string.default_web_client_id))
//                            .requestEmail()
//                            .requestProfile()
//                            .build();
//
//                    // Build a GoogleSignInClient with the options specified by gso.
//                    mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
//
//                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                    startActivityForResult(signInIntent, RC_SIGN_IN);
//                }
//                else {
//                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
//                    startActivity(intent);
//                }
//                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.coming_soon), Snackbar.LENGTH_SHORT).show();
//                return true;
//            }
            return false;
        }
    };

//    private IDrawerItem[] initializeStickyDrawerItems() {
//        return new IDrawerItem[]{
//        };
//    }

//    private Bitmap drawable_from_url(String url) throws java.io.IOException {
//
//        HttpURLConnection connection = (HttpURLConnection)new URL(url) .openConnection();
//        connection.setRequestProperty("User-agent","Mozilla/4.0");
//
//        connection.connect();
//        InputStream input = connection.getInputStream();
//
//        return BitmapFactory.decodeStream(input);
//    }

//    public static Drawable drawableFromUrl(String url) throws IOException {
//        Bitmap x;
//
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.connect();
//        InputStream input = connection.getInputStream();
//
//        x = BitmapFactory.decodeStream(input);
//        return new BitmapDrawable(Resources.getSystem(), x);
//    }

//    private AccountHeader initializeAccountHeader() {
//        IProfile profile = new ProfileDrawerItem()
//                .withName(accountName)
//                .withEmail(accountEmail)
////                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
////                    @Override
////                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
////                        Intent intent = new Intent(Intent.ACTION_VIEW);
////                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=by.ddrvld.notes"));
////                        startActivity(intent);
////                        return false;
////                    }
////                })
//                .withIcon(getResources().getDrawable(R.drawable.ic_launcher));
////                .withIcon(Picasso.get().load(photoUrl).toString());
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

        dialogButtonYes.setOnClickListener(v -> {
            dialog.cancel();
            setContentView(R.layout.activity_wait);

            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dialog_accept_and_continue_click_yes");
            firebaseAnalytics.logEvent("dialog_accept_and_continue_click_yes", bundle);

//            webView = findViewById(R.id.webView);
//            webView.setWebViewClient(new MyWebViewClient());

            date_of_death = getDateOfDeathFirstTime();
            saveTime();
            showMsg("getDateOfDeathFirstTime");
            updateValueInDatabase();
        });
        dialogButtonNo.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dialog_accept_and_continue_click_no");
            firebaseAnalytics.logEvent("dialog_accept_and_continue_click_no", bundle);

            dialog.cancel();
            finish();
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

        dialogButtonYes.setOnClickListener(v -> {
            dialog.cancel();

            lastRatingDay = 1000;
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(LAST_RATING_DAY, lastRatingDay);
            editor.apply();

            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dialog_rating_click_yes");
            firebaseAnalytics.logEvent("dialog_rating_click_yes", bundle);

            if(reviewInfo != null) {
                Task<Void> flow = manager.launchReviewFlow(MainActivity.this, reviewInfo);
                flow.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "inAppReview isSuccessful");
                    }
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                startActivity(intent);
            }
        });
        dialogButtonNo.setOnClickListener(v -> {
            dialog.cancel();

            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dialog_rating_click_no");
            firebaseAnalytics.logEvent("dialog_rating_click_no", bundle);

            Calendar cal = Calendar.getInstance();
            lastRatingDay = cal.get(Calendar.DAY_OF_MONTH);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(LAST_RATING_DAY, lastRatingDay);
            editor.apply();
        });
        try {
            dialog.setCancelable(false);
            dialog.show();
        }
        catch (Exception ignored) {
        }
    }

    private void createInterstitialAd() {
        //Создаём межстраничное объявление
        final InterstitialAd interstitial;

        interstitial = new InterstitialAd(MainActivity.this);
        interstitial.setAdUnitId("ca-app-pub-7528412641056592/9979660478");
        AdRequest adRequesti = new AdRequest.Builder()
                .addTestDevice("2915B28E56B33B9CC3D2C5D421E9FE3E")
                .addTestDevice("1D5297D5D4A3A977DCE0D970B2D4F83A")
                .addTestDevice("F8E3AC49CB906029A3F3B414144CFB18")
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
                TimerToActivityDate();
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
                TimerToActivityDate();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
//                TimerToActivityDate();
//                showKinovoWebView();
            }
        });
    }

//    private void createInterstitialAd_For_Soon_Dying() { //Создаём межстраничное объявление
//        final InterstitialAd interstitial;
//
//        interstitial = new InterstitialAd(MainActivity.this);
//        interstitial.setAdUnitId("ca-app-pub-7528412641056592/7503516855");
//        AdRequest adRequesti = new AdRequest.Builder()
//                .addTestDevice("2915B28E56B33B9CC3D2C5D421E9FE3E")
//                .addTestDevice("1D5297D5D4A3A977DCE0D970B2D4F83A")
//                .addTestDevice("F8E3AC49CB906029A3F3B414144CFB18")
//                .build();
//        interstitial.loadAd(adRequesti);
//        interstitial.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                interstitial.show();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                super.onAdFailedToLoad(errorCode);
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//            }
//
//            @Override
//            public void onAdClicked() {
//                super.onAdClicked();
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                super.onAdLeftApplication();
//            }
//
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//            }
//        });
//    }

//    private void PermissionRequest() {
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            // Here, thisActivity is the current activity
//            if (ContextCompat.checkSelfPermission(MainActivity.this,
//                    Manifest.permission.READ_PHONE_STATE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                // Permission is not granted
//                // Should we show an explanation?
//
//                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                        Manifest.permission.READ_PHONE_STATE)) {
//                    // Show an explanation to the user *asynchronously* -- don't block
//                    // this thread waiting for the user's response! After the user
//                    // sees the explanation, try again to request the permission.
//                    ErrorReadPhoneStateDialog();
//
//                    Bundle bundle = new Bundle();
////            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "permission_read_phone_state_cancel");
//                    firebaseAnalytics.logEvent("permission_read_phone_state_cancel", bundle);
//                } else {
//                    // No explanation needed; request the permission
//                    ActivityCompat.requestPermissions(MainActivity.this,
//                            new String[]{Manifest.permission.READ_PHONE_STATE},
//                            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
//
//                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                    // app-defined int constant. The callback method gets the
//                    // result of the request.
//                }
//            } else {
//                // Permission has already been granted
////                if(activityDateTimer == null)
//                    TimerToActivityDate();
//
//                Bundle bundle = new Bundle();
////            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "permission_read_phone_state_accept");
//                firebaseAnalytics.logEvent("permission_read_phone_state_accept", bundle);
//            }
//        } else {
////            if(activityDateTimer == null)
//                TimerToActivityDate();
//        }
//    }

//    public void ErrorReadPhoneStateDialog() {
//        final Context context = this;
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.error_read_phone_state_dialog);
//
//        TextView titleDialog = dialog.findViewById(R.id.titleDialog);
//
//        titleDialog.setText(Html.fromHtml(getString(R.string.app_name)));
//
//        TextView messageDialog = dialog.findViewById(R.id.messageDialog);
//        messageDialog.setText(R.string.read_phone_state_text);
//
//        Button dialogButtonYes = dialog.findViewById(R.id.dialogButtonYes);
//
//        dialogButtonYes.setText(R.string.understand);
//
//        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.cancel();
//                finish();
//                Intent intentX = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("package", getPackageName(), null);
//                intentX.setData(uri);
//                if (intentX.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intentX);
//                }
//            }
//        });
//        dialog.setCancelable(false);
//        dialog.show();
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
////                    if(activityDateTimer == null)
//                        TimerToActivityDate();
//
//                    Bundle bundle = new Bundle();
////            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "permission_read_phone_state_accept");
//                    firebaseAnalytics.logEvent("permission_read_phone_state_accept", bundle);
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    TimerToActivityDate();
//                }
//            }
//            // other 'case' lines to check for other
//            // permissions this app might request.
//        }
//    }

    public void TimerRating() {
        final Thread ratingTimer = new Thread() {
            public void run() {
                try {
                    Thread.sleep(60 * 1000);
                    runOnUiThread(() -> {
                        DialogRating();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        ratingTimer.start();
    }

    public void TimerToActivityDate() {
        final Thread activityDateTimer = new Thread() {
            public void run() {
                try {
                    Thread.sleep(2 * 1000);
                    runOnUiThread(() -> {
                        onCreateActivityDate();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        activityDateTimer.start();
    }

    public void theEnd() {
        playSound(krik);
        createVibration(3000);
    }

    @SuppressLint("HardwareIds")
    private String getUniqueKey() {
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private long getDateOfDeathFirstTime() {
        return getRandomLongInRange(1630000000L, 3400000000L);
    }

    @Override
    public View makeView() {
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextSize(88);
        textView.setTextColor(Color.WHITE);
//        textView.setShadowLayer(5, 5, 5, Color.WHITE);
        return textView;
    }

    public class AdsTimer extends CountDownTimer {

        public AdsTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            if(!noAds)
                createInterstitialAd();
        }

        @Override
        public void onTick(long millisUntilFinished) {}
    }

    private AdsTimer adsTimer;
    private void startAdsTimer() {
        if(!noAds) {
            if (adsTimer == null) {
                adsTimer = new AdsTimer(12 * 1000, 1000);
                adsTimer.start();
            }
        }
    }

    // CountDownTimer class
    public class MainCountDownTimer extends CountDownTimer {

        public MainCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            theEnd();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            runOnUiThread(MainActivity.this::updateUI);
            long fullDays = 364L, fullHours = 23L, fullMins = 59L, fullSecs = 59L;
            if (secs > 0) secs--;
            else {
                secs = fullSecs;
                if (mins > 0) mins--;
                else {
                    mins = fullMins;
                    if (hours > 0) hours--;
                    else {
                        hours = fullHours;
                        if (days > 0) days--;
                        else {
                            days = fullDays;
                            if (years > 0) years--;
                            else {
                                theEnd();
                                if(mainTimer != null)
                                    mainTimer.cancel();
                            }
                        }
                    }
                }
            }
        }
    }

    private CountDownTimer mainTimer;
    private void startMainTimer() {
        if(!hasConnection(getApplicationContext()))
            countDateOfDeath();
        if(mainTimer == null && date_of_death > currentTime) {
            mainTimer = new MainCountDownTimer(1000000000, 1000);
            mainTimer.start();
        }
        else updateUI();
    }

    @UiThread
    private void updateUI() {
        TextView tvTextYrs = (TextView) tvYrs.getChildAt(0);
        TextView tvTextDay = (TextView) tvDay.getChildAt(0);
        TextView tvTextHrs = (TextView) tvHrs.getChildAt(0);
        TextView tvTextMin = (TextView) tvMin.getChildAt(0);
        TextView tvTextSec = (TextView) tvSec.getChildAt(0);

//        if (years > 0) {
        if (years >= 10) tvYrs.setText(String.valueOf(years));
        else tvYrs.setText(String.format("0%s", years));
//        } else tvTextYrs.setText(String.format("0%s", years));
//        if (days > 0) {
        if (days >= 10) tvDay.setText(String.valueOf(days));
        else tvDay.setText(String.format("0%s", days));
//        } else tvTextDay.setText(String.format("0%s", days));
//        if (hours > 0) {
        if (hours >= 10) tvHrs.setText(String.valueOf(hours));
        else tvHrs.setText(String.format("0%s", hours));
//        } else tvTextHrs.setText(String.format("0%s", hours));
//        if(mins > 0) {
        if (mins >= 10) tvMin.setText(String.valueOf(mins));
        else tvMin.setText(String.format("0%s", mins));
//        } else tvTextMin.setText(String.format("0%s", mins));
//        if(secs > 0) {
        if (secs >= 10) tvSec.setText(String.valueOf(secs));
        else tvSec.setText(String.format("0%s", secs));
//        } else tvTextSec.setText(String.format("0%s", secs));

        if (years <= 0 && days <= 0 && hours <= 0 && mins <= 0 && secs <= 0) {
            tvYrs.setText("B");
            tvDay.setText("O");
            tvHrs.setText("O");
            tvMin.setText("O");
            tvSec.setText("O");

            textYrs.setVisibility(View.INVISIBLE);
            textDay.setVisibility(View.INVISIBLE);
            textHrs.setVisibility(View.INVISIBLE);
            textMin.setVisibility(View.INVISIBLE);
            textSec.setVisibility(View.INVISIBLE);

            if(mainTimer != null)
                mainTimer.cancel();
            theEnd();
        }

        textYrs.setText(GetWord(years, getResources().getString(R.string.text_yrs1), getResources().getString(R.string.text_yrs2), getResources().getString(R.string.text_yrs3)));
        textDay.setText(GetWord(days, getResources().getString(R.string.text_day1), getResources().getString(R.string.text_day2), getResources().getString(R.string.text_day3)));
        textHrs.setText(GetWord(hours, getResources().getString(R.string.text_hrs1), getResources().getString(R.string.text_hrs2), getResources().getString(R.string.text_hrs3)));
        textMin.setText(GetWord(mins, getResources().getString(R.string.text_min1), getResources().getString(R.string.text_min2), getResources().getString(R.string.text_min3)));
        textSec.setText(GetWord(secs, getResources().getString(R.string.text_sec1), getResources().getString(R.string.text_sec2), getResources().getString(R.string.text_sec3)));

        if (years == 0) {
            tvTextYrs.setTextColor(getResources().getColor(R.color.red, getTheme()));
            textYrs.setTextColor(getResources().getColor(R.color.red, getTheme()));
            if (days == 0) {
                tvTextDay.setTextColor(getResources().getColor(R.color.red, getTheme()));
                textDay.setTextColor(getResources().getColor(R.color.red, getTheme()));
                if (hours == 0) {
                    tvTextHrs.setTextColor(getResources().getColor(R.color.red, getTheme()));
                    textHrs.setTextColor(getResources().getColor(R.color.red, getTheme()));
                    if (mins == 0) {
                        tvTextMin.setTextColor(getResources().getColor(R.color.red, getTheme()));
                        textMin.setTextColor(getResources().getColor(R.color.red, getTheme()));
                        if (secs == 0) {
                            tvTextSec.setTextColor(getResources().getColor(R.color.red, getTheme()));
                            textSec.setTextColor(getResources().getColor(R.color.red, getTheme()));
                        } else {
                            tvTextSec.setTextColor(getResources().getColor(R.color.white, getTheme()));
                            textSec.setTextColor(getResources().getColor(R.color.white, getTheme()));
                        }
                    } else {
                        tvTextMin.setTextColor(getResources().getColor(R.color.white, getTheme()));
                        textMin.setTextColor(getResources().getColor(R.color.white, getTheme()));
                    }
                } else {
                    tvTextHrs.setTextColor(getResources().getColor(R.color.white, getTheme()));
                    textHrs.setTextColor(getResources().getColor(R.color.white, getTheme()));
                }
            } else {
                tvTextDay.setTextColor(getResources().getColor(R.color.white, getTheme()));
                textDay.setTextColor(getResources().getColor(R.color.white, getTheme()));
            }
        } else {
            tvTextYrs.setTextColor(getResources().getColor(R.color.white, getTheme()));
            textYrs.setTextColor(getResources().getColor(R.color.white, getTheme()));
        }
    }

//    private void sendInAppNotification() {
//        theEnd();
//
////        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.krik);
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(MainActivity.this, getResources().getString(R.string.app_name))
//                        .setSmallIcon(R.drawable.ic_launcher)
//                        .setContentTitle(getResources().getString(R.string.app_name))
//                        .setContentText(getResources().getString(R.string.user_agreement_broken))
////                        .setSound(soundUri)
//                        .setLights(0xff0000ff, 100, 100)
//                        .setVibrate(new long[] { 200, 3000})
//                        .setOngoing(true)
//                        .setTimeoutAfter(30000)
////                .setDefaults(Notification.DEFAULT_LIGHTS)
////                .setWhen(System.currentTimeMillis())
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationManagerCompat notificationManager =
//                NotificationManagerCompat.from(MainActivity.this);
//        notificationManager.notify(0, builder.build());
//    }

    private void saveTime() {
//        long time, yearsNow, daysNow, hoursNow, minsNow;
//        yearsNow = years * 31536000;
//        daysNow = days * 86400;
//        hoursNow = hours * 3600;
//        minsNow = mins * 60;
//        time = yearsNow + daysNow + hoursNow + minsNow + secs;
        if(date_of_death >= 0) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(DATE_OF_DEATH, date_of_death);
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerResult != null && drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        }
        else if(webView != null) {
            if(webView.canGoBack()/* && !webView.getUrl().equals("https://kinovo.online")*/) {
                webView.goBack();
            }
            else {
                webView.setVisibility(View.INVISIBLE);
                TimerToActivityDate();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (settings.contains(PERIOD_SETTINGS)) {
//            PERIOD = settings.getInt(PERIOD_SETTINGS, 0);
//        }
    }

    @Override
    protected void onPause() {
        if (timerTime > 0) {
            long timeToNotifi, yearsNow, daysNow, hoursNow, minsNow;
            yearsNow = years * 31536000;
            daysNow = days * 86400;
            hoursNow = hours * 3600;
            minsNow = mins * 60;
            timeToNotifi = yearsNow + daysNow + hoursNow + minsNow;

//            if(timerTime > 36000)
//                timeToNotifi /= 3;
//            else if(timerTime > 160)
//                timeToNotifi = timerTime - 160L;
//            else if(timerTime > 3)
//                timeToNotifi = timerTime;
//            else return;

//            SharedPreferences.Editor editor = settings.edit();
//            AdsStatusForSoonDying = true;
//            editor.putBoolean(ADS_STATUS_FOR_SOON_DYING, AdsStatusForSoonDying);
//            editor.apply();

            Intent intent = new Intent(this, Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (timeToNotifi * 1000), pendingIntent);
//            Snackbar.make(findViewById(android.R.id.content), "Alarm set in " + timeToNotifi + " seconds",Snackbar.LENGTH_LONG).show();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mainTimer != null) {
            mainTimer.cancel();
            mainTimer = null;
        }
        if (mBillingProcessor != null) {
            mBillingProcessor.release();
        }
        super.onDestroy();
    }

    // Implement a function to display an ad if the Placement is ready:
//    public void DisplayUnityInterstitialAd() {
//        if (UnityAds.isReady (placementId)) {
//            UnityAds.show(this, placementId);
//        }
//    }
//
//    @Override
//    public void onUnityAdsReady (String placementId) {
//        // Implement functionality for an ad being ready to show.
//        System.out.println("onUnityAdsReady");
//    }
//
//    @Override
//    public void onUnityAdsStart (String placementId) {
//        // Implement functionality for a user starting to watch an ad.
//        System.out.println("onUnityAdsStart");
//    }
//
//    @Override
//    public void onUnityAdsFinish (String placementId, UnityAds.FinishState finishState) {
//        TimerToActivityDate();
//    }
//
//    @Override
//    public void onUnityAdsError (UnityAds.UnityAdsError error, String message) {
//        TimerToActivityDate();
//    }

//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w("TAG", "Google sign in failed", e);
//                // ...
//            }
//        }
//    }

//    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
//        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Log.d("TAG", "signInWithCredential:success");
////                            updateUI(user);
////                            DrawerBuilder();
//
//                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
//                        startActivity(intent);
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Log.w("TAG", "signInWithCredential:failure", task.getException());
//                        Snackbar.make(findViewById(R.id.relative_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
////                            updateUI(null);
//                    }
//                    // ...
//                });
//    }

    private void DrawerBuilder() {
//        accountName = acct.getDisplayName();
//        accountEmail = acct.getEmail();
//        photoUrl = acct.getPhotoUrl();

        final DividerDrawerItem dividerDrawerItem = new DividerDrawerItem()
                .withEnabled(true);

        final PrimaryDrawerItem ourAppsDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.our_apps)
                .withTextColorRes(R.color.white)
                .withIdentifier(BTN_OUR_APPS);

        final PrimaryDrawerItem colorMatchDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.color_match)
                .withTextColorRes(R.color.white)
                .withIcon(R.drawable.img_color_match)
                .withIdentifier(BTN_COLOR_MATCH);

        final PrimaryDrawerItem jumpUpDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.jump_up)
                .withTextColorRes(R.color.white)
                .withIcon(R.drawable.img_jump_up)
                .withIdentifier(BTN_JUMP_UP);

//        final PrimaryDrawerItem christmasGameDrawerItem = new PrimaryDrawerItem()
//                .withName(R.string.christmas_game)
//                .withTextColorRes(R.color.white)
//                .withIcon(R.drawable.img_christmas_game)
//                .withIdentifier(BTN_CHRISTMAS_GAME);

        final PrimaryDrawerItem christmasTreeDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.christmas_tree)
                .withTextColorRes(R.color.white)
                .withIcon(R.drawable.img_christmas_tree)
                .withIdentifier(BTN_CHRISTMAS_TREE);

        final PrimaryDrawerItem barleyBreakDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.barley_break)
                .withTextColorRes(R.color.white)
                .withIcon(R.drawable.img_barley_break)
                .withIdentifier(BTN_BARLEY_BREAK);

        final PrimaryDrawerItem shoppingCalculatorDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.shopping_calculator)
                .withTextColorRes(R.color.white)
                .withIcon(R.drawable.img_shopping_calculator)
                .withIdentifier(BTN_SHOPPING_CALCULATOR);

        final PrimaryDrawerItem changeYourFateDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.change_your_fate)
                .withTextColorRes(R.color.white)
                .withIcon(R.drawable.icon_filled)
                .withIdentifier(BTN_CHANGE_UR_FATE);

        final PrimaryDrawerItem shareDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.share)
                .withTextColorRes(R.color.white)
                .withIcon(android.R.drawable.ic_menu_share)
                .withIdentifier(BTN_SHARE);

        PrimaryDrawerItem disableAdsDrawerItem;
        if(!noAds) {
            disableAdsDrawerItem = new PrimaryDrawerItem()
                    .withName(R.string.disable_ads)
                    .withTextColorRes(R.color.white)
                    .withIcon(R.drawable.no_ads)
                    .withIdentifier(BTN_DISABLE_ADS);
        }
        else disableAdsDrawerItem = null;

//        AccountHeader accountHeader = initializeAccountHeader();
        drawerResult = new DrawerBuilder()
                .withActivity(MainActivity.this)
                .withToolbar(toolbar)
                .withSliderBackgroundColorRes(R.color.transparent)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(dividerDrawerItem)
                .addDrawerItems(ourAppsDrawerItem)
                .addDrawerItems(colorMatchDrawerItem)
                .addDrawerItems(jumpUpDrawerItem)
                .addDrawerItems(christmasTreeDrawerItem)
                .addDrawerItems(barleyBreakDrawerItem)
                .addDrawerItems(shoppingCalculatorDrawerItem)
                .addDrawerItems(dividerDrawerItem)
                .addDrawerItems(dividerDrawerItem)
                .addDrawerItems(changeYourFateDrawerItem)
                .addDrawerItems(disableAdsDrawerItem)
                .addDrawerItems(shareDrawerItem)
                //            .addStickyDrawerItems(initializeStickyDrawerItems())
                .withOnDrawerItemClickListener(onClicksLis)
                //            .withAccountHeader(accountHeader)
                //            .withRootView(R.id.drawer_layout)
                .withGenerateMiniDrawer(true)
                .build();
    }

//    private void showKinovoWebView() {
//        webView.setVisibility(View.VISIBLE);
//        // включаем поддержку JavaScript
//        webView.getSettings().setJavaScriptEnabled(true);
//        // указываем страницу загрузки
//        webView.loadUrl("https://kinovo.online");
//    }

    private class MyWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        // Для старых устройств
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
        showMsg("onBillingInitialized");
        if (mBillingProcessor.loadOwnedPurchasesFromGoogle()) {
            handleLoadedItems();
        }
    }

    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
        if(mBillingProcessor.isPurchased(DISABLE_ADS))
            noAds(true);

        showMsg("onPurchaseHistoryRestored");
        handleLoadedItems();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
        showMsg("onBillingError");
    }

    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        showMsg("onProductPurchased");
        if(details != null) {
            if (checkIfPurchaseIsValid(details.purchaseInfo)) {
                showMsg("purchase: " + productId + " COMPLETED");
                switch (productId) {
                    case CHANGE_YOUR_FATE:
                        noAds(true);
                        changeDateOfDeath();
                        break;
                    case DISABLE_ADS:
                        noAds(true);
                        updateValueInDatabase();
                        break;
                }
            } else {
                showMsg("fakePayment");
            }
        }
    }

    private void noAds(boolean adsStatus) {
        RelativeLayout relative_layout_ads = findViewById(R.id.relative_layout_ads);
        if(adsStatus) {
            noAds = true;
            if(relative_layout_ads.getVisibility() != View.GONE)
                relative_layout_ads.setVisibility(View.GONE);
        } else {
            noAds = false;
            if(relative_layout_ads.getVisibility() != View.VISIBLE)
                relative_layout_ads.setVisibility(View.VISIBLE);
        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(NO_ADS, adsStatus);
        editor.apply();
        DrawerBuilder();
    }

    private boolean checkIfPurchaseIsValid(PurchaseInfo purchaseInfo) {
        // TODO as of now we assume that all purchases are valid
        return true;
    }

    private void handleLoadedItems() {
//        mProgress.setVisibility(View.GONE);

//        boolean isOneTimePurchaseSupported = mBillingProcessor.isOneTimePurchaseSupported();
//        if (isOneTimePurchaseSupported) {
////            mSingleTimePaymentButton.setVisibility(View.VISIBLE);
////            mConsumabelButton.setVisibility(View.VISIBLE);
//        } else {
//            showMsg("one_time_payment_not_supported");
//        }
//
//        boolean isSubsUpdateSupported = mBillingProcessor.isSubscriptionUpdateSupported();
//        if (isSubsUpdateSupported) {
////            mSubscriptionButton.setVisibility(View.VISIBLE);
//        } else {
//            showMsg("subscription_not_supported");
//        }

//        changeFate(mBillingProcessor.listOwnedProducts().contains(CHANGE_YOUR_FATE));
    }

    private void showChangeYourFateDialog() {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_your_fate);

        Button dialogButtonYes = dialog.findViewById(R.id.dialogButtonYes);
        Button dialogButtonNo = dialog.findViewById(R.id.dialogButtonNo);

        dialogButtonYes.setOnClickListener(v -> {
            dialog.cancel();
            mBillingProcessor.purchase(MainActivity.this, CHANGE_YOUR_FATE);

            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dialog_change_your_fate_yes");
            firebaseAnalytics.logEvent("dialog_change_your_fate_yes", bundle);
        });
        dialogButtonNo.setOnClickListener(v -> {
            dialog.cancel();

            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "dialog_change_your_fate_no");
            firebaseAnalytics.logEvent("dialog_change_your_fate_no", bundle);
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    public void EnterInformationDialog() {
        final Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.enter_information_dialog);

        Button dialogButtonYes = dialog.findViewById(R.id.dialogButtonYes);

        TextInputEditText textInptEdtTxtName = dialog.findViewById(R.id.name);
        textViewBirthday = dialog.findViewById(R.id.birthday);
        TextInputEditText textInptEdtTxtPlaceOfBirth = dialog.findViewById(R.id.placeOfBirth);

        Bundle bundle = new Bundle();
        firebaseAnalytics.logEvent("EnterInformationDialog", bundle);

        textViewBirthday.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListener, year, month, day);

            dpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dpDialog.show();
        });

        dateSetListener = (view, year, month, dayOfMonth) -> {
            month += 1; // correction
            dateOfBirth = dayOfMonth + "." + month + "." + year;
            textViewBirthday.setText(dateOfBirth);
        };

        dialogButtonYes.setOnClickListener(v -> {
            if((!textInptEdtTxtName.getText().equals("") && textInptEdtTxtName.length() > 3) &&
                    (!textViewBirthday.getText().equals("Birthday")) &&
                    (!textInptEdtTxtPlaceOfBirth.getText().equals("") && textInptEdtTxtPlaceOfBirth.length() > 3)) {

                nameAndSurname = textInptEdtTxtName.getText().toString();
                placeOfBirth = textInptEdtTxtPlaceOfBirth.getText().toString();

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(NAME_AND_SURNAME, nameAndSurname);
                editor.putString(DATE_OF_BIRTH, dateOfBirth);
                editor.putString(PLACE_OF_BIRTH, placeOfBirth);
                editor.apply();

                dialog.cancel();
                onCreateActivityTermOfUse();
                firebaseAnalytics.logEvent("EnterInformationDialog_accept_button", bundle);
            }
            else {
                Snackbar.make(findViewById(android.R.id.content), R.string.enterAllData, Snackbar.LENGTH_SHORT).show();
                firebaseAnalytics.logEvent("enterAllData_snackbar_message", bundle);
            }
//            Intent intentX = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            Uri uri = Uri.fromParts("package", getPackageName(), null);
//            intentX.setData(uri);
//            if (intentX.resolveActivity(getPackageManager()) != null) {
//                startActivity(intentX);
//            }
            //submit
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showMsg(String text) {
//        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        Log.d("showMsg", text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mBillingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void readFromDatabase() {
        // Read from the database
        myReference.child("users").child(getUniqueKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                if(user == null) { // если в БД нет записи о пользователе
                    EnterInformationDialog();
                    Log.e("READ_FROM_DATABASE", "terms_of_use");
                } else {
                    date_of_death = user.getDateOfDeath();
                    if (mainTimer == null) {  // при обычном запуске приложения (mainTimer не запущен)
                        onCreateActivityDate();
                        Log.e("READ_FROM_DATABASE", "mainTimer == null");
                    }
                    countDateOfDeath();
                    saveTime();
                    noAds(user.getNoAds());
                    Log.e("DATE_OF_DEATH", "Value is: " + date_of_death);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("DATE_OF_DEATH", "Failed to read value.", error.toException());
            }
        });
    }

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    private void createVibration(int duration) {
        Vibrator vibration = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibration.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibration.vibrate(duration);
        }
    }

//    public static class MyFragmentPagerAdapter extends FragmentPagerAdapter {
//
//        public MyFragmentPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return LeftFragment.newInstance(position);
//        }
//
//        @Override
//        public int getCount() {
//            return PAGE_COUNT;
//        }
//    }
//
//    public static class LeftFragment extends Fragment {
//
//        public static LeftFragment newInstance(int page) {
//            LeftFragment leftFragment = new LeftFragment();
////            Bundle arguments = new Bundle();
////            arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
////            pageFragment.setArguments(arguments);
//            return leftFragment;
//        }
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
////            pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
//
////            Random rnd = new Random();
////            backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View view = inflater.inflate(R.layout.activity_date, null);
//
//            TextView tvPage = (TextView) view.findViewById(R.id.tv);
////            tvPage.setText("Page " + pageNumber);
////            tvPage.setBackgroundColor(backColor);
//
//            return view;
//        }
//    }
}