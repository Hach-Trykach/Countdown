package by.ddrvld.countdowndeathapp;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

public class WidgetUpdateService  extends Service {

    public WidgetUpdateService() {
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        updateInfoWidget();
        return super.onStartCommand(intent, flags, startId);
    }
    private void updateInfoWidget()
    {//Обновление виджета
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this.getApplicationContext().getPackageName(), UpdateWidget.class.getName()));
        UpdateWidget.updateAppWidget(this.getApplicationContext(),appWidgetManager,ids[0]);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}