package by.ddrvld.countdowndeathapp;

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
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements IUnityAdsListener {
    static SharedPreferences settings;
    static final String APP_PREFERENCES = "settings";
    private final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

    static final String DATE_OF_DEATH = "randomlifetime";
    static final String PERIOD_SETTINGS = "period";
    static final String LAST_RATING_DAY = "last_rating_day";
    static final String ADS_STATUS_FOR_SOON_DYING = "AdsStatusForSoonDying";
//    private ImageView moreAppsBtn;

    int lastRatingDay;

    long timerTime, date_of_death;
    long currentTime = System.currentTimeMillis() / 1000;

    long fullDays = 364L, fullHours = 23L, fullMins = 59L, fullSecs = 59L;
    long years, days, hours, mins, secs;

    TextView tvYrs, tvDay, tvHrs, tvMin, tvSec;
    TextView textYrs, textDay, textHrs, textMin, textSec;

    private AdView mAdView;
    private Drawer drawerResult;

//    private RelativeLayout relativeLayout;
//    private LinearLayout linearLayout;
//    private RelativeLayout floatingMenu;

    public static int PERIOD;

    private boolean AdsStatusForSoonDying = false;
//    private boolean layoutStatus = false;

    private final int BTN_COLOR_MATCH = 1;
    private final int BTN_JUMP_UP = 2;
    private final int BTN_CHRISTMAS_GAME = 3;
    private final int BTN_CHRISTMAS_TREE = 4;
    private final int BTN_BARLEY_BREAK = 5;
    private final int BTN_SHOPPING_CALCULATOR = 6;
    private final int BTN_OUR_APPS = 7;
    private final int BTN_SHARE = 8;
    private final int BTN_CHAT = 9;

//    static final int PAGE_COUNT = 2;

//    ViewPager pager;
//    PagerAdapter pagerAdapter;

    private String unityGameID = "3523564";
    private Boolean testMode = true;
    private String placementId = "video";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private int RC_SIGN_IN = 1101;
    private GoogleSignInClient mGoogleSignInClient;

    private String accountName = "";
    private String accountEmail = "";
    private Uri photoUrl;

    private Toolbar toolbar;

    private WebView webView;

//    private FakeReviewManager manager;
    private ReviewManager manager;
    private ReviewInfo reviewInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_of_use);

        // Initialize the SDK:
        UnityAds.initialize (this, unityGameID, this, testMode);

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (settings.contains(DATE_OF_DEATH)) {

//            //////////For Test/////////
//            SharedPreferences.Editor editor = settings.edit();
//            AdsStatusForSoonDying = true;
//            editor.putBoolean(ADS_STATUS_FOR_SOON_DYING, AdsStatusForSoonDying);
//            editor.apply();
//            ///////////////////////////

            if (settings.contains(ADS_STATUS_FOR_SOON_DYING)) {
                AdsStatusForSoonDying = settings.getBoolean(ADS_STATUS_FOR_SOON_DYING, false);
                if(AdsStatusForSoonDying) {
                    onCreateActivityDate();
                    createInterstitialAd_For_Soon_Dying();
                    SharedPreferences.Editor editor = settings.edit();
                    AdsStatusForSoonDying = false;
                    editor.putBoolean(ADS_STATUS_FOR_SOON_DYING, AdsStatusForSoonDying);
                    editor.apply();
                }
                else onCreateActivityDate();
            }
            else onCreateActivityDate();
        } else setContentView(R.layout.terms_of_use);

        Button accept_and_continue_Btn = findViewById(R.id.accept_and_continue);
        if (accept_and_continue_Btn != null) {
            accept_and_continue_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogAcceptAndContinue();
                }
            });
        }

//        manager = new FakeReviewManager(this);
        manager = ReviewManagerFactory.create(this);
        com.google.android.play.core.tasks.Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();
            } else {
                // There was some problem, continue regardless of the result.
            }
        });
    }

