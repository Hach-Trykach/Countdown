package by.ddrvld.countdown;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

public class DateActivity extends Activity {

    int years, days, hours, mins = 1, secs = 60;

    TextView tvYrs, tvDay, tvHrs, tvMin, tvSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        tvYrs = findViewById(R.id.yrs);
        tvDay = findViewById(R.id.day);
        tvHrs = findViewById(R.id.hrs);
        tvMin = findViewById(R.id.min);
        tvSec = findViewById(R.id.sec);

        setValues();
    }

    private void setValues() {
        tvYrs.setText("0" + years);
        tvDay.setText("0" + days);
        tvHrs.setText("0" + hours);
        tvMin.setText("0" + mins);
        tvSec.setText("0" + secs);

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if(secs > 0) secs--;
                if(secs == 0) {
                    if(mins > 0) mins--;
                    if(mins >= 10) tvMin.setText("" + mins);
                    else tvMin.setText("0" + mins);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(secs >= 10) tvSec.setText("" + secs);
                        else tvSec.setText("0" + secs);
                    }
                });
            }
        }, 0, 1000);
    }
}