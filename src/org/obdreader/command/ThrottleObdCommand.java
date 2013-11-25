package org.obdreader.command;

import android.content.Context;

public class ThrottleObdCommand extends IntObdCommand {

	public ThrottleObdCommand(String cmd, String desc, String resType, String impType, Context ctx) {
		super(cmd,desc,resType,impType,ctx);
	}
	public ThrottleObdCommand(Context ctx) {
		super("0111","Throttle Position","%","%",ctx);
	}
	public ThrottleObdCommand(ThrottleObdCommand other) {
		super(other);
	}
	protected int transform(int b) {
		return (int)((double)(b*100)/255.0);
	}
}
