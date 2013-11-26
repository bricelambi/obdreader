package org.obdreaderv2.command;

import android.content.Context;
import android.util.Log;

public class FordThrottlePositionObdCommand extends FordObdCommand {

	public FordThrottlePositionObdCommand(Context ctx) {
		super("22093c","Throttle Position","%","%",ctx);
	}
	public FordThrottlePositionObdCommand(FordThrottlePositionObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		Log.e(this.desc,res);
		Log.e("pedal pos",res);
		if ("NODATA".equals(res)) {
			return "NODATA";
		}
		String A = res.substring(6,8);
		String B = res.substring(8,10);
		int a = Integer.parseInt(A,16);
		int b = Integer.parseInt(B,16);
		int pos = (int)((a*256.0+b) * 100) / 8192;
		return String.format("%d%s", pos, resType);
	}
}
