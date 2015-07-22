package com.example.anonymous.adddata;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class DisplayActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void view_log(View v){
        Intent intent = new Intent(DisplayActivity.this, EmployeeLog.class);
        startActivity(intent);
    }

    public void add_employee(View v){
        Intent intent = new Intent(DisplayActivity.this,addemployeee.class);
        startActivity(intent);
    }

    public void remove_employee(View v){
        Intent intent = new Intent(DisplayActivity.this,removeemployee.class);
        startActivity(intent);
    }


}


