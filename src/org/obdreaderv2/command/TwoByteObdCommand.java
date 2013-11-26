package org.obdreaderv2.command;

import android.content.Context;

public abstract class TwoByteObdCommand extends IntObdCommand {

	public TwoByteObdCommand(TwoByteObdCommand other){
		super(other);
	}

	public TwoByteObdCommand(String cmd, String desc, String resType, String impType, Context ctx) {
		super(cmd,desc,resType,impType,ctx);
	}

	public String formatResult() {
		String res = getResult();
		String[] ress = res.split("\r");
		res = ress[0].replace(" ","");
		if ("NODATA".equals(res)) {
			return "NODATA";
		}
		String byteStrOne = res.substring(4,6);
		String byteStrTwo = res.substring(6,8);
		int a = Integer.parseInt(byteStrOne,16);
		int b = Integer.parseInt(byteStrTwo,16);
		intValue = transform(a,b);
		return String.format("%d", intValue);
	}
	protected int transform(int a, int b) {
		return (int)((double)(a*256+b)/4.0);
	}
}
