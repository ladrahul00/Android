package com.example.anonymous.client2706;

import com.example.anonymous.client2706.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by shivang on 7/1/2015.
 */
public class MyWidgetIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("pl.looksok.intent.action.CHANGE_PICTURE")){
            updateWidgetPictureAndButtonListener(context);
        }
    }

    private void updateWidgetPictureAndButtonListener(Context context) {
        Toast.makeText(context, "u clicked",Toast.LENGTH_SHORT).show();
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

         //code to be entered


        remoteViews.setOnClickPendingIntent(R.id.widget_button, widget.buildButtonPendingIntent(context));
        widget.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
    }

}
