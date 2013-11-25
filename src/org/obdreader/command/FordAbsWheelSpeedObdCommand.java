package org.obdreader.command;

import android.content.Context;

public class FordAbsWheelSpeedObdCommand extends FordObdCommand {

	public FordAbsWheelSpeedObdCommand(Context ctx) {
		super("223987","ABS Wheel Speed","km/h","km/h",ctx);
	}
	public FordAbsWheelSpeedObdCommand(FordAbsWheelSpeedObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		if ("NODATA".equals(res)) {
			return "NODATA";
		}
		String A = res.substring(6,8);
		String B = res.substring(8,10);
		String C = res.substring(10,12);
		String D = res.substring(12,14);
		int a = Integer.parseInt(A,16);
		int b = Integer.parseInt(B,16);
		int c = Integer.parseInt(C,16);
		int d = Integer.parseInt(D,16);
		return String.format("%d %d %d %d %s", a, b, c, d, resType);
	}
}
