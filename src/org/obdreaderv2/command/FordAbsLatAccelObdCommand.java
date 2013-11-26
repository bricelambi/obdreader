package org.obdreaderv2.command;

import android.content.Context;
import android.util.Log;

public class FordAbsLatAccelObdCommand extends FordObdCommand {

	public FordAbsLatAccelObdCommand(Context ctx) {
		super("223a51","ABS Lat Accel","m/s","m/s",ctx);
	}
	public FordAbsLatAccelObdCommand(FordAbsLatAccelObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		Log.e("abs_lat_accel",res);
		if ("NODATA".equals(res)) {
			return "NODATA";
		}
		String A = res.substring(6,8);
		String B = res.substring(8,10);
		int a = Integer.parseInt(A,16);
		int b = Integer.parseInt(B,16);
		double accel = ((a*256)+b)*0.02;
		
		return String.format("%.2f %s", accel, resType);
	}
}