//    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
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

    private void onCreateActivityDate() {
        setContentView(R.layout.activity_date);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

//        LayoutInflater inflater = LayoutInflater.from(this);
//        List<View> pages = new ArrayList<>();
//
//        View page = inflater.inflate(R.layout.activity_date, null);
//        pages.add(page);
//
//        page = inflater.inflate(R.layout.activity_date, null);
//        pages.add(page);

//        pager = findViewById(R.id.pager);
//        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
//        pager.setAdapter(pagerAdapter);
//        pager.setCurrentItem(1);
////        setContentView(pager);
//
//        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int position) {
//            }
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
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

//        Long lll = 352662060043475L;
//        final Long date_of_death = Long.parseLong(lll.toString());

        playStartSound(R.raw.countdown);

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

//        moreAppsBtn = findViewById(R.id.more_apps_button);
//        relativeLayout = findViewById(R.id.relative_layout);
//        linearLayout = findViewById(R.id.linear_layout);
//        floatingMenu = findViewById(R.id.floatingMenu);

        if (settings.contains(DATE_OF_DEATH)) {
            date_of_death = settings.getLong(DATE_OF_DEATH, 0);
        } else {
            date_of_death = getIMEI();
//            date_of_death = 352662060003475L; // for testing
            String imeiString = "";
            for (int i = 8; i < Long.toString(date_of_death).length(); i++)
                imeiString += Long.toString(date_of_death).charAt(i);

            if (Character.getNumericValue(Long.toString(date_of_death).charAt(8)) <= 1) {
                int second_number_of_timestamp = Character.getNumericValue(Long.toString(currentTime).charAt(1)) + 1;
                date_of_death = Long.parseLong("1" + second_number_of_timestamp + imeiString + "0");
            } else date_of_death = Long.parseLong("3" + imeiString + "00");

            if (date_of_death < currentTime + (3600 * 24) * 4)
                date_of_death = currentTime + ((3600L * 24) * 4) + 1111;
//            else if(date_of_death > currentTime + 1982459975L) date_of_death = currentTime + 1982459975L;

//            System.out.println("\date_of_death: " + date_of_death);
//            System.out.println("\date_of_death String: " + imeiString);
//            System.out.println("\date_of_death: " + date_of_death);

            saveTime();
        }
        timerTime = date_of_death - currentTime;

        years = timerTime / 31536000;
        days = timerTime / 86400 % 365;
        hours = timerTime / 3600 % 24;
        mins = timerTime / 60 % 60;
        secs = timerTime % 60;

        setValues();

        adsInitialization();

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
//            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    String msg = "Successfull";
//                    if(!task.isSuccessful()) {
//                        msg = "Failed";
//                    }
//                    Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show();
//                }
//            });
        
        DrawerBuilder();
    }

    private void playStartSound(int soundID) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
            mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
            MediaPlayer mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mp = MediaPlayer.create(this, soundID);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, originalVolume, 0);
                }
            });
        } else {
            MediaPlayer mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mp = MediaPlayer.create(this, soundID);
            mp.start();
        }
    }

    private void adsInitialization() {
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
    }

    private Drawer.OnDrawerItemClickListener onClicksLis = new Drawer.OnDrawerItemClickListener() {
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
            else if(drawerItem.getIdentifier() == BTN_CHAT) {
                if(user == null) {

                    // Configure Google Sign In
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .requestProfile()
                            .build();

                    // Build a GoogleSignInClient with the options specified by gso.
                    mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);

                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    return true;
                }
                else {
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);
                    return true;
                }
            }
            return false;
        }
    };

    private IDrawerItem[] initializeDrawerItems() {
        return new IDrawerItem[] {

            new DividerDrawerItem()
                    .withEnabled(true),

            new PrimaryDrawerItem()
                    .withName(R.string.our_apps)
                    .withTextColorRes(R.color.white)
                    .withIdentifier(BTN_OUR_APPS),

            new PrimaryDrawerItem()
                    .withName(R.string.color_match)
                    .withTextColorRes(R.color.white)
                    .withIcon(R.drawable.img_color_match)
                    .withIdentifier(BTN_COLOR_MATCH),

            new PrimaryDrawerItem()
                    .withName(R.string.jump_up)
                    .withTextColorRes(R.color.white)
                    .withIcon(R.drawable.img_jump_up)
                    .withIdentifier(BTN_JUMP_UP),

//            new PrimaryDrawerItem()
//                    .withName(R.string.christmas_game)
//                    .withTextColorRes(R.color.white)
//                    .withIcon(R.drawable.img_christmas_game)
//                    .withIdentifier(BTN_CHRISTMAS_GAME),

            new PrimaryDrawerItem()
                    .withName(R.string.christmas_tree)
                    .withTextColorRes(R.color.white)
                    .withIcon(R.drawable.img_christmas_tree)
                    .withIdentifier(BTN_CHRISTMAS_TREE),

            new PrimaryDrawerItem()
                    .withName(R.string.barley_break)
                    .withTextColorRes(R.color.white)
                    .withIcon(R.drawable.img_barley_break)
                    .withIdentifier(BTN_BARLEY_BREAK),

            new PrimaryDrawerItem()
                    .withName(R.string.shopping_calculator)
                    .withTextColorRes(R.color.white)
                    .withIcon(R.drawable.img_shopping_calculator)
                    .withIdentifier(BTN_SHOPPING_CALCULATOR),

            new DividerDrawerItem()
                    .withEnabled(true),

                new PrimaryDrawerItem()
                        .withName(R.string.share)
                        .withTextColorRes(R.color.white)
                        .withIcon(android.R.drawable.ic_menu_share)
                        .withIdentifier(BTN_SHARE),

                new PrimaryDrawerItem()
                        .withName(R.string.chat)
                        .withTextColorRes(R.color.white)
                        .withIcon(android.R.drawable.ic_menu_send)
                        .withIdentifier(BTN_CHAT)
        };
    }

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

        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                setContentView(R.layout.activity_wait);

