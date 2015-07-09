package com.example.anonymous.adddata;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class EmployeeLog extends ActionBarActivity {
    private String empid;
    SeekBar seekBar;
    ListView listView;
    private ArrayAdapter<String> lvArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_log);
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "ecNYEdsTREI9Mwzx5gWOoh2HB9V78KvVWe8W8iIA", "YHuKHkJdjm4gSdl6lrZavY9Sdx06Da1DPNNXy40p");

        listView = (ListView)findViewById(R.id.listView);
        lvArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listView.setAdapter(lvArrayAdapter);

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progvalue, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progress=seekBar.getProgress();
                String dType = "DD / MM / YYYY";
                SimpleDateFormat sdf = new SimpleDateFormat(dType);
                String CurrentDate = sdf.format(new Date());
                ParseQuery<ParseObject> query = ParseQuery.getQuery("EmployeeLog");
                query.whereEqualTo("Date",CurrentDate);
                query.findInBackground(new FindCallBack<ParseObject>(){
                   public void done(List<ParseObject> scoreList,ParseException e){
                       if(e==null){
                           
                       }
                       else{
                           //error
                       }
                   }
                });


            }
        });
    }

    public void showLogs(View v){
        EditText EmpIDEditText = (EditText)findViewById(R.id.editText);
        String empid = EmpIDEditText.getText().toString();

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
