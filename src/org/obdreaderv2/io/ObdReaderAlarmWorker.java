package org.obdreaderv2.io;

import java.util.ArrayList;
import java.util.Map;

import org.obdreaderv2.activity.ObdReaderMainActivity;
import org.obdreaderv2.command.ObdCommand;
import org.obdreaderv2.config.ObdConfig;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ObdReaderAlarmWorker extends AlarmWorker {

	public final static String OBD_ALARM_INTENT = "org.obdreader.io.ObdReaderService.OBD_ALARM_INTENT";
	
	protected ObdReader reader;
	protected ArrayList<ObdCommand> cmds;

	public ObdReaderAlarmWorker(Context ctx, int periodSec) {
		super(ctx, periodSec,true);
		cmds = ObdConfig.getCommands(ctx);
	}

	@Override
	public String getAlarmIntent() {
		return OBD_ALARM_INTENT;
	}

	@Override
	public void handleAlarm(Intent intent, AlarmHandledCallback callback) {
		ObdWorker w = new ObdWorker(callback);
		w.start();
	}

	@Override
	public void stop() {
		super.stop();
		if (reader != null) {
			reader.setStop(true);
		}
	}

	public void sendUpdate(Map<String,String> results) {
		for (int i = 0; i < cmds.size(); i++) {
			ObdCommand c = cmds.get(i);
			String desc = c.getDesc();
			String v = ObdReader.FILL_STRING;
			String r = ObdReader.FILL_STRING;
			if (results.containsKey(desc)) {
				v = results.get(desc);
				if (!ObdReader.FILL_STRING.equals(v)) {
					r = c.prettyPrintResult(v);
				}
			}
			sendUpdate(ctx, desc, v, r);
		}
		if (results.containsKey(ObdReader.IGNITION)) {
			String r = results.get(ObdReader.IGNITION);
			sendUpdate(ctx, ObdReader.IGNITION, r, r);
		}
		sendOther(results, ObdReader.COMM);
	}

	public void sendOther(Map<String,String> results, String desc) {
		String c = ObdReader.FILL_STRING;
		if (results.containsKey(desc)) {
			c = results.get(desc);
		}
		sendUpdate(ctx, desc, c, c);
	}

	public static void sendStatusUpdate(Context ctx, String msg) {
		Intent i = new Intent(ObdReaderService.STATUS_INTENT);
		i.putExtra(ObdReaderMainActivity.EXTRA_STATUS, msg);
		ctx.sendBroadcast(i);
	}

	public static void sendUpdate(Context ctx, String cmdDesc, String value, String result) {
		Intent i = new Intent();
		i.putExtra(ObdReaderMainActivity.EXTRA_NAME, cmdDesc);
		i.putExtra(ObdReaderMainActivity.EXTRA_VALUE, value);
		i.putExtra(ObdReaderMainActivity.EXTRA_RESULT, result);
		i.setAction(ObdReaderService.UPDATE_INTENT);
		ctx.sendBroadcast(i);
	}

	protected class ObdWorker extends Thread {
		AlarmHandledCallback callback;
		protected ObdWorker(AlarmHandledCallback callback) {
			this.callback = callback;
		}
		@Override
		public void run() {
			reader = new ObdReader(ctx);
			reader.start();
			try {
				reader.join();
			} catch (Exception e) {
				Log.e("obdreader","joining worker thread failed");
				e.printStackTrace();
				return;
			}
			sendUpdate(reader.results);
			callback.alarmHandled();
		}
	}
}
