package com.example.anonymous.client2706;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.widget.Toast;
import android.view.View;
/**
 * Implementation of App Widget functionality.
 */
public class widget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appwidgtid = appWidgetIds[i];

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
                Intent configIntent = new Intent(context.getApplicationContext(), WidgetService.class);
                configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appwidgtid);
                PendingIntent pIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.widget_button, pIntent);
                //remoteViews.setOnClickFillInIntent();
                // remoteViews.setOnClickPendingIntent(R.id.widget_button, buildButtonPendingIntent(context));
            context.startService(configIntent);
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
        intent.setAction("pl.looksok.intent.action.CHANGE_PICTURE");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, widget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}


