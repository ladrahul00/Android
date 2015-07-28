package com.example.anonymous.adddata;

import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
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
    String date;
    private ArrayAdapter<String> lvArrayAdapter;
    int progress;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_log);

        SearchView search=(SearchView) findViewById(R.id.searchView);
        search.setQueryHint("SearchView");
        textView = (TextView)findViewById(R.id.show);
        Calendar cal = Calendar.getInstance();
        int currentDate = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, currentDate);
        String dType = "dd / MM / yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dType);
        date = sdf.format(cal.getTime());
        textView.setText("Showing Logs for "+date);
        try {
            showAll();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                try {
                    showLogs(arg0);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });

    }


    public void today(View v)throws ParseException
    {
        TextView show=(TextView)findViewById(R.id.show);
        show.setText("");
        Calendar cal = Calendar.getInstance();
        int currentDate = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, currentDate);
        String dType = "dd / MM / yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dType);
        date = sdf.format(cal.getTime());
        textView.setText("Showing Logs for "+date);
        lvArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("EmployeeLog");
        query.whereEqualTo("Date", date);
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
                    String str = "No Records found";
                    lvArrayAdapter.add(str);
                }
            }
        });
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(lvArrayAdapter);

        TextView day=(TextView)findViewById(R.id.day);
        day.setText("TODAY");
        Button today=(Button)findViewById(R.id.today);
        today.setVisibility(View.INVISIBLE);
        Button yesterday=(Button)findViewById(R.id.yesterday);
        yesterday.setVisibility(View.VISIBLE);
    }

    public void yesterday(View v)throws ParseException
    {
        TextView show=(TextView)findViewById(R.id.show);
        show.setText("");
        Calendar cal = Calendar.getInstance();
        int currentDate = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, currentDate-1);
        String dType = "dd / MM / yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dType);
        date = sdf.format(cal.getTime());
        textView.setText("Showing Logs for "+date);
        lvArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("EmployeeLog");
        query.whereEqualTo("Date", date);
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
                    String str = "No Records found";
                    lvArrayAdapter.add(str);
                }
            }
        });
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(lvArrayAdapter);

        TextView day=(TextView)findViewById(R.id.day);
        day.setText("YESTERDAY");
        Button today=(Button)findViewById(R.id.today);
        today.setVisibility(View.VISIBLE);
        Button yesterday=(Button)findViewById(R.id.yesterday);
        yesterday.setVisibility(View.INVISIBLE);
    }

    public void showAll()throws ParseException{
        lvArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("EmployeeLog");
        query.whereEqualTo("Date", date);
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

    public void showLogs(String emp) throws ParseException {
        TextView show=(TextView)findViewById(R.id.show);
        show.setText("searching for "+emp);
        lvArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        final ParseQuery<ParseObject> query1 = ParseQuery.getQuery("EmployeeLog");
        query1.whereEqualTo("EmployeeID", emp);
        query1.whereEqualTo("Date", date);
        textView.setText("Showing Logs of "+date+" for "+emp);
        //List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        //queries.add(query1);
        //queries.add(query2);

        //ParseQuery<ParseObject> query =ParseQuery.or(queries);

        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        ParseObject pobj = list.get(i);
                        String str = pobj.get("Status") + "     " + pobj.get("Time");
                        lvArrayAdapter.add(str);
                    }
                } else {
                    TextView show=(TextView)findViewById(R.id.show);
                    show.setText("");
                    String str="No Records found";
                    lvArrayAdapter.add(str);
                }
            }
        });
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(lvArrayAdapter);
        show.setText("showing for " + emp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_log, menu);
        return true;
    }
}
