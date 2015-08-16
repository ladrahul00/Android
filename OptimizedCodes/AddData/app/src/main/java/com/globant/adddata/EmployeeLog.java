package com.globant.adddata;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.os.Handler;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.LogRecord;

public class EmployeeLog extends ActionBarActivity {
    private ListView listView;
    private String date;
    private ArrayAdapter<String> lvArrayAdapter;
    private TextView textView;
    private Handler handler = new Handler();
    private String dType = "dd / MM / yyyy";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_log);
        SearchView search = (SearchView) findViewById(R.id.searchView);
        search.setQueryHint("SearchView");

        textView = (TextView) findViewById(R.id.show);

        Calendar cal = Calendar.getInstance();
        int currentDate = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, currentDate);

        SimpleDateFormat sdf = new SimpleDateFormat(dType);
        date = sdf.format(cal.getTime());

        textView.setText("Showing Logs for " + date);
        showAll();//show logs

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String arg0) {
                //search parse for emp id = arg0
                    showLogs(arg0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showAll();
                lvArrayAdapter.notifyDataSetChanged();
                handler.postDelayed(this, 10000);
            }
        }, 10 * 1000);
    }

    public void refresh(View v){
        showAll();
    }

    public void today(View v)
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
        TextView day=(TextView)findViewById(R.id.day);
        day.setText("TODAY");
        Button today=(Button)findViewById(R.id.today);
        today.setVisibility(View.INVISIBLE);
        Button yesterday=(Button)findViewById(R.id.yesterday);
        yesterday.setVisibility(View.VISIBLE);
        showAll();
    }

    public void yesterday(View v)
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
        TextView day=(TextView)findViewById(R.id.day);
        day.setText("YESTERDAY");
        Button today=(Button)findViewById(R.id.today);
        today.setVisibility(View.VISIBLE);
        Button yesterday=(Button)findViewById(R.id.yesterday);
        yesterday.setVisibility(View.INVISIBLE);
        showAll();
    }

    public void showAll(){
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

    public void showLogs(String emp) {
        TextView show=(TextView)findViewById(R.id.show);
        show.setText("searching for "+emp);
        lvArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        final ParseQuery<ParseObject> query1 = ParseQuery.getQuery("EmployeeLog");
        query1.whereEqualTo("EmployeeID", emp);
        query1.whereEqualTo("Date", date);
        textView.setText("Showing Logs of "+date+" for "+emp);
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
}
