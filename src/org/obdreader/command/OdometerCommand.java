package org.obdreader.command;

import org.obdreader.io.ObdReader;

import android.content.Context;

public class OdometerCommand extends TwoByteObdCommand {

	public OdometerCommand(Context ctx) {
		super("0131", ObdReader.ODOMETER, "KM", "KM", ctx);
	}

	protected int transform(int a, int b) {
		return a*256+b;
	}
}
