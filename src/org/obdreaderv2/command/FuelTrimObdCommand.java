package org.obdreaderv2.command;

import android.content.Context;

public class FuelTrimObdCommand extends IntObdCommand {

	public FuelTrimObdCommand(String cmd, String desc, String resType,Context ctx) {
		super(cmd,desc,resType,resType,ctx);
	}
	public FuelTrimObdCommand(FuelTrimObdCommand other) {
		super(other);
	}

	public FuelTrimObdCommand(Context ctx) {
		super("0107","Long Term Fuel Trim","%","%",ctx);
	}

	@Override
	public int transform(int b) {
		double perc = (b-128)*(100.0/128);
		return (int)perc;
	}
}
