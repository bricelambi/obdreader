package org.obdreader.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.obdreader.activity.ObdReaderConfigActivity;
import org.obdreader.command.IgnitionObdCommand;
import org.obdreader.command.ObdCommand;
import org.obdreader.command.SpeedObdCommand;
import org.obdreader.config.ObdConfig;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

public class ObdReader extends Thread {

	public final static String COOLANT_TEMP = "Coolant Temp";
	public final static String FUEL_ECON = "Fuel Economy";
	public final static String FUEL_ECON_MAP = "Fuel Economy MAP";
	public final static String RPM = "Engine RPM";
	public final static String ODOMETER = "Odometer";
	public final static String RUN_TIME = "Engine Runtime";
	public final static String SPEED = "Vehicle Speed";
	public final static String IGNITION = "Ignition";
	public final static String ENGINE_CODES = "Engine Codes";
	public final static String AVG_SPEED = "Average Vehicle Speed";
	public final static String MAX_SPEED = "Max Vehicle Speed";
	public final static String MIN_SPEED = "Min Vehicle Speed";
	public final static String AIR_TEMP = "Ambient Air Temp";
	public final static String INTAKE_TEMP = "Air Intake Temp";
	public final static String VOLTAGE = "Voltage";
	public final static String LATITUDE = "Latitude";
	public final static String LONGITUDE = "Longitude";
	public final static String HEADING = "Heading";
	public final static String GPS_SPEED = "GPS Speed";
	public final static String GPS_TIME = "GPS Time";
	public final static String OBSTIME = "Obs Time";
	public final static String OKAY = "OK";
	public final static String COMM = "Comm";

	private ArrayList<ObdCommand> cmds;
	private String devString;
	protected static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private InputStream in;
	private OutputStream out;
	protected BluetoothDevice dev = null;
	protected BluetoothSocket sock = null;
	protected BluetoothAdapter adapter = null;
	protected HashMap<String,String> results = null;
	public final static int NUM_CONNECT_ATTEMPTS = 15;
	public final static int NUM_ATTEMPTS = 5;
	private String connectMessage;
	private boolean stop;
	private boolean connected;
	public final static String FILL_STRING = "--";
	public final static double FILL_DOUBLE = -9999.0;
	protected Context ctx;

	public ObdReader(Context ctx) {
		this.ctx = ctx;
		this.devString = ObdReaderConfigActivity.getBluetootheDevice(ctx);
		results = new HashMap<String,String>();
		stop = false;
		adapter = BluetoothAdapter.getDefaultAdapter();
		connected = false;
		cmds = ObdConfig.getCommands(ctx);
	}

	public void runSingleCommand(ObdCommand cmd) {
		
	}

	public void run() {
    	int cmdSize = cmds.size();
        for (int i = 0; i < cmdSize; i++) {
        	String desc = cmds.get(i).getDesc();
        	results.put(desc,FILL_STRING);
        }
    	try {
        	long obsTime = System.currentTimeMillis()/1000;
    		results.put(OBSTIME, Long.toString(obsTime));
    		
    		connected = startDevice();
			if (connected) {
				results.put(COMM,OKAY);
			} else {
				stopDevice();
				results.put(COMM,FILL_STRING);
				return;
			}
			testVehicleRunning();
        	for (int i = 0; !stop && i < cmdSize; i++) {
        		ObdCommand cmd = cmds.get(i);
        		String desc = cmd.getDesc();
        		for (int a = 0; a < NUM_ATTEMPTS; a++) {
                	try {
    					String result = runCommand(cmd,in,out);
    					results.put(desc,result);
    					break;
                	} catch (Exception e) {
                		Log.e("obdreader","command " + desc + " failed attempt " + a);
                		e.printStackTrace();
                		results.put(desc, FILL_STRING);
                	}
                	if (cmd.isUnableToConnect()) {
                		Log.e("obdreader", "unable to connect, breaking");
                		break;
                	}
                	try {
                		Log.e("obdreader","resetting device...");
	        			ObdCommand reset = new ObdCommand("atws","soft reset","string","string",ctx);	        			
	        			ObdCommand clear = new SpeedObdCommand(ctx);
	        			ObdCommand ate0 = new ObdCommand("ate0","echo off","string","string",ctx);

	        			tryRunCommand(reset);
	        			tryRunCommand(clear);
	        			tryRunCommand(ate0);
                	} catch (Exception e) {
                		Log.e("obdreader","reset device failed.");
                		e.printStackTrace();
                	}
                	Thread.sleep(150);
        		}
        		if (cmd.isUnableToConnect()) {
        			Log.e("obdreader", "unable to connect, breaking");
        			break;
        		}
        	}
        	stopDevice();
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    }

	protected boolean startDevice() throws IOException, InterruptedException {
		for (int a = 1; !stop && a < NUM_CONNECT_ATTEMPTS+1; a++) {
			try {
				Log.e("obdreader","starting device...");
				adapter.cancelDiscovery();
				dev = adapter.getRemoteDevice(devString);
				//sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
				sock = dev.createInsecureRfcommSocketToServiceRecord(MY_UUID);
		        sock.connect();
		        in = sock.getInputStream();
		        out = sock.getOutputStream();
		        ObdCommand echoOff = new ObdCommand("ate0", "echo off", "string", "string",ctx);
		        runCommand(echoOff,in,out);
		        ObdReaderAlarmWorker.sendStatusUpdate(ctx, "connection okay");
				return true;
			} catch (Exception e) {
				Log.e("obdreader","device connect failed attempt " + a);
				ObdReaderAlarmWorker.sendStatusUpdate(ctx, "device connect failed attempt " + a);
				e.printStackTrace();
				connectMessage = e.getMessage();
				stopDevice();
			}
			Thread.sleep(500);
		}
		return false;
	}

	protected void stopDevice() {
		try {
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			sock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		in = null;
		out = null;
		sock = null;
	}

	protected void testVehicleRunning() {
		try {
			IgnitionObdCommand ig = new IgnitionObdCommand(ctx);
			runCommand(ig,in,out);
			String r = ig.formatResult();
			results.put(IGNITION, r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String runCommand(ObdCommand cmd, InputStream in, OutputStream out) throws IOException {
		cmd.setInputStream(in);
		cmd.setOutputStream(out);
		cmd.run();
		if (cmd.isNoData()) {
			return FILL_STRING;
		}
		return cmd.formatResult();
	}

	public static String tryRunCommand(ObdCommand cmd, InputStream in, OutputStream out) {
		try {
			return runCommand(cmd,in,out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String tryRunCommand(ObdCommand cmd) {
		try {
			return runCommand(cmd,in,out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public HashMap<String, String> getResults() {
		return results;
	}

	public void putResult(String key, String value) {
		results.put(key, value);
	}

	public void setResults(HashMap<String, String> results) {
		this.results = results;
	}

	public String getConnectMessage() {
		return connectMessage;
	}

	public void setConnectMessage(String connectMessage) {
		this.connectMessage = connectMessage;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	public boolean isConnected() {
		return connected;
	}
}
