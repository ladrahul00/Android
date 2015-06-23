package com.example.anonymous.server;

/**
 * Created by payal on 21/06/2015.
 */
public class Database {

    private String _id;
    private String _checkIn;
    private String _checkOut;

    public Database(){

    }

    public Database(String empid)
    {
        this._id = empid;
    }

    public void set_id(String empid) {
        this._id = empid;
    }

    public String get_empid() {
        return _id;
    }

}