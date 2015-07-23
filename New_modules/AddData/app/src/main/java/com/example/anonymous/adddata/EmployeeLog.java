package com.example.anonymous.adddata;

import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class EmployeeLog extends ActionBarActivity {
    private String empid;
    ListView listView;
    private ArrayAdapter<String> lvArrayAdapter;
    int progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_log);
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "ecNYEdsTREI9Mwzx5gWOoh2HB9V78KvVWe8W8iIA", "YHuKHkJdjm4gSdl6lrZavY9Sdx06Da1DPNNXy40p");

        SearchView search=(SearchView) findViewById(R.id.searchView);
        search.setQueryHint("SearchView");

 /*               //*** setOnQueryTextFocusChangeListener ***
                search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
                {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus)
                    {
                        Toast.makeText(getApplicationContext(), "yo yo", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getBaseContext(), String.valueOf(hasFocus),
                                Toast.LENGTH_SHORT).show();
                    }
                });
*/

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String arg0) {
                //search parse for emp id = arg0


                return true;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {

                return false;
            }
        });


        Calendar cal = Calendar.getInstance();
        int currentDate = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, currentDate);
        String dType = "dd / MM / yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dType);
        String CurrentDate = sdf.format(cal.getTime());
        TextView textView = (TextView)findViewById(R.id.textView3);
        textView.setText(CurrentDate);
    }

   /* public void showLogs(View v) throws ParseException {

        lvArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        Calendar cal = Calendar.getInstance();
        int currentDate = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, currentDate - k);


        String dType = "dd / MM / yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dType);
        String CurrentDate = sdf.format(cal.getTime());
        TextView textView = (TextView)findViewById(R.id.textView3);
        textView.setText(CurrentDate);

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("EmployeeLog");
        query.whereEqualTo("Date", CurrentDate);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        ParseObject pobj = list.get(i);
                        String str = pobj.get("EmployeeID").toString() + "      " + pobj.get("Status") + "     " + pobj.get("Time");
                        lvArrayAdapter.add(str);
                    }
                } else {
                    String str="No Records found";
                    lvArrayAdapter.add(str);
                }
            }
        });
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(lvArrayAdapter);
    } */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_log, menu);
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
