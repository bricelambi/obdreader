package org.obdreaderv2.command;

import android.content.Context;

public class TempObdCommand extends IntObdCommand{

	public TempObdCommand(TempObdCommand other) {
		super(other);
	}
	public TempObdCommand(String cmd, String desc, String resType, String impType, Context ctx) {
		super(cmd,desc,resType,impType,ctx);
	}
	protected int transform(int b) {
		return b-40;
	}
	@Override
	public int getImperialInt() {
		return (intValue*9/5) + 32;
	}
}
