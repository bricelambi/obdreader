package org.obdreader.command;

import android.content.Context;

public class FordTractAssistOnOffObdCommand extends FordObdCommand {

	public FordTractAssistOnOffObdCommand(Context ctx) {
		super("222927","Traction Assist","","",ctx);
	}
	public FordTractAssistOnOffObdCommand(FordTractAssistOnOffObdCommand other) {
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
