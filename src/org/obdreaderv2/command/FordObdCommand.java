package org.obdreaderv2.command;

import android.content.Context;

public class FordObdCommand extends ObdCommand {

	public FordObdCommand(FordObdCommand other) {
		super(other);
	}
	public FordObdCommand(String cmd, String desc, String resType, String impType, Context ctx) {
		super(cmd,desc,resType,impType,ctx);
	}
	@Override
	public void run() {
		try {
			//ObdCommand atsh = new ObdCommand("atsh000760","set header","","");
			//runCmd(atsh);
			sendCmd(cmd);
			readResult();
			//atsh = new ObdCommand("atsh0007DF","set header","","");
			//runCmd(atsh);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
