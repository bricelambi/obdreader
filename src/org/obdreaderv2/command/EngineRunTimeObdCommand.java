package org.obdreaderv2.command;

import org.obdreaderv2.io.ObdReader;

import android.content.Context;



public class EngineRunTimeObdCommand extends ObdCommand {

	public EngineRunTimeObdCommand(Context ctx) {
		super("011F",ObdReader.RUN_TIME,"","",ctx);
	}
	public EngineRunTimeObdCommand(EngineRunTimeObdCommand other) {
		super(other);
	}
	@Override
	public String formatResult() {
		String res = super.formatResult();
		/*if ("NODATA".equals(res)) {
			return "NODATA";
		}*/
//		Log.e("read",res);
		String A = res.substring(4,6);
		String B = res.substring(6,8);
		int a = Integer.parseInt(A,16);
		int b = Integer.parseInt(B,16);
		int sec = (a*256)+b;
//		String hh = String.format("%02d", sec/3600);
//		String mm = String.format("%02d", (sec%3600)/60);
//		String ss = String.format("%02d", sec%60);
//		String time = String.format("%s:%s:%s", hh,mm,ss);
		return Integer.toString(sec);
	}
}
