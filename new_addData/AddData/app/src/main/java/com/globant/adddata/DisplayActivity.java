package com.globant.adddata;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

public class DisplayActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
    }

    public void view_log(View v){
        Intent EmployeeLogIntent = new Intent(DisplayActivity.this, EmployeeLog.class);
        startActivity(EmployeeLogIntent);
    }

    public void add_employee(View v){
        Intent AddEmployeeIntent = new Intent(DisplayActivity.this,AddEmployee.class);
        startActivity(AddEmployeeIntent);
    }

    public void remove_employee(View v){
        Intent RemoveEmployeeIntent = new Intent(DisplayActivity.this,RemoveEmployee.class);
        startActivity(RemoveEmployeeIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
