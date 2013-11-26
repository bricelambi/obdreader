package org.obdreaderv2.command;

import android.content.Context;

public class IntakeManifoldPressureObdCommand extends PressureObdCommand {

	public IntakeManifoldPressureObdCommand(Context ctx) {
		super("010B","Intake Manifold Press","kPa","atm",ctx);
	}
	public IntakeManifoldPressureObdCommand(IntakeManifoldPressureObdCommand other) {
		super(other);
	}
}