//                webView = findViewById(R.id.webView);
//                webView.setWebViewClient(new MyWebViewClient());

                createInterstitialAd();
//                DisplayUnityInterstitialAd();
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

                lastRatingDay = 1000;
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(LAST_RATING_DAY, lastRatingDay);
                editor.apply();
//
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
//                startActivity(intent);
//
                com.google.android.play.core.tasks.Task<Void> flow = manager.launchReviewFlow(MainActivity.this, reviewInfo);
                flow.addOnCompleteListener(task -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            }
        });
        dialogButtonNo.setOnClickListener(v -> {
            dialog.cancel();

            Calendar cal = Calendar.getInstance();
            lastRatingDay = cal.get(Calendar.DAY_OF_MONTH);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(LAST_RATING_DAY, lastRatingDay);
            editor.apply();
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void createInterstitialAd() { //Создаём межстраничное объявление
        final InterstitialAd interstitial;

        interstitial = new InterstitialAd(MainActivity.this);
        interstitial.setAdUnitId("ca-app-pub-7528412641056592/9979660478");
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
//                showKinovoWebView();
            }
        });
    }

    private void createInterstitialAd_For_Soon_Dying() { //Создаём межстраничное объявление
        final InterstitialAd interstitial;

        interstitial = new InterstitialAd(MainActivity.this);
        interstitial.setAdUnitId("ca-app-pub-7528412641056592/7503516855");
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
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });
    }

    private void PermissionRequest() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        } else Timer();
    }

    public void ErrorReadPhoneStateDialog() {
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
                Intent intentX = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intentX.setData(uri);
                if (intentX.resolveActivity(getPackageManager()) != null) {
                    startActivity(intentX);
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
                    while (logoTimer < 60000) { // 1 минута
                        sleep(1000);
                        logoTimer += 1000;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(reviewInfo != null) {
                                DialogRating();
                            }
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
        playStartSound(R.raw.krik);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private long getIMEI() {
        long imei;
        switch (android.os.Build.VERSION.SDK_INT) {
            case Build.VERSION_CODES.M:
            case Build.VERSION_CODES.N:
            case Build.VERSION_CODES.N_MR1:
            case Build.VERSION_CODES.O:
            case Build.VERSION_CODES.O_MR1:
            case Build.VERSION_CODES.P: {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    imei = Long.parseLong(manager.getDeviceId(1));
                    return imei;
                }
                else return 852662060043475L;
            }
            case Build.VERSION_CODES.Q: {
                imei = 852662063043475L;
                return imei;
            }
            case Build.VERSION_CODES.KITKAT:{
                imei = 352662062543475L;
                return imei;
            }
            case Build.VERSION_CODES.LOLLIPOP:{
                imei = 352662062043475L;
                return imei;
            }
            case Build.VERSION_CODES.LOLLIPOP_MR1: {
                imei = 352662061043475L;
                return imei;
            }
            default: return 352662064043475L;
        }
    }

//    private long getIMEI2() {
//        long imei;
//        switch (Build.VERSION.SDK_INT) {
//            case Build.VERSION_CODES.M:
//            case Build.VERSION_CODES.N:
//            case Build.VERSION_CODES.N_MR1:
//            case Build.VERSION_CODES.O:
//            case Build.VERSION_CODES.O_MR1:
//            case Build.VERSION_CODES.P: {
//                TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//                if(manager != null) {
//                    if (manager.getDeviceId(1) != null) {
//                        imei = Long.parseLong(manager.getImei(1));
//                    } else if (manager.getDeviceId(0) != null) {
//                        imei = Long.parseLong(manager.getImei(0));
//                    } else if (manager.getImei(1) != null) {
//                        imei = Long.parseLong(manager.getDeviceId(1));
//                    } else if (manager.getImei(0) != null) {
//                        imei = Long.parseLong(manager.getDeviceId(0));
//                    }
//                }
//            }
//            case Build.VERSION_CODES.KITKAT: {
//                imei = 352662063043475L;
//                break;
//            }
//            case Build.VERSION_CODES.LOLLIPOP: {
//                imei = 352662062043475L;
//                break;
//            }
//            case Build.VERSION_CODES.LOLLIPOP_MR1: {
//                imei = 352662061043475L;
//                break;
//            }
//            case Build.VERSION_CODES.Q: {
//                imei = 852662062543475L;
//                break;
//            }
//            default: imei = 352662064043475L;
//        }
//        return imei;
//    }

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
                                    timer.cancel();
                                    return;
                                }
                            }
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

    private void updateUI() {
        if (years >= 10) tvYrs.setText("" + years);
        else tvYrs.setText("0" + years);

        if (days >= 10) tvDay.setText("" + days);
        else tvDay.setText("0" + days);

        if (hours >= 10) tvHrs.setText("" + hours);
        else tvHrs.setText("0" + hours);

        if (mins >= 10) tvMin.setText("" + mins);
        else tvMin.setText("0" + mins);

        if (secs >= 10) tvSec.setText("" + secs);
        else tvSec.setText("0" + secs);

        if (years == 0 && days == 0 && hours == 0 && mins == 0 && secs == 0) {
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
        }

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
//        FirebaseDatabase.getInstance().getReference().push().setValue(date_of_death);
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
                PermissionRequest();
            }
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        updateUI(user);
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
        super.onPause();
        System.out.println("TIMER_TIME: " + timerTime);
        System.out.println("date_of_death: " + date_of_death);

        if (timerTime > 0 && timerTime < 259200) { // Меньше 3х дней
            long timeToNotifi, yearsNow, daysNow, hoursNow, minsNow;
            yearsNow = years * 31536000;
            daysNow = days * 86400;
            hoursNow = hours * 3600;
            minsNow = mins * 60;
            timeToNotifi = yearsNow + daysNow + hoursNow + minsNow + secs;

            if(timerTime > 36000)
                timeToNotifi = timeToNotifi / 3;
            else if(timerTime > 160)
                timeToNotifi = timerTime - 160L;
            else if(timerTime > 3)
                timeToNotifi = timerTime;
            else return;

            SharedPreferences.Editor editor = settings.edit();
            AdsStatusForSoonDying = true;
            editor.putBoolean(ADS_STATUS_FOR_SOON_DYING, AdsStatusForSoonDying);
            editor.apply();

//            FirebaseDatabase.getInstance().getReference().push().setValue(timerTime);

            Intent intent = new Intent(this, Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (timeToNotifi * 1000), pendingIntent);
//            Snackbar.make(findViewById(android.R.id.content), "Alarm set in " + timeToNotifi + " seconds",Snackbar.LENGTH_LONG).show();
        }
    }

    // Implement a function to display an ad if the Placement is ready:
