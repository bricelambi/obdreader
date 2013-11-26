package org.obdreaderv2.command;

import android.content.Context;
import android.util.Log;

public class FordAcceleratorPedalPositionObdCommand extends FordObdCommand {

	public FordAcceleratorPedalPositionObdCommand(Context ctx) {
		super("2209d4","Pedal Position","%","%",ctx);
	}
	public FordAcceleratorPedalPositionObdCommand(FordAcceleratorPedalPositionObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		Log.e("pedal pos",res);
		if ("NODATA".equals(res)) {
			return "NODATA";
		}
		String A = res.substring(6,8);
		
		int a = Integer.parseInt(A,16);
		int pos = a/2;
		return String.format("%d%s", pos, resType);
	}
}
