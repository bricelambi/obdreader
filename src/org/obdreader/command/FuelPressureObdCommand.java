package org.obdreader.command;

import android.content.Context;

public class FuelPressureObdCommand extends PressureObdCommand{

	public FuelPressureObdCommand(Context ctx) {
		super("010A","Fuel Press","kPa","atm",ctx);
	}
	public FuelPressureObdCommand(FuelPressureObdCommand other) {
		super(other);
	}
	public int transform(int b) {
		return b*3;
	}
}