//    public void DisplayUnityInterstitialAd() {
//        if (UnityAds.isReady (placementId)) {
//            UnityAds.show(this, placementId);
//        }
//    }

    @Override
    public void onUnityAdsReady (String placementId) {
        // Implement functionality for an ad being ready to show.
        System.out.println("onUnityAdsReady");
    }

    @Override
    public void onUnityAdsStart (String placementId) {
        // Implement functionality for a user starting to watch an ad.
        System.out.println("onUnityAdsStart");
    }

    @Override
    public void onUnityAdsFinish (String placementId, UnityAds.FinishState finishState) {
        PermissionRequest();
    }

    @Override
    public void onUnityAdsError (UnityAds.UnityAdsError error, String message) {
        PermissionRequest();
    }

//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");
//                            updateUI(user);
//                            DrawerBuilder();

                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        Snackbar.make(findViewById(R.id.relative_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                    }
                    // ...
                });
    }

    private void DrawerBuilder() {
//        accountName = acct.getDisplayName();
//        accountEmail = acct.getEmail();
//        photoUrl = acct.getPhotoUrl();
//
//        AccountHeader accountHeader = initializeAccountHeader();
        drawerResult = new DrawerBuilder()
            .withActivity(MainActivity.this)
            .withToolbar(toolbar)
            .withSliderBackgroundColorRes(R.color.transparent)
            .withActionBarDrawerToggleAnimated(true)
            .addDrawerItems(initializeDrawerItems())
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

//    private class MyWebViewClient extends WebViewClient {
//        @TargetApi(Build.VERSION_CODES.N)
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            view.loadUrl(request.getUrl().toString());
//            return true;
//        }
//
//        // Для старых устройств
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return true;
//        }
//    }
}