package org.obdreaderv2.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class ObdCommand {

	protected InputStream in = null;
	protected OutputStream out = null;
	protected ArrayList<Byte> buff = null;
	protected String cmd = null;
	protected String desc = null;
	protected String resType = null;
	protected Exception error;
	protected Object rawValue = null;
	protected HashMap<String,Object> data = null;
	protected String impType = null;
	protected boolean isAtCommand;
	protected Context ctx;

	public ObdCommand(String cmd, String desc, String resType, String impType, Context ctx) {
		this.cmd = cmd;
		this.desc = desc;
		this.resType = resType;
		this.buff = new ArrayList<Byte>();
		this.impType = impType;
		this.isAtCommand = false;
		this.ctx = ctx;
	}
	public void setBuff(ArrayList<Byte> buff) {
		this.buff = buff;
	}
	public boolean isImperial() {
		return false;
	}
	public ObdCommand(ObdCommand other) {
		this(other.cmd, other.desc, other.resType, other.impType, other.ctx);
	}
	public void setInputStream(InputStream in) {
		this.in = in;
	}
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}
	public void run() throws IOException {
		Log.e("write",cmd);
		sendCmd(cmd);
		readResult();
		Log.e("read",getResult());
	}
	protected void sendCmd(String cmd) throws IOException {
		cmd += "\r\n";
		while (in.available() > 0) {
			in.read();
		}
		out.write(cmd.getBytes());
		out.flush();
	}
	protected void readResult() throws IOException {
		this.buff.clear();
		while (true) {
			int b = in.read();
			if (b == -1) {
				break;
			}
			char ch = (char)b;
			if (ch == '>') {
				break;
			}
			buff.add((byte)b);
		}
		while (in.available() > 0) {
			in.read();
		}
	}
	public String getResult() {
		return new String(getByteArray());
	}
	public byte[] getByteArray() {
		byte[] data = new byte[this.buff.size()];
		for (int i = 0; i < this.buff.size(); i++) {
			data[i] = this.buff.get(i);
		}
		return data;
	}
	public String formatResult() {
		String res = getResult();
		String[] ress = res.split("\r");
		res = ress[0].replace(" ","");
		return res;
	}
	public InputStream getIn() {
		return in;
	}
	public OutputStream getOut() {
		return out;
	}
	public ArrayList<Byte> getBuff() {
		return buff;
	}
	public String getCmd() {
		return cmd;
	}
	public String getDesc() {
		return desc;
	}
	public String getResType() {
		return resType;
	}
	public void setError(Exception e) {
		error = e;
	}
	public Exception getError() {
		return error;
	}
	public Object getRawValue() {
		return rawValue;
	}
	public String prettyPrintResult(String r) {
		return r;
	}
	public boolean isUnableToConnect() {
		String r = getResult();
		if (r != null && r.toUpperCase().contains("UNABLE")) {
			return true;
		}
		return false;
	}
	public boolean isNoData() {
		String r = getResult();
		if (r != null && 
				(r.toUpperCase().contains("NODATA") ||
					r.toUpperCase().contains("NO DATA"))) {
			return true;
		}
		return false;
	}
	public boolean isAtCommand() {
		return isAtCommand;
	}
}
