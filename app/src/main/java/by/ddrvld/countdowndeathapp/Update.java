package by.ddrvld.countdowndeathapp;

import android.net.Uri;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Arrays;

public class Update extends AppWidgetProvider {


    final String LOG_TAG = "myLogs";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(LOG_TAG, "onEnabled");
    }

//    @Override
//    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
//                         int[] appWidgetIds) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds));
//    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(LOG_TAG, "onDisabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int i=0; i<appWidgetIds.length; i++){
            int currentWidgetId = appWidgetIds[i];

//            //Делаем простой http запрос на указанную ссылку и выполняем по ней переход:
//            String url = "https://ddrvld.com";
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setData(Uri.parse(url));
//
//            //Определяем два обязательных объекта класса PendingIntent и RemoteViews:
//            PendingIntent pending = PendingIntent.getActivity(context, 0,intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.appwidget_provider_layout);
//
//            //Настраиваем обработку клика по добавлению виджета:
//            views.setOnClickPendingIntent(R.id.button, pending);
            views.setTextViewText(R.id.textView,"Обновился");
            appWidgetManager.updateAppWidget(currentWidgetId,views);
            Toast.makeText(context, "Виджет добавлен", Toast.LENGTH_SHORT).show();
        }
    }
}