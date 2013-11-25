package org.obdreader.command;

import android.content.Context;

public class TimingAdvanceObdCommand extends ObdCommand {

	public TimingAdvanceObdCommand(Context ctx) {
		super("010E","Timing Advance","deg","deg",ctx);
	}
	public TimingAdvanceObdCommand(TimingAdvanceObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		if ("NODATA".equals(res)) {
			return "NODATA";
		}
		String A = res.substring(4,6);
		double a = (double)Integer.parseInt(A,16);
		double adv = (a/2.0) / 64;
		return String.format("%.1f %s", adv, resType);
	}
}
