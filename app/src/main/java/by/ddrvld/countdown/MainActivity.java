package by.ddrvld.countdown;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {
    static SharedPreferences settings;
    static final String APP_PREFERENCES = "settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (settings.contains(DateActivity.YEARS) || settings.contains(DateActivity.DAYS) || settings.contains(DateActivity.HOURS) || settings.contains(DateActivity.MINS) || settings.contains(DateActivity.SECS)) {
            finish();
            Intent intent = new Intent(MainActivity.this, DateActivity.class);
            startActivity(intent);
        }
        else Dialog();
    }

    private void Dialog() {
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
                finish();
                Intent intent = new Intent(MainActivity.this, WaitActivity.class);
                startActivity(intent);
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

}
