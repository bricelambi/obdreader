package org.obdreaderv2.command;

import android.content.Context;

public class FordBrakeOnOffObdCommand extends FordObdCommand {

	public FordBrakeOnOffObdCommand(Context ctx) {
		super("222900","Brake","","",ctx);
	}
	public FordBrakeOnOffObdCommand(FordBrakeOnOffObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		if ("NODATA".equals(res)) {
			return "NODATA";
		}
		String A = res.substring(6,8);
		if ("00".equals(A)) {
			return "Off";
		}
		return "On";
	}
}
