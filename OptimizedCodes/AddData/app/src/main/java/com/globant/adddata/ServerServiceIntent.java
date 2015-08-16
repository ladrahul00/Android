package com.globant.adddata;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ServerServiceIntent extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.anonymous.adddata.action.FOO";
    private static final String ACTION_BAZ = "com.example.anonymous.adddata.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.anonymous.adddata.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.anonymous.adddata.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ServerServiceIntent.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ServerServiceIntent.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public ServerServiceIntent() {
        super("ServerServiceIntent");
        ConnectThread mConnect = new ConnectThread();
        mConnect.start();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class ConnectThread extends Thread {
        BluetoothAdapter myBluetoothAdapter;
        private BluetoothServerSocket mmSocket;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread() {
            mmSocket=null;
        }

        public void run() {
            myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            myBluetoothAdapter.enable();
            //enable bluetooth
            while(!myBluetoothAdapter.isEnabled());
            //check if bluetooth is enabled
            BluetoothServerSocket temp = null;

            try {
                temp = myBluetoothAdapter.listenUsingRfcommWithServiceRecord("Server", MY_UUID);
            }
            catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Socket not created", Toast.LENGTH_SHORT).show();
            }

            mmSocket = temp;

            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = mmSocket.accept();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"Socket connection failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (socket != null) {
                    ConnectedThread mConnection = new ConnectedThread(socket);
                    mConnection.start();
                }
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectedThread extends Thread {
        private InputStream mmInStrteam;
        private OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;
        private String empid;
        private String status;
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;
            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStrteam = tempIn;
            mmOutStream = tempOut;
        }

        void parseMsg(String msg){
            char m[]=msg.toCharArray();
            char emparray[] = new char [100];
            char st='\0';
            for (int i=0;i<m.length;i++){
                if(m[i]=='#'){
                    st=m[i+1];
                    break;
                }
                else
                    emparray[i]=m[i];
            }
            empid=String.valueOf(emparray);
            if(st=='0')
                status="OUT";
            else
                status="IN";
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes = 0;
            while (true) {
                try {
                    bytes = mmInStrteam.read(buffer);
                    String m = new String(buffer);
                    parseMsg(m);
                    ParseObject testObject = new ParseObject("EmployeeLog");
                    testObject.put("EmployeeID", empid);

                    String dType = "dd / MM / yyyy";
                    String tType = "HH:mm:ss";

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dType);
                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(tType);

                    String date = simpleDateFormat.format(new Date());
                    String time = simpleTimeFormat.format(new Date());

                    testObject.put("Date",date);
                    testObject.put("Time",time);
                    testObject.put("Status",status);
                    testObject.saveEventually();

                    byte [] xyz = "Successful".getBytes();

                    try {
                        mmOutStream.write(xyz);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    break;
                }
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

}
