package org.obdreader.command;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class Obd1939Command extends ObdCommand {

	public static final int NUM_LINES_TO_READ = 6;
	
	protected String rawResult;

	public Obd1939Command(String cmd, String desc, String resType,
			String impType,Context ctx) {
		super(cmd, desc, resType, impType,ctx);
	}
	public Obd1939Command(Obd1939Command other) {
		this(other.cmd, other.desc, other.resType, other.impType, other.ctx);
	}
	public void run() throws IOException {
		Log.e("write",cmd);
		ObdResponseHandler res = new ObdResponseHandler(in);
		try {
			res.start();
			sendCmd(cmd);
			Thread.sleep(100);
			out.write('!');
			res.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		rawResult = res.result.toString();
		Log.e("read",getResult());
	}
	protected void sendCmd(String cmd) throws IOException {
		cmd += "\r\n";
		out.write(cmd.getBytes());
	}
	
	public String getResult() {
		return rawResult;
	}
	
	protected class ObdResponseHandler extends Thread {
		public InputStream in;
		public boolean running;
		public StringBuffer result; 
		public ObdResponseHandler(InputStream in) {
			this.in = in;
			running = false;
			result = new StringBuffer();
		}
		public void run() {
			running = true;
			try {
				while (running) {
					int b = in.read();
					char c = (char)b;
					if (b == -1 || '>' == c) {
						break;
					}
					result.append(c);
				}
				while (in.available() > 0) {
					in.read();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				running = false;
			}
		}
	}
}
