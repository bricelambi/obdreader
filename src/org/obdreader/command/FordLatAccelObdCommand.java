package org.obdreader.command;

import android.content.Context;

public class FordLatAccelObdCommand extends FordObdCommand {

	public FordLatAccelObdCommand(String cmd, String desc,Context ctx) {
		super(cmd,desc,"m/s","m/s",ctx);
	}
	public FordLatAccelObdCommand(Context ctx) {
		super("223A33","Lateral Accel","m/s","m/s",ctx);
	}
	public FordLatAccelObdCommand(FordLatAccelObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		if ("NODATA".equals(res)) {
			return "NODATA";
		}
		String A = res.substring(6,8);
		String B = res.substring(8,10);
		int a = Integer.parseInt(A,16);
		int b = Integer.parseInt(B,16);
		double accel = ((a+b)-128)/16.0;
		return String.format("%.2f %s", accel, resType);
	}
}
