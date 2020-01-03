package by.ddrvld.countdownapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class WaitActivity extends Activity {

    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        final Thread logoTimer = new Thread() {
            int logoTimer = 0;
            public void run() {
                try {
                    while (logoTimer < 1000) {
                        sleep(100);
                        logoTimer = logoTimer + 100;
                    }
                    Intent intent = new Intent(WaitActivity.this, DateActivity.class);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };

        //Создаём межстраничное объявление
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-7528412641056592/9234282990");
        AdRequest adRequesti = new AdRequest.Builder()
                .addTestDevice("2915B28E56B33B9CC3D2C5D421E9FE3E")
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
                logoTimer.start();
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
                logoTimer.start();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                logoTimer.start();
            }
        });
    }
}
