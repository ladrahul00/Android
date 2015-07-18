package com.globant.client;

import android.app.Fragment;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.app.PendingIntent;
import android.content.ComponentName;

public class widget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appwidgtid = appWidgetIds[i];
            int layoutID;
            if(true){
                layoutID=R.layout.widget;
            }
            else{
                layoutID=R.layout.widgetextendlayout;
            }
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutID);
            remoteViews.setOnClickPendingIntent(R.id.imageButton, buildButtonPendingIntent(context));
            pushWidgetUpdate(context, remoteViews);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Intent intent = new Intent(context,CheckInOut.class);
        PendingIntent pendingIntent = PendingIntent.getService(context,0,intent,0);
    }

    public static PendingIntent buildButtonPendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.bluetooth.rec");
        //Fragment fg = new Fragment();
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, widget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

}