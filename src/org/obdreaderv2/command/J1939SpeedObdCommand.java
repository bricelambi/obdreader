package org.obdreaderv2.command;

import org.obdreaderv2.io.ObdReader;

import android.content.Context;
import android.util.Log;

public class J1939SpeedObdCommand extends Obd1939Command {

	public J1939SpeedObdCommand(Context ctx) {
		super("00fef1",ObdReader.SPEED,"km/h","km/h",ctx);
	}
	public String formatResult() {
		String res = getResult();
		Log.e("res",res);
		res = res.replace('\r', ',').replace('\n', ',');
		int v = 0;
		String[] strs = res.split(",");
		for (int i = 0; i < strs.length; i++) {
			String line = strs[i];
			String[] byteStrings = line.split(" ");
			String aStr = byteStrings[2];
			String bStr = byteStrings[1];
			if ("FF".equals(aStr.toUpperCase()) &&
					"FF".equals(bStr.toUpperCase())) {
				continue;
			}
			String wordStr = aStr+bStr;
			v = Integer.parseInt(wordStr,16);
			v = (int)(v * 0.00390625);
			Log.e("1939speed",""+v);
			break;
		}
		return Integer.toString(v);
	}
}
