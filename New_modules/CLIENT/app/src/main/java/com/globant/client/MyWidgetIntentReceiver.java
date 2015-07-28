package com.globant.client;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.concurrent.Semaphore;

public class MyWidgetIntentReceiver extends BroadcastReceiver {
    int layoutID;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.bluetooth.rec")){
            try {
                updateWidgetName(context);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateWidgetName(Context context) throws InterruptedException {
        Toast.makeText(context, "U clicked",Toast.LENGTH_SHORT).show();
        Intent configIntent = new Intent(context.getApplicationContext(), WidgetService.class);
        context.startService(configIntent);
        //widget.LOCK.acquire();
        layoutID=R.layout.widgetextendlayout;
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutID);
        Intent intent = new Intent(context,MainActivityClient.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
        remoteViews.setOnClickPendingIntent(R.id.button,pendingIntent);
        widget.pushWidgetUpdate(context.getApplicationContext(), remoteViews);

        //layoutID=R.layout.widget;
        //RemoteViews remoteViews1 = new RemoteViews(context.getPackageName(), layoutID);
//        remoteViews.setOnClickPendingIntent(R.id.imageButton, widget.buildButtonPendingIntent(context));

        //widget.pushWidgetUpdate(context.getApplicationContext(), remoteViews1);



    }
}
