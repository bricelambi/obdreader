package org.obdreader.config;

import java.util.ArrayList;

import org.obdreader.command.AirIntakeTempObdCommand;
import org.obdreader.command.CommandEquivRatioObdCommand;
import org.obdreader.command.DtcNumberObdCommand;
import org.obdreader.command.EngineCodeObdCommand;
import org.obdreader.command.EngineRPMObdCommand;
import org.obdreader.command.EngineRunTimeObdCommand;
import org.obdreader.command.FuelPressureObdCommand;
import org.obdreader.command.FuelTrimObdCommand;
import org.obdreader.command.IntakeManifoldPressureObdCommand;
import org.obdreader.command.MassAirFlowObdCommand;
import org.obdreader.command.ObdCommand;
import org.obdreader.command.PressureObdCommand;
import org.obdreader.command.SpeedObdCommand;
import org.obdreader.command.TempObdCommand;
import org.obdreader.command.ThrottleObdCommand;

import android.content.Context;

public class ObdConfig {

	public static ArrayList<ObdCommand> getCommands(Context ctx) {
		ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
		cmds.add(new AirIntakeTempObdCommand(ctx));
		cmds.add(new IntakeManifoldPressureObdCommand(ctx));
		cmds.add(new PressureObdCommand("0133","Barometric Press","kPa","atm",ctx));
		cmds.add(new TempObdCommand("0146","Ambient Air Temp","C","F",ctx));
		cmds.add(new SpeedObdCommand(ctx));
		cmds.add(new ThrottleObdCommand(ctx));
		cmds.add(new EngineRPMObdCommand(ctx));
		cmds.add(new FuelPressureObdCommand(ctx));
		cmds.add(new TempObdCommand("0105","Coolant Temp","C","F",ctx));
		cmds.add(new ThrottleObdCommand("0104","Engine Load","%","%",ctx));
		cmds.add(new MassAirFlowObdCommand(ctx));
		cmds.add(new FuelTrimObdCommand(ctx));
		cmds.add(new FuelTrimObdCommand("0106","Short Term Fuel Trim","%",ctx));
		cmds.add(new EngineRunTimeObdCommand(ctx));
		cmds.add(new CommandEquivRatioObdCommand(ctx));
		return cmds;
	}

	public static ArrayList<ObdCommand> getStaticCommands(Context ctx) {
		ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
		cmds.add(new DtcNumberObdCommand(ctx));
		cmds.add(new EngineCodeObdCommand(ctx));
		cmds.add(new ObdCommand("04","Reset Codes","","",ctx));
		cmds.add(new ObdCommand("atz","Serial Reset atz","","",ctx));
		cmds.add(new ObdCommand("ate0","Serial Echo Off ate0","","",ctx));
		cmds.add(new ObdCommand("ate1","Serial Echo On ate1","","",ctx));
		cmds.add(new ObdCommand("atsp0","Reset Protocol astp0","","",ctx));
		cmds.add(new ObdCommand("atspa2","Reset Protocol atspa2","","",ctx));
		return cmds;
	}

	public static ArrayList<ObdCommand> getAllCommands(Context ctx) {
		ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
		cmds.addAll(getStaticCommands(ctx));
		cmds.addAll(getCommands(ctx));
		return cmds;
	}
}
