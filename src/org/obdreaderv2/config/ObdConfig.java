package org.obdreaderv2.config;

import java.util.ArrayList;

import org.obdreaderv2.command.AirIntakeTempObdCommand;
import org.obdreaderv2.command.CommandEquivRatioObdCommand;
import org.obdreaderv2.command.DtcNumberObdCommand;
import org.obdreaderv2.command.EngineCodeObdCommand;
import org.obdreaderv2.command.EngineRPMObdCommand;
import org.obdreaderv2.command.EngineRunTimeObdCommand;
import org.obdreaderv2.command.FuelPressureObdCommand;
import org.obdreaderv2.command.FuelTrimObdCommand;
import org.obdreaderv2.command.IntakeManifoldPressureObdCommand;
import org.obdreaderv2.command.MassAirFlowObdCommand;
import org.obdreaderv2.command.ObdCommand;
import org.obdreaderv2.command.PressureObdCommand;
import org.obdreaderv2.command.SpeedObdCommand;
import org.obdreaderv2.command.TempObdCommand;
import org.obdreaderv2.command.ThrottleObdCommand;

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
