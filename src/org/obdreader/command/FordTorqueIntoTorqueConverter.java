package org.obdreader.command;

import android.content.Context;
import android.util.Log;

public class FordTorqueIntoTorqueConverter extends FordObdCommand {

	public FordTorqueIntoTorqueConverter(Context ctx) {
		super("2209cb","Torque Conv.","ft./lbs","ft./lbs",ctx);
	}
	public FordTorqueIntoTorqueConverter(FordTorqueIntoTorqueConverter other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		Log.e("torque_conv",res);
		if ("NODATA".equals(res)) {
			return "NODATA";
		}
		String A = res.substring(6,8);
		String B = res.substring(8,10);
		
		int a = Integer.parseInt(A,16);
		int b = Integer.parseInt(B,16);
		
		int val = a * 256 + b;
		return String.format("%d %s", val, resType);
	}
}
