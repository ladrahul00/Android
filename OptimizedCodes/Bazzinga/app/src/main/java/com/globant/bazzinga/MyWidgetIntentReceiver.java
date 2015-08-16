package com.globant.bazzinga;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MyWidgetIntentReceiver extends BroadcastReceiver {
    public MyWidgetIntentReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       // Toast.makeText(context, "Broadcast Received", Toast.LENGTH_SHORT).show();
        if(intent.getAction().equals("android.bluetooth.rec")){
            try {
                updateWidgetName(context);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateWidgetName(Context context) throws InterruptedException {
       // Toast.makeText(context, "U clicked", Toast.LENGTH_SHORT).show();
        Intent configIntent = new Intent(context.getApplicationContext(), WidgetService.class);
        context.startService(configIntent);
/*
        int layoutID=R.layout.new_app_widget;
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutID);
        remoteViews.setOnClickPendingIntent(R.id.imageButton, NewAppWidget.buildButtonPendingIntent(context));
        //Intent intent = new Intent(context,MainActivity.class);
       // PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
        //remoteViews.setOnClickPendingIntent(R.id.button,pendingIntent);
        NewAppWidget.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
*/
    }


}
