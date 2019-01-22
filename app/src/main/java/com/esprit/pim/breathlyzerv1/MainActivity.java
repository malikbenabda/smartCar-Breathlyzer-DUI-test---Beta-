package com.esprit.pim.breathlyzerv1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.esprit.pim.breathlyzerv1.Adapters.DeviceListAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


/**
 * Created by Cyber info on 12/4/2015.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DOMOTIQUE"; //Debugging Tag


    Handler h;
    String output;
    private Button mPairBtn;

    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;
    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "00:12:03:10:70:12";

    String receive;


    private TextView AState;
    private String alcoholThres;
    private String password;
    Button btn1;
    private Toolbar mToolbar;
    private String alcovalue;
    private TextView SoberS;
    public static String txtkey = "lockstate";
    public InputStream mmInStream;
    public OutputStream mmOutStream;
    SharedPreferences sharedPref;
    private byte[] msgBuffer2;
    public String limitnum;
    public String receivedm;
    public String msgLimit = "T";
    public double bac;
    private ListView mListView;
    private DeviceListAdapter mAdapter;
    private ArrayList<BluetoothDevice> mDeviceList;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public String lockstate = "";
    double bacc = 0.04;
    public ProgressBar _progressBar;
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
private Random rand;
    public ConnectedThread connectedThread;
    String tag = "debugging";
TextView alcoholvalue;
    TextView alcoholthres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        Log.d(TAG, "incomingString: " + strIncom.toString() + " ////");// create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\n");
                        Log.d(TAG, "incoming: " + sb.toString() + " ---...");
                        // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                            sb.delete(0, sb.length());                                      // and clear
                            //output is the value of a string read from arduino
                            output = sbprint;
                            // remove textview
                            receive = output;
                            //  showToast("rec : " + receive);
                            showToast(receive);
                            if (receive.substring(0,1).equals("V")){
                               Double val = Double.parseDouble(receive.substring(1)) /1000 ;
                                alcoholvalue.setText(val+"");
                                _progressBar.setProgress(val.intValue());
                            }  if (receive.substring(0,1).equals("M")){
                                Double val = Double.parseDouble(receive.substring(1)) /1000 ;
                                alcoholthres.setText(val+"");
                                alcovalue=val.toString();
                                _progressBar.setProgress(val.intValue());
                            } if (receive.substring(0,1).equals("T")){
                                Double val = Double.parseDouble(receive.substring(1)) /1000 ;
                                alcoholThres = val.toString();
                                sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor edt = sharedPref.edit();
                                edt.putString("ALTHRES", val.toString());
                                edt.commit();
                            }
                            ((TextView) findViewById(R.id.astate)).setText(" ");
                            if (receive.equals("SSOBER")) {
showToast("sobbeeeerr");
                                    SoberS.setText("Sober");
                                    SoberS.setVisibility(View.VISIBLE);
                                    AState.setText("Car Unlocked");
                                    btn1.setText("ACTIVATE DRINKING MODE");
                                }
                            if (receive.equals("SDRUNK")) {
                                   SoberS.setText("Drunk ");
                                  SoberS.setVisibility(View.VISIBLE);

                            }


                            Log.d(TAG,receive);
                        }

                        break;
                }
            }

            ;
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
        SoberS = (TextView) findViewById(R.id.sober);
        alcoholthres = (TextView) findViewById(R.id.alcthresholdernum);
        AState = (TextView) findViewById(R.id.astate);
        btn1 = (Button) findViewById(R.id.button2);
        alcoholvalue = (TextView) findViewById(R.id.textView1);
        Button blue = (Button) findViewById(R.id.pair);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (receive != null) {
            double percentage = Integer.parseInt(receive) / 1023.0;
            bac = percentage * 0.21; //bac calculate alcohol value according to BAC formula
        }


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        _progressBar = (ProgressBar) findViewById(R.id.circularProgressBar);






// as 60 is max, we specified in the xml layout, 30 will be its half ;)


        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Welcome to Breathalyzer", Snackbar.LENGTH_LONG)
                .setAction(null, null);

        View vv = snack.getView();
        vv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) vv.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) vv.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();




        btn1.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View v) {

                                        if (btAdapter.isEnabled()) {
                                            String input = "00000000L";
                                            mConnectedThread.write(input);
                                            mConnectedThread.write(input);
                                            showToast("locked");
                                            btn1.setText("Activated Drinking Mode");
                                            ((TextView) findViewById(R.id.astate)).setText("Car Locked");
                                            String txt = "Car Locked";
                                            Intent i = new Intent(MainActivity.this, ActivateScreen.class);
                                            i.putExtra(txtkey, txt);
                                            startActivity(i);
                                        } else {
                                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Please Pair your device before hand!!", Snackbar.LENGTH_LONG)
                                                    .setAction(null, null);

                                            View vv = snack.getView();
                                            vv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                            ((TextView) vv.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                                            ((TextView) vv.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

                                            snack.show();
                                            //Prompt user to turn on Bluetooth
                                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                            startActivityForResult(enableBtIntent, 1);
                                            onResume();
                                        }
                                    }
                                }

        );


        blue.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View v) {

                                        if (btAdapter.isEnabled()) {
                                            mConnectedThread.write("00000000T");
                                        } else {
                                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Please Pair your device before hand!!", Snackbar.LENGTH_LONG)
                                                    .setAction(null, null);

                                            View vv = snack.getView();
                                            vv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                            ((TextView) vv.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                                            ((TextView) vv.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

                                            snack.show();
                                            //Prompt user to turn on Bluetooth
                                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                            startActivityForResult(enableBtIntent, 1);
                                            onResume();
                                        }
                                    }
                                }

        );
    }


    public void tres(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            PreferenceManager.setDefaultValues(MainActivity.this, R.xml.activity_setting, false);
            startActivity(new Intent(MainActivity.this, SettingActivity.class));


        }
        if (id == R.id.action_map) {
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class});
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice("00:12:03:10:70:12");

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        Log.d(TAG, "device" + device.getName());
        Log.d(TAG, "device ad" + device.getAddress());
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Your Phone is now paired with Breathalyzer", Snackbar.LENGTH_LONG)
                    .setAction(null, null);

            View vv = snack.getView();
            vv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            ((TextView) vv.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
            ((TextView) vv.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

            snack.show();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
//            errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e.getMessage() + ".");

        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        Log.d(TAG, "...Connect thread ini...");

        mConnectedThread.start();
        Log.d(TAG, "...connectedthread invoked...");

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();

            try {
                mmOutStream.write(msgBuffer);
                Log.d(TAG, "...Data is sent: " + message + "...");
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }
}