package org.obdreaderv2.command;

import android.content.Context;

public class IntObdCommand extends ObdCommand {

	protected int intValue = -9999;
	public IntObdCommand(String cmd, String desc, String resType, String impType, Context ctx) {
		super(cmd, desc, resType, impType,ctx);
	}
	public IntObdCommand(IntObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		String byteStr = res.substring(4,6);
		int b = Integer.parseInt(byteStr,16);
		intValue = transform(b);
		return Integer.toString(intValue);
	}
	protected int transform(int b) {
		return b;
	}
	public int getInt() {
		return intValue;
	}
	public int getImperialInt() {
		return intValue;
	}
	public Object getRawValue() {
		return intValue;
	}
}
