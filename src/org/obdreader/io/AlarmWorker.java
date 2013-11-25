package org.obdreader.io;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public abstract class AlarmWorker implements LocationListener {

	public static final long GPS_UPDATE_TIME = 0;
	public static final float GPS_UPDATE_DIST = 0.0f;
	
	public final static String LATITUDE = "Latitude";
	public final static String LONGITUDE = "Longitude";
	public final static String HEADING = "Heading";
	public final static String GPS_SPEED = "GPS Speed";
	public final static String GPS_TIME = "GPS Time";

	protected Location location;
	protected double lat;
	protected double lon;
	protected double speed;
	protected double heading;
	protected double gpsTime;
	protected Context ctx;
	protected PendingIntent alarmManPendingIntent;
	protected AlarmManager alarmMan;
	protected int periodSec;
	protected boolean started;
	protected LocationManager locationMan;
	protected boolean sendGpsUpdates;
	
	public AlarmWorker(Context ctx, int periodSec, boolean sendGpsUpdates) {
		this.ctx = ctx;
		this.periodSec = periodSec;
		this.sendGpsUpdates = sendGpsUpdates;
		alarmMan = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
		locationMan = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
		Intent alarmManIntent = new Intent();
		alarmManIntent.setAction(getAlarmIntent());
		alarmManPendingIntent = PendingIntent.getBroadcast(ctx,
				0,
				alarmManIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		started = false;
	}

	public abstract String getAlarmIntent();
	public abstract void handleAlarm(Intent intent, AlarmHandledCallback callback);

	protected final BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				handleAlarm(intent, new AlarmHandledCallback());
			} catch (Exception e) {
				Log.e("obdreader","alarm handler failed");
				e.printStackTrace();
			}
		}
	};

	public void start() {
		updatePrefs();
		locationMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_TIME, GPS_UPDATE_DIST, this);
		IntentFilter alarmFilter = new IntentFilter(getAlarmIntent());
		ctx.registerReceiver(alarmReceiver, alarmFilter);
		schedule();
		started = true;
	}
	
	public void stop() {
		ctx.unregisterReceiver(alarmReceiver);
		try {
			alarmMan.cancel(alarmManPendingIntent);
		} catch (Exception e) {
		}
		locationMan.removeUpdates(this);
		started = false;
	}

	protected void schedule() {
		long ms = System.currentTimeMillis() + (periodSec * 1000);
		alarmMan.set(AlarmManager.RTC_WAKEUP, 
				ms,
				alarmManPendingIntent);
	}

	public boolean isStarted() {
		return started;
	}

	protected void updatePrefs() {
		
	}

	protected void sendGpsUpdate() {
		if (sendGpsUpdates) {
			ObdReaderAlarmWorker.sendUpdate(ctx, LATITUDE, Double.toString(lat), String.format("%.5f", lat));
			ObdReaderAlarmWorker.sendUpdate(ctx, LONGITUDE, Double.toString(lon), String.format("%.5f", lon));
			ObdReaderAlarmWorker.sendUpdate(ctx, HEADING, Double.toString(heading), String.format("%.0f", heading));
			ObdReaderAlarmWorker.sendUpdate(ctx, GPS_SPEED, Double.toString(speed), String.format("%.2f km/h", speed));
			ObdReaderAlarmWorker.sendUpdate(ctx, GPS_TIME, Long.toString((long)gpsTime), Long.toString((long)gpsTime));
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location == null) {
			return;
		}
		this.location = location;
		if (location.getLatitude() == 0.0 ||
				location.getLongitude() == 0.0) {
			lat = ObdReader.FILL_DOUBLE;
			lon = ObdReader.FILL_DOUBLE;
			speed = ObdReader.FILL_DOUBLE;
			heading = ObdReader.FILL_DOUBLE;
			gpsTime = ObdReader.FILL_DOUBLE;
			sendGpsUpdate();
			return;
		}
		lat = location.getLatitude();
		lon = location.getLongitude();
		heading = location.getBearing();
		speed = location.getSpeed() * 3.6;
		gpsTime = location.getTime() / 1000;
		sendGpsUpdate();
	}
	@Override
	public void onProviderDisabled(String provider) {
	}
	@Override
	public void onProviderEnabled(String provider) {
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	protected class AlarmHandledCallback {
		public void alarmHandled() {
			schedule();
		}
	}
}
