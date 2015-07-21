package com.example.payal.add_data;

import android.app.SearchManager;
import android.content.Intent;
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
import java.util.Date;
import java.util.List;


public class EmployeeLog extends ActionBarActivity {
    private String empid;
    SeekBar seekBar;
    ListView listView;
    private ArrayAdapter<String> lvArrayAdapter;
    int progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_log);
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "ecNYEdsTREI9Mwzx5gWOoh2HB9V78KvVWe8W8iIA", "YHuKHkJdjm4gSdl6lrZavY9Sdx06Da1DPNNXy40p");
        //

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
          }
        }

    public void doMySearch(String query)
    {

    }

    public void showLogs(View v) throws ParseException {

        lvArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        String dType = "dd / MM / yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dType);
 /*       Date mDate;
        try {
            mDate = sdf.parse(dType);
        }catch(ParseException e){}
*/
        String CurrentDate = sdf.format(new Date());


        ParseQuery<ParseObject> query = ParseQuery.getQuery("EmployeeLog");
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
    }

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
