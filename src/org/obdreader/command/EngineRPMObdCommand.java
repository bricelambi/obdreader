package org.obdreader.command;

import org.obdreader.io.ObdReader;

import android.content.Context;

public class EngineRPMObdCommand extends TwoByteObdCommand {

	public EngineRPMObdCommand(Context ctx) {
		super("010C",ObdReader.RPM,"RPM","RPM",ctx);
	}
	public EngineRPMObdCommand(EngineRPMObdCommand other) {
		super(other);
	}
}
