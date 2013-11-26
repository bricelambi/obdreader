package org.obdreaderv2.command;

import android.content.Context;

public class FordSteeringAngleObdCommand extends FordObdCommand {

	public FordSteeringAngleObdCommand(Context ctx) {
		super("223201","Steering Angle","deg","deg",ctx);
	}
	public FordSteeringAngleObdCommand(FordSteeringAngleObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		if ("NODATA".equals(res)) {
			return "NODATA";
		}
		String A = res.substring(6,8);
		//String B = res.substring(8,10);
		int a = Integer.parseInt(A,16);
		//int b = Integer.parseInt(B,16);
		int angle = (int)(a*6.25 - 800);
		return String.format("%d %s", angle, resType);
	}
}
