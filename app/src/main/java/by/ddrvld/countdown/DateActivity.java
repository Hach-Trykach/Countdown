package by.ddrvld.countdown;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class DateActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        Dialog();
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
            }
        });
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

}
