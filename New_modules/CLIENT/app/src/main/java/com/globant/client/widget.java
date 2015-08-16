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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.Semaphore;

public class widget extends AppWidgetProvider {
    static boolean clicked=false;
    static int layoutID;
//    public static final Semaphore LOCK = new Semaphore(0);
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appwidgtid = appWidgetIds[i];
            layoutID=R.layout.widget;
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutID);
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

    public static PendingIntent buildButtonPendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.bluetooth.rec");
        //ProgressWheel pw = new ProgressWheel(context,R.id.imageView);

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, widget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

}