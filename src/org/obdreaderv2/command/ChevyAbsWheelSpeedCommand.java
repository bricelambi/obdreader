package org.obdreaderv2.command;

import android.content.Context;

public class ChevyAbsWheelSpeedCommand extends IntObdCommand{

	public ChevyAbsWheelSpeedCommand(String cmd, String desc, String resType, String impType, Context ctx) {
		super("224054", "Left Rear Wheel Speed", resType, impType, ctx);
	}
	public ChevyAbsWheelSpeedCommand(ChevyAbsWheelSpeedCommand other) {
		super(other);
	}
	protected int transform(int b) {
		return b-40;
	}
	@Override
	public int getImperialInt() {
		return (intValue*9/5) + 32;
	}
}
