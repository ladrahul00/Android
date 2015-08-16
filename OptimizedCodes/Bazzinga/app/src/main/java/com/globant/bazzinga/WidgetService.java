package com.globant.bazzinga;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;


public class WidgetService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("EmployeeData", 0);
        SharedPreferences.Editor editor = pref.edit();
        int state = pref.getInt("Status", Constants.OUTSIDE);
        editor.commit();
        String empid = pref.getString("EmployeeIDKey", "blank");
        editor.commit();
        if (empid.equals("blank")) {
            Toast.makeText(getApplicationContext(), "Sign-In to proceed", Toast.LENGTH_SHORT).show();
            Intent intentMain = new Intent(WidgetService.this, MainActivity.class);
            this.startActivity(intentMain);
        } else {
            String msg = empid + "#" + String.valueOf(state);
            ConnectThread connectWidgetThread = new ConnectThread(msg,getApplicationContext());
            connectWidgetThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
