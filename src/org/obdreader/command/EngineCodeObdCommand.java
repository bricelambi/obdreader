package org.obdreader.command;

import java.util.ArrayList;
import java.util.List;

import org.obdreader.io.ObdReader;

import android.content.Context;
import android.util.Log;

public class EngineCodeObdCommand extends ObdCommand {

	public EngineCodeObdCommand(Context ctx) {
		super("03",ObdReader.ENGINE_CODES,"","",ctx);
	}
	public EngineCodeObdCommand(EngineCodeObdCommand other) {
		super(other);
	}

	@Override
	public String formatResult() {
		String res = getResult();
		res = res.replace("\r","").replace("\n","").replace(" ","");
		return res;
	}

	public static List<String> getEngineCodes(String res) {
		List<String> codes = new ArrayList<String>();
		int n = -1;
		try {
			String Bstr = res.substring(2,4);
			n = Integer.parseInt(Bstr);
		} catch (Exception e) {
			Log.e("enginecodes","couldn't parse number of codes: " + res);
			e.printStackTrace();
		}
		for (int i = 0; i < n; i++) {
			int start = 4 + i * 4;
			try {
				String code = res.substring(start,start+4);
				codes.add(code);
			} catch (Exception e) {
				Log.e("enginecodes","couldn't parse engine code: " + res);
				e.printStackTrace();
			}
		}
		return codes;
	}
}
