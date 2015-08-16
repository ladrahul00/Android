package com.globant.bazzinga;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.Toast;


public class NewAppWidget extends AppWidgetProvider {
    static int layoutID;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appwidgtid = appWidgetIds[i];
            SharedPreferences pref = context.getSharedPreferences("EmployeeData", 0);
            SharedPreferences.Editor editor = pref.edit();
            int state = pref.getInt("Status", Constants.OUTSIDE);
            editor.commit();
            if (state == Constants.INSIDE)
                layoutID = R.layout.new_app_widget;
            else if(state== Constants.OUTSIDE)
                layoutID=R.layout.new_app_widget_2;
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutID);
            if(state==Constants.INSIDE) {
                remoteViews.setTextViewText(R.id.textView2, "Inside");
            }
            else if(state==Constants.OUTSIDE){
                remoteViews.setTextViewText(R.id.textView2, "Outside");
            }
            remoteViews.setOnClickPendingIntent(R.id.imageButton, buildButtonPendingIntent(context));

            pushWidgetUpdate(context, remoteViews);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getService(context,0,intent,0);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static PendingIntent buildButtonPendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.bluetooth.rec");
       // Toast.makeText(context, "broadcasting", Toast.LENGTH_SHORT).show();
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, NewAppWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }
}

