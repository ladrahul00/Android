package com.globant.adddata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.List;

public class RemoveEmployee extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_employee);
        Intent adminLoginIntent = new Intent(this,AdminLogin.class);
        startActivity(adminLoginIntent);
    }

    public void removeData(View v){
        EditText editText = (EditText)findViewById(R.id.EmployeeID);
        String empidString = editText.getText().toString();
        if(empidString.equals("")){
            Toast.makeText(getApplicationContext(), "Enter Employee Id", Toast.LENGTH_SHORT).show();
        }
        else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("EmployeeData");
            query.whereEqualTo("EmployeeID", empidString);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < list.size(); i++) {
                            ParseObject pobj = list.get(i);
                            pobj.deleteEventually();
                            Toast.makeText(getApplicationContext(), "Employee Removed from Database", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RemoveEmployee.this, DisplayActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Employee Not found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
        setContentView(R.layout.activity_remove_employee);
        super.onResume();
    }
}
