package org.obdreaderv2.io;

import org.obdreaderv2.R;
import org.obdreaderv2.activity.ObdReaderConfigActivity;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class ObdReaderService extends Service {

	public static final int COMMAND_ERROR_NOTIFY = 2;
	public static final int CONNECT_ERROR_NOTIFY = 3;
	public static final int OBD_SERVICE_RUNNING_NOTIFY = 4;
	public static final int OBD_SERVICE_ERROR_NOTIFY = 5;
	
	public static final String UPDATE_INTENT = "org.obdreader.io.ObdReaderService.UPDATE";
	public static final String STATUS_INTENT = "org.obdreader.io.ObdReaderService.STATUS";
	public final static String OBD_ALARM_INTENT = "org.obdreader.io.ObdReaderService.OBD_ALARM_INTENT";

	private Intent notificationIntent = null;
	private PendingIntent contentIntent = null;
	protected ObdReaderAlarmWorker obdWorker = null;
	protected ObdPersistAlarmWorker persistWorker = null;
	protected int obdCyclePeriod;
	protected int persistCyclePeriod;
	protected boolean started;

	@Override
	public void onCreate() {
		super.onCreate();
		notificationIntent = new Intent(this, ObdReaderService.class);
		contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		updatePrefs();
		obdWorker = new ObdReaderAlarmWorker(this, obdCyclePeriod);
		persistWorker = new ObdPersistAlarmWorker(this, persistCyclePeriod);
		started = false;
	}

	public void updatePrefs() {
		obdCyclePeriod = ObdReaderConfigActivity.getUpdatePeriod(this);
		persistCyclePeriod = ObdReaderConfigActivity.getPersistPeriod(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (started) {
			return START_STICKY;
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String devString = prefs.getString(ObdReaderConfigActivity.BLUETOOTH_LIST_KEY, null);
		final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
    		Toast.makeText(this, "Bluetooth not available, make sure it is enabled under system settings", Toast.LENGTH_LONG).show();
    		stopSelf();
        	return START_STICKY;
        }
    	if (devString == null || "".equals(devString)) {
    		Toast.makeText(this, "No bluetooth device selected, please edit application settings", Toast.LENGTH_LONG).show();
			stopSelf();
			return START_STICKY;
		}
    	LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
        	Toast.makeText(this, "This device does not support GPS", Toast.LENGTH_LONG).show();
        	stopSelf();
        	return START_STICKY;
        }

        obdWorker.start();
        persistWorker.start();
        
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(R.drawable.car, "OBD Service Running", when);
		notification.setLatestEventInfo(getApplicationContext(), "OBD Service Running", "", contentIntent);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		startForeground(OBD_SERVICE_RUNNING_NOTIFY, notification);

        started = true;
        return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			obdWorker.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			persistWorker.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		stopSelf();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
