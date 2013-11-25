package org.obdreader.command;

import org.obdreader.io.ObdReader;

import android.content.Context;

public class VoltageObdCommand extends IntObdCommand {

	public VoltageObdCommand(Context ctx) {
		super("atrv",ObdReader.VOLTAGE,"v","v",ctx);
		isAtCommand = true;
	}
	public String formatResult() {
		String res = getResult();
		String[] ress = res.split("\r");
		res = ress[0].replace(" ","");
		res = res.toLowerCase().replace("v", "");
		double v = Double.parseDouble(res);
		String vStr = String.format("%.2f", v);
		return vStr;
	}
}
