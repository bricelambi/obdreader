package org.obdreader.command;

import android.content.Context;
import android.util.Log;

public class DtcNumberObdCommand extends ObdCommand {

	private int codeCount = -1;
	private boolean milOn = false;
	public DtcNumberObdCommand(Context ctx) {
		super("0101","DTC Status","","",ctx);
	}
	public DtcNumberObdCommand(DtcNumberObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		String byte1 = res.substring(4,6);
		String byte2 = res.substring(6,8);
		String byte3 = res.substring(8,10);
		String byte4 = res.substring(10,12);
		int A = Integer.parseInt(byte1,16);
		int B = Integer.parseInt(byte2,16);
		int C = Integer.parseInt(byte3,16);
		int D = Integer.parseInt(byte4,16);
		boolean mil = false;
		String result = "";
		Log.e("mil", "" + A);
		Log.e("mil", res);
		if ((A & 0x80) == 1) {
			mil = true;
			result += "MIL is on";
		} else {
			result += "MIL is off";
		}
		int num = A & 0x7f;
		for (int i = 0; i < 3; i++) {
			int code = ((B>>i)&0x01)+((B>>(3+i))&0x02);
			result += ", " + code;
		}
		for (int i = 0; i < 7; i++) {
			int code = ((C>>i)&0x01)+(((D>>i)&0x01)<<1);
		}
		int egrCode = ((D>>7)&0x01);
		return result;
	}
	public int getCodeCount() {
		return codeCount;
	}
	public boolean getMilOn() {
		return milOn;
	}
}
