package org.obdreader.db;

import java.util.ArrayList;
import java.util.List;

public class EngineCodeObject {

	private String engineCode;
	private long startTime;
	
	public EngineCodeObject(String engineCode, long startTime) {
		super();
		this.engineCode = engineCode;
		this.startTime = startTime;
	}
	public String getEngineCode() {
		return engineCode;
	}
	public void setEngineCode(String engineCode) {
		this.engineCode = engineCode;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public static List<String> getCodeStrings(List<EngineCodeObject> objs) {
		List<String> codes = new ArrayList<String>();
		for (int i = 0; i < objs.size(); i++) {
			EngineCodeObject obj = objs.get(i);
			if (obj != null) {
				String code = obj.getEngineCode();
				if (code != null && !"".equals(code)) {
					codes.add(code);
				}
			}
		}
		return codes;
	}
}
