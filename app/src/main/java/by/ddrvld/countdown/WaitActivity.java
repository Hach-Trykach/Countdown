package by.ddrvld.countdown;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WaitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        Thread logoTimer = new Thread() {
            int logoTimer = 0;
            public void run() {
                try {
                    while (logoTimer < 5000) {
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
        logoTimer.start();
    }
}
