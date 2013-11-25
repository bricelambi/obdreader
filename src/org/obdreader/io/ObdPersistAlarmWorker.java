package org.obdreader.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.obdreader.activity.ObdReaderConfigActivity;
import org.obdreader.activity.ObdReaderMainActivity;
import org.obdreader.db.ObdDatabaseHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class ObdPersistAlarmWorker extends AlarmWorker {

	public final static String OBD_PERSIST_INTENT = "org.obdreader.io.ObdReaderService.OBD_PERSIST_INTENT";
	
	public final static String MAX_SPEED = "Max Speed";
	public final static String MIN_SPEED = "Min Speed";
	public final static String AVG_SPEED = "Avg Speed";
	
	public final static String MAX_GPS_SPEED = "Max GPS Speed";
	public final static String MIN_GPS_SPEED = "Min GPS Speed";
	public final static String AVG_GPS_SPEED = "Avg GPS Speed";

	public final static String MAX_RPM = "Max RPM";
	public final static String MIN_RPM = "Min RPM";
	public final static String AVG_RPM = "Avg RPM";

	public static final String SEPARATOR = ",";
	
	protected Map<String,String> obdData;
	protected int maxAgeMinutes;
	protected boolean logCsv;
	protected String dataDir;
	
	protected ArrayList<Double> speeds;
	protected ArrayList<Double> gpsSpeeds;
	protected ArrayList<Double> rpms;

	public ObdPersistAlarmWorker(Context ctx, int periodSec) {
		super(ctx, periodSec, false);
		obdData = new HashMap<String,String>();
		speeds = new ArrayList<Double>();
		rpms = new ArrayList<Double>();
		gpsSpeeds = new ArrayList<Double>();
	}

	@Override
	protected void updatePrefs() {
		super.updatePrefs();
		maxAgeMinutes = ObdReaderConfigActivity.getMaxDataAgeMinutes(ctx);
		logCsv = ObdReaderConfigActivity.getLogToCsv(ctx);
		dataDir = ObdReaderConfigActivity.getDataDir(ctx);
	}

	@Override
	public String getAlarmIntent() {
		return OBD_PERSIST_INTENT;
	}

	@Override
	public void handleAlarm(Intent intent, AlarmHandledCallback callback) {
		PersistWorker w = new PersistWorker(callback);
		w.start();
	}
	
	protected void logCsvData(JSONObject data) {
		try {
			Iterator<String> keysIt = data.keys();
			ArrayList<String> keys = new ArrayList<String>();
			while (keysIt.hasNext()) {
				keys.add(keysIt.next());
			}
			Collections.sort(keys);
			File outDir = new File(Environment.getExternalStorageDirectory(), dataDir);
			if (!outDir.exists()) {
				if (!outDir.mkdirs()) {
					Log.e("csvwrite","can't create output directory");
					ObdReaderAlarmWorker.sendStatusUpdate(ctx, "can't access sd card");
					return;
				}
			}
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.HHmm");
			String fileName = String.format("obd_data.%s.csv", df.format(c.getTime()));
			File outFile = new File(outDir, fileName);
			BufferedWriter writer = null;
			if (!outFile.exists()) {
				writer = new BufferedWriter(new FileWriter(outFile));
				writer.write(join(keys));
				writer.newLine();
			} else {
				writer = new BufferedWriter(new FileWriter(outFile,true));
			}
			ArrayList<String> row = getStringList(keys,data);
			writer.write(join(row));
			writer.newLine();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected ArrayList<String> getStringList(ArrayList<String> keys, JSONObject data) {
		ArrayList<String> res = new ArrayList<String>();
		try {
			for (int i = 0; i < keys.size(); i++) {
				String k = keys.get(i);
				if (data.has(k)) {
					res.add(data.getString(k));
				} else {
					res.add(ObdReader.FILL_STRING);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	protected static String join(List<String> ls) {
		StringBuffer line = new StringBuffer();
		Iterator<String> keysIt = ls.iterator();
		while (keysIt.hasNext()) {
			line.append(keysIt.next());
			if (keysIt.hasNext()) {
				line.append(SEPARATOR);
			}
		}
		return line.toString();
	}

	@Override
	public void start() {
		super.start();
        IntentFilter updateFilter = new IntentFilter(ObdReaderService.UPDATE_INTENT);
        ctx.registerReceiver(updateRecv, updateFilter);
	}

	@Override
	public void stop() {
		super.stop();
		ctx.unregisterReceiver(updateRecv);
	}

	private final BroadcastReceiver updateRecv = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			String desc = extras.getString(ObdReaderMainActivity.EXTRA_NAME);
			String res = extras.getString(ObdReaderMainActivity.EXTRA_RESULT);
			String val = extras.getString(ObdReaderMainActivity.EXTRA_VALUE);
			obdData.put(desc, val);
			if (ObdReader.SPEED.equals(desc)) {
				addInt(speeds,val);
			} else if (ObdReader.RPM.equals(desc)) {
				addInt(rpms,val);
			} else if (AlarmWorker.GPS_SPEED.equals(desc)) {
				addDouble(gpsSpeeds,val);
			}
		}
	};
	
	protected void addDouble(ArrayList<Double> vals, String res) {
		try {
			double v = Double.parseDouble(res);
			vals.add(v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void addInt(ArrayList<Double> vals, String res) {
		try {
			double v = (double)Integer.parseInt(res);
			vals.add(v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static double min(ArrayList<Double> ds) {
		if (ds.size() > 0) {
			return Collections.min(ds);
		}
		return ObdReader.FILL_DOUBLE;
	}

	public static double max(ArrayList<Double> ds) {
		if (ds.size() > 0) {
			return Collections.max(ds);
		}
		return ObdReader.FILL_DOUBLE;
	}

	public static double mean(ArrayList<Double> ds) {
		if (ds == null || ds.size() == 0) {
			return ObdReader.FILL_DOUBLE;
		}
		double avg = 0.0;
		for (int i = 0; ds != null && i < ds.size(); i++) {
			avg += ds.get(i);
		}
		return avg / ds.size();
	}
	
	protected class PersistWorker extends Thread {
		AlarmHandledCallback callback;
		protected PersistWorker(AlarmHandledCallback callback) {
			this.callback = callback;
		}

		@Override
		public void run() {
			double avgSpeed = mean(speeds);
			double minSpeed = min(speeds);
			double maxSpeed = max(speeds);
			speeds.clear();
			double avgGpsSpeed = mean(gpsSpeeds);
			double minGpsSpeed = min(gpsSpeeds);
			double maxGpsSpeed = max(gpsSpeeds);
			gpsSpeeds.clear();
			double avgRpm = mean(rpms);
			double minRpm = max(rpms);
			double maxRpm = min(rpms);
			rpms.clear();

			try {
				JSONObject data = new JSONObject(obdData);
				data.put(LATITUDE, lat);
				data.put(LONGITUDE, lon);
				data.put(GPS_SPEED, speed);
				data.put(GPS_TIME, gpsTime);
				data.put(HEADING, heading);
				data.put(MAX_SPEED, maxSpeed);
				data.put(MIN_SPEED, minSpeed);
				data.put(AVG_SPEED, avgSpeed);
				data.put(MAX_GPS_SPEED, maxGpsSpeed);
				data.put(MIN_GPS_SPEED, minGpsSpeed);
				data.put(AVG_GPS_SPEED, avgGpsSpeed);
				data.put(MAX_RPM, maxRpm);
				data.put(MIN_RPM, minRpm);
				data.put(AVG_RPM, avgRpm);
				
				ObdReaderAlarmWorker.sendUpdate(ctx, MAX_SPEED, Double.toString(maxSpeed), formatDouble("%.2f km/h",maxSpeed));
				ObdReaderAlarmWorker.sendUpdate(ctx, MIN_SPEED, Double.toString(minSpeed), formatDouble("%.2f km/h",minSpeed));
				ObdReaderAlarmWorker.sendUpdate(ctx, AVG_SPEED, Double.toString(avgSpeed), formatDouble("%.2f km/h",avgSpeed));

				ObdReaderAlarmWorker.sendUpdate(ctx, MAX_GPS_SPEED, Double.toString(maxGpsSpeed), formatDouble("%.2f km/h",maxGpsSpeed));
				ObdReaderAlarmWorker.sendUpdate(ctx, MIN_GPS_SPEED, Double.toString(minGpsSpeed), formatDouble("%.2f km/h",minGpsSpeed));
				ObdReaderAlarmWorker.sendUpdate(ctx, AVG_GPS_SPEED, Double.toString(avgGpsSpeed), formatDouble("%.2f km/h",avgGpsSpeed));

				ObdReaderAlarmWorker.sendUpdate(ctx, MAX_RPM, Double.toString(maxRpm), formatInt("%d", (int)maxRpm));
				ObdReaderAlarmWorker.sendUpdate(ctx, MIN_RPM, Double.toString(minRpm), formatInt("%d", (int)minRpm));
				ObdReaderAlarmWorker.sendUpdate(ctx, AVG_RPM, Double.toString(avgRpm), formatInt("%d", (int)avgRpm));

				double obsTime = System.currentTimeMillis() / 1000.0;
				data.put(ObdReader.OBSTIME, obsTime);

				ObdDatabaseHelper db = new ObdDatabaseHelper(ctx);
				db.writeMapData(obsTime,data);
				db.clearData(maxAgeMinutes * 60);
				db.close();

				if (logCsv) {
					logCsvData(data);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			callback.alarmHandled();
		}
	}
	
	public static String formatDouble(String format, double v) {
		if (v == ObdReader.FILL_DOUBLE) {
			return ObdReader.FILL_STRING;
		} else {
			return String.format(format,v);
		}
	}

	public static String formatInt(String format, int v) {
		if (v == ObdReader.FILL_DOUBLE) {
			return ObdReader.FILL_STRING;
		} else {
			return String.format(format,v);
		}
	}

}
