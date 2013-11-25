package org.obdreader.activity;

import java.util.HashMap;
import java.util.Map;

import org.obdreader.R;
import org.obdreader.io.ObdReaderService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ObdReaderMainActivity extends Activity {
	static final int NO_BLUETOOTH_ID = 0;
	static final int NO_GPS_ID = 1;
	static final int SETTINGS = 3;
	static final int STOP = 4;
	static final int START = 5;
	static final int TABLE_ROW_MARGIN = 6;
	static final int CONFIG_ACTIVITY_RESULT = 1;
	private static final int REQUEST_ENABLE_BT = 1;

	public final static String EXTRA_NAME = "org.obdreader.ObdCommandDescription";
	public final static String EXTRA_VALUE = "org.obdreader.ObdCommandValue";
	public final static String EXTRA_RESULT = "org.obdreader.ObdCommandResult";
	public final static String EXTRA_STATUS = "org.obdreader.ObdStatus";

	protected TextView statusTxtView;

	protected Map<String,TextView> dataTable = null;
	protected IntentFilter updateFilter = null;
	protected IntentFilter statusFilter = null;
	private final BroadcastReceiver updateRecv = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			String desc = extras.getString(EXTRA_NAME);
			String res = extras.getString(EXTRA_RESULT);
			updateDataTable(desc, res);
		}
	};
	
	protected final BroadcastReceiver statusRecv = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String msg = intent.getExtras().getString(EXTRA_STATUS);
			statusTxtView.setText(msg);
		}
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
        	Toast.makeText(this, "No GPS available, ensure it is enabled in phone settings.", Toast.LENGTH_LONG).show();
        	return;
        }
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
        	Toast.makeText(this, "No bluetooth available, ensure it is enabled in phone settings", Toast.LENGTH_LONG).show();
        	return;
        }
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
        dataTable = new HashMap<String, TextView>();
        setContentView(R.layout.main);
        statusTxtView = (TextView)findViewById(R.id.status_label);
        startService();
        updateFilter = new IntentFilter(ObdReaderService.UPDATE_INTENT);
        statusFilter = new IntentFilter(ObdReaderService.STATUS_INTENT);
        registerReceiver(updateRecv, updateFilter);
        registerReceiver(statusRecv, statusFilter);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	unregisterReceiver(updateRecv);
    	unregisterReceiver(statusRecv);
    }
    
    protected void onResume() {
    	super.onResume();
		registerReceiver(updateRecv, updateFilter);
		registerReceiver(statusRecv, statusFilter);
    }
    
    private void updateConfig() {
    	Intent configIntent = new Intent(this,ObdReaderConfigActivity.class);
    	startActivityForResult(configIntent,CONFIG_ACTIVITY_RESULT);
    }
    
    @Override
    protected void onActivityResult(int code, int result, Intent data) {
    	Log.e("result",code + ", " + result);
    	if (code == CONFIG_ACTIVITY_RESULT) {
    		stopService();
    		startService();
    	} else if (code == REQUEST_ENABLE_BT) {
			if (result != Activity.RESULT_OK) {
				Toast.makeText(this,
						"User decided not to enable Bluetooth - exiting...",
						Toast.LENGTH_LONG).show();
				finish();
			}
		}
    }
    
    protected void stopService() {
    	stopService(new Intent(this,ObdReaderService.class));
    }
    
    protected void startService() {
    	startService(new Intent(this,ObdReaderService.class));
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, SETTINGS, 0, "Settings");
        menu.add(0, STOP, 0, "Stop");        
        menu.add(0, START, 0, "Start");
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case SETTINGS:
        	updateConfig();
        	return true;
        case STOP:
        	stopService();
        	return true;
        case START:
        	startService();
        	return true;
        }
        return false;
    }
    
    protected Dialog onCreateDialog(int id) {
    	AlertDialog.Builder build = new AlertDialog.Builder(this);
    	switch(id) {
    	case NO_BLUETOOTH_ID:
            build.setMessage("Sorry, your device doesn't support bluetooth");
            return build.create();
    	case NO_GPS_ID:
            build.setMessage("Sorry, your device doesn't support gps");
            return build.create();
    	}
    	return null;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
    	MenuItem settingsItem = menu.findItem(SETTINGS);
    	MenuItem startItem = menu.findItem(START);
    	MenuItem stopItem = menu.findItem(STOP);
   		settingsItem.setEnabled(true);
   		if (isServiceRunning(this, ObdReaderService.class.getName())) {
   			stopItem.setEnabled(true);
   			startItem.setEnabled(false);
   		} else {
   			stopItem.setEnabled(false);
   			startItem.setEnabled(true);
   		}
    	return true;
    }
    
    public void updateDataTable(String desc, String val) {
    	TableLayout tl = (TableLayout)findViewById(R.id.data_table);
    	if (!dataTable.containsKey(desc)) {
    		TextView txt = addTableRow(tl,desc,val);
    		dataTable.put(desc, txt);
    	} else {
    		TextView txt = dataTable.get(desc);
    		txt.setText(val);
    	}
    }
    
    private TextView addTableRow(TableLayout tl, String key, String val) {
    	TableRow tr = new TableRow(this);
		MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
				LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
		params.setMargins(TABLE_ROW_MARGIN,TABLE_ROW_MARGIN,TABLE_ROW_MARGIN,TABLE_ROW_MARGIN);
		tr.setLayoutParams(params);
		tr.setBackgroundColor(Color.BLACK);
		TextView name = new TextView(this);
		name.setGravity(Gravity.RIGHT);
		name.setText(key + ": ");
		TextView value = new TextView(this);
		value.setGravity(Gravity.LEFT);
		value.setText(val);
		tr.addView(name);
		tr.addView(value);
		tl.addView(tr,new TableLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
		return value;
    }
    
	public static boolean isServiceRunning(Context ctx, String className) {
		ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (className.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
	}
}
