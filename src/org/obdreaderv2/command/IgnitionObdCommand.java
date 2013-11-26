package org.obdreaderv2.command;

import org.obdreaderv2.io.ObdReader;

import android.content.Context;

public class IgnitionObdCommand extends ObdCommand {
	
	public IgnitionObdCommand(Context ctx) {
		super("at ign", ObdReader.IGNITION, "", "",ctx);
	}
	
	public IgnitionObdCommand(IntObdCommand other) {
		super(other);
	}

	public String formatResult() {
		String res = getResult();
		String[] ress = res.split("\r");
		res = ress[0].replace(" ","");
		return res.toUpperCase();
	}
}
