package org.obdreader.command;

import android.content.Context;

public class FordYawObdCommand extends FordObdCommand {

	public FordYawObdCommand(String cmd, String desc, Context ctx) {
		super(cmd, desc, "deg/sec", "deg/sec", ctx);
	}
	public FordYawObdCommand(Context ctx) {
		super("223A45","Yaw Rate","deg/sec","deg/sec",ctx);
	}
	public FordYawObdCommand(FordYawObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		if ("NODATA".equals(res)) {
			return "NODATA";
		}
		String A = res.substring(6,8);
		int a = Integer.parseInt(A,16);
		a = a - 128;
		return Integer.toString(a);
	}
}
