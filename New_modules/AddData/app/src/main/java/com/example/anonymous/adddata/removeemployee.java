package com.example.anonymous.adddata;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


public class removeemployee extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removeemployee);
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
                            Intent intent = new Intent(removeemployee.this, DisplayActivity.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_removeemployee, menu);
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
}
