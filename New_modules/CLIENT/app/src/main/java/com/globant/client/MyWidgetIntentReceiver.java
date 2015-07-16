package com.globant.client;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;



public class MyWidgetIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.bluetooth.rec")){
            updateWidgetName(context);
        }
    }

    private void updateWidgetName(Context context) {
        Toast.makeText(context, "U clicked",Toast.LENGTH_SHORT).show();

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent configIntent = new Intent(context.getApplicationContext(), WidgetService.class);
        context.startService(configIntent);

        remoteViews.setOnClickPendingIntent(R.id.imageButton, widget.buildButtonPendingIntent(context));
        widget.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
    }
}
