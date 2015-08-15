package com.example.anonymous.adddata;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.ParseObject;

public class AddEmployee extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
    }

    public void addData(View v) {
        ParseObject pobj = new ParseObject("EmployeeData");
        EditText empid = (EditText)findViewById(R.id.EmployeeID);
        EditText macaddress = (EditText)findViewById(R.id.macadd);
        EditText cardno = (EditText)findViewById(R.id.cardno);
        EditText empname = (EditText)findViewById(R.id.empname);
        String empNameStr = empname.getText().toString();
        String empIdStr = empid.getText().toString();
        String empCardNo = cardno.getText().toString();
        String empMacAddress = macaddress.getText().toString();
        if(empNameStr.equals("") || empIdStr.equals("") || empCardNo.equals("") || empMacAddress.equals("")){
            Toast.makeText(getApplicationContext(), "All Employee Details not Entered!", Toast.LENGTH_SHORT).show();
        }
        else {
            pobj.put("EmployeeName", empNameStr);
            pobj.put("EmployeeID", empIdStr);
            pobj.put("CardNo", empCardNo);
            pobj.put("MacAddress", empMacAddress);
            pobj.saveInBackground();
            Toast.makeText(getApplicationContext(), "Employee Added to Database", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DisplayActivity.class);
            startActivity(intent);
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
        super.onResume();
    }
}
