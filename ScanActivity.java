package com.example.faridam.howzit;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.faridam.howzit.AccountActivity;
import com.example.faridam.howzit.MyRecyclerViewAdapter;
import com.example.faridam.howzit.QrReaderActivity;

import java.util.ArrayList;
import java.util.Set;


public class ScanActivity extends AppCompatActivity  implements android.support.v7.widget.PopupMenu.OnMenuItemClickListener ,MyRecyclerViewAdapter.ItemClickListener {

    private static final String TAG = "DeviceListActivity";
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int TIME_DELAY = 1000;
    private static long back_pressed;
    MyRecyclerViewAdapter adapter;

    ArrayList<User> arrayListAdapter = new ArrayList<>();

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayAdapter<String> mNewDevicesArrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        // Setup the window
        requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
        setContentView( R.layout.activity_scan );
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Intent intent = new Intent();
        String mdevice = bluetoothAdapter.getAddress();

        // Set result CANCELED in case the user backs out
        setResult( Activity.RESULT_CANCELED );

        //ImageButton scanButton = findViewById( R.id.scan_button );
        final Button scanButton = findViewById( R.id.scan_button );
        scanButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                //doDiscovery();
                //v.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(ScanActivity.this, R.anim.sample_anim);
                scanButton.startAnimation(animation);
                startActivity( new Intent( ScanActivity.this, AccountActivity.class ) );
            }
        } );

        ArrayAdapter<String> pairedDevicesArrayAdapter = new ArrayAdapter<String>( this, R.layout.device_name );

        TextView textView = findViewById(R.id.contact_textview);
        if(QrReaderActivity.names.size()>0)
            textView.setText("contacts:");

        arrayListAdapter = MainActivity.db.getAllContacts(MainActivity.macAdd);

        RecyclerView recyclerView = (RecyclerView) findViewById( R.id.contact_re );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        adapter = new MyRecyclerViewAdapter( this, DatabaseHelper.contactNames);
        adapter.setClickListener( this );
        recyclerView.setAdapter( adapter );

    }


    @Override
    public void onBackPressed() {
        if(back_pressed + TIME_DELAY > System.currentTimeMillis()){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this , "press once again to exit" ,Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
        //super.onBackPressed();
    }
    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        //findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    /**
     * The on-click listener for all devices in the ListViews
     */
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */


    @Override
    public void onItemClick(View view, int position) {
        startActivity(new Intent(ScanActivity.this , ConnectionActivity.class));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.account:
                //do what yoiu want to do

                break;
            /*case R.id.clear_contacts:
                //do what you want
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPopUp(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.scan_menu, popupMenu.getMenu());
        popupMenu.show();
        /*popupMenu.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popupMenu.inflate(R.menu.scan_menu);
        popupMenu.show();*/
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.account:
                //do what yoiu want to do

                return true;
           /* case R.id.clear_contacts:
                //do what you want
                return true;*/
        }
        return false;
    }

    /*@Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.account:
                //do what you want to do

                return true;
           /* case R.id.clear_contacts:
                //do what you want
                return true;*/
    // }
    //   return false;



}