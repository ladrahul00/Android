package com.globant.client;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MyWidgetIntentReceiver extends BroadcastReceiver {
    int layoutID;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.bluetooth.rec")){
            updateWidgetName(context);
        }
    }

    private void updateWidgetName(Context context) {
        Toast.makeText(context, "U clicked",Toast.LENGTH_SHORT).show();
        layoutID=R.layout.widgetextendlayout;
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutID);
        Intent configIntent = new Intent(context.getApplicationContext(), WidgetService.class);
        context.startService(configIntent);
        remoteViews.setOnClickPendingIntent(R.id.imageButton, widget.buildButtonPendingIntent(context));
        widget.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
    }
}
