package org.obdreaderv2.command;

import android.content.Context;
import android.util.Log;

public class FordTransmissionGearObdCommand extends FordObdCommand {

	public FordTransmissionGearObdCommand(Context ctx) {
		super("2211b3","Transmission Gear","","",ctx);
	}
	public FordTransmissionGearObdCommand(FordTransmissionGearObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		Log.e("trans",res);
		if ("NODATA".equals(res)) {
			return "NODATA";
		}
		String A = res.substring(6,8);
		//String B = res.substring(8,10);
		int a = Integer.parseInt(A,16);
		//int b = Integer.parseInt(B,16);
		int gear = a / 2;
		return String.format("%d %s", gear, resType);
	}
}
