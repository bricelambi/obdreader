package org.obdreader.command;

import android.content.Context;

public class AirIntakeTempObdCommand extends TempObdCommand{

	public AirIntakeTempObdCommand(Context ctx) {
		super("010F","Air Intake Temp","C","F",ctx);
	}
	public AirIntakeTempObdCommand(AirIntakeTempObdCommand other) {
		super(other);
	}
}
