package org.obdreader.command;

import org.obdreader.io.ObdReader;

import android.content.Context;

public class SpeedObdCommand extends IntObdCommand {

	public static final String metricUnits = "km/h";
	public static final String imperialUnits = "mph";
	public SpeedObdCommand(Context ctx) {
		super("010D",ObdReader.SPEED,metricUnits,imperialUnits,ctx);
	}
	public SpeedObdCommand(SpeedObdCommand other) {
		super(other);
	}
	@Override
	public String prettyPrintResult(String r) {
		return String.format("%s %s", r, metricUnits);
	}
	@Override
	public int getImperialInt() {
		if (intValue <= 0) {
			return 0;
		}
		return (int)(intValue * .625);
	}
}
