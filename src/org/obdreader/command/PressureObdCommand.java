package org.obdreader.command;

import android.content.Context;

public class PressureObdCommand extends IntObdCommand {

	public PressureObdCommand(String cmd, String desc, String resType, String impType, Context ctx) {
		super(cmd,desc,resType,impType,ctx);
	}
	public PressureObdCommand(PressureObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		if (!isImperial() || "NODATA".equals(res)) {
			return res;
		}
		double atm = intValue * 1.0 / 101.3;
		return String.format("%.2f %s", atm, impType);
	}
}
