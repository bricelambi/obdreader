package org.obdreader.activity;

import java.util.ArrayList;
import java.util.Set;

import org.obdreader.R;
import org.obdreader.command.ObdCommand;
import org.obdreader.config.ObdConfig;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class ObdReaderConfigActivity extends PreferenceActivity implements OnPreferenceChangeListener {

	public static final String BLUETOOTH_LIST_KEY = "bluetooth_list_preference";
	public static final String UPLOAD_URL_KEY = "upload_url_preference";
	public static final String UPLOAD_DATA_KEY = "upload_data_preference";
	public static final String UPDATE_PERIOD_KEY = "update_period_preference";
	public static final String PERSIST_PERIOD_KEY = "persist_period_preference";
	public static final String VEHICLE_ID_KEY = "vehicle_id_preference";
	public static final String ENGINE_DISPLACEMENT_KEY = "engine_displacement_preference";
	public static final String VOLUMETRIC_EFFICIENCY_KEY = "volumetric_efficiency_preference";
	public static final String IMPERIAL_UNITS_KEY = "imperial_units_preference";
	public static final String COMMANDS_SCREEN_KEY = "obd_commands_screen";
	public static final String ENABLE_GPS_KEY = "enable_gps_preference";
	public static final String MAX_DATA_AGE_KEY = "max_data_age_preference";
	public static final String DATA_DIR_KEY = "data_dir_preference";
	public static final String LOG_CSV_KEY = "log_csv_preference";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		ArrayList<CharSequence> pairedDeviceStrings = new ArrayList<CharSequence>();
        ArrayList<CharSequence> vals = new ArrayList<CharSequence>();
		ListPreference listPref = (ListPreference) getPreferenceScreen().findPreference(BLUETOOTH_LIST_KEY);

		getPreferenceScreen().findPreference(UPDATE_PERIOD_KEY).setOnPreferenceChangeListener(intPrefChangeListener);
		getPreferenceScreen().findPreference(PERSIST_PERIOD_KEY).setOnPreferenceChangeListener(intPrefChangeListener);
		getPreferenceScreen().findPreference(MAX_DATA_AGE_KEY).setOnPreferenceChangeListener(intPrefChangeListener);

		final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
        	listPref.setEntries(pairedDeviceStrings.toArray(new CharSequence[0]));
            listPref.setEntryValues(vals.toArray(new CharSequence[0]));
            Toast.makeText(this,"This device does not support bluetooth", Toast.LENGTH_LONG).show();
        	return;
        }
        listPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				return true;
			}
		});
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceStrings.add(device.getName() + "\n" + device.getAddress());
                vals.add(device.getAddress());
            }
        }
        listPref.setEntries(pairedDeviceStrings.toArray(new CharSequence[0]));
        listPref.setEntryValues(vals.toArray(new CharSequence[0]));
        ArrayList<ObdCommand> cmds = ObdConfig.getCommands(this);
        PreferenceScreen cmdScr = (PreferenceScreen) getPreferenceScreen().findPreference(COMMANDS_SCREEN_KEY);
        for (int i = 0; i < cmds.size(); i++) {
        	ObdCommand cmd = cmds.get(i);
        	CheckBoxPreference cpref = new CheckBoxPreference(this);
        	cpref.setTitle(cmd.getDesc());
        	cpref.setKey(cmd.getDesc());
        	cpref.setChecked(true);
        	cmdScr.addPreference(cpref);
        }
	}
	
	protected final OnPreferenceChangeListener intPrefChangeListener = new OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			try {
				Integer.parseInt(newValue.toString());
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	};
	
	protected final OnPreferenceChangeListener doublePrefChangeListener = new OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			try {
				Double.parseDouble(newValue.toString());
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	};

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (UPDATE_PERIOD_KEY.equals(preference.getKey()) || 
				VOLUMETRIC_EFFICIENCY_KEY.equals(preference.getKey()) ||
				ENGINE_DISPLACEMENT_KEY.equals(preference.getKey())) {
			try {
				Double.parseDouble(newValue.toString());
				return true;
			} catch (Exception e) {
				Toast.makeText(this,"Couldn't parse '" + newValue.toString() + "' as an number.",Toast.LENGTH_LONG).show();
			}
		}
		return false;
	}

	public static boolean getLogToCsv(Context ctx) {
		return getBool(LOG_CSV_KEY,ctx,true);
	}
	public static int getUpdatePeriod(Context ctx) {
		return getInt(UPDATE_PERIOD_KEY, 
				ctx, 
				ctx.getResources().getInteger(R.integer.default_update_period_sec));
    }
	public static String getDataDir(Context ctx) {
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(DATA_DIR_KEY, "obd_data");
	}
	public static int getPersistPeriod(Context ctx) {
		return getInt(PERSIST_PERIOD_KEY, ctx, 8);
	}

    public static double getVolumetricEfficieny(Context ctx) {
    	return getDouble(VOLUMETRIC_EFFICIENCY_KEY, ctx, 0.85);
    }
    public static double getEngineDisplacement(Context ctx) {
    	return getDouble(ENGINE_DISPLACEMENT_KEY, ctx, 1.6);
    }

    public static ArrayList<ObdCommand> getObdCommands(Context ctx) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    	ArrayList<ObdCommand> cmds = ObdConfig.getCommands(ctx);
    	ArrayList<ObdCommand> ucmds = new ArrayList<ObdCommand>();
    	for (int i = 0; i < cmds.size(); i++) {
    		ObdCommand cmd = cmds.get(i);
    		boolean selected = prefs.getBoolean(cmd.getDesc(), true);
    		if (selected) {
    			ucmds.add(cmd);
    		}
    	}
    	return ucmds;
    }

	public static String getBluetootheDevice(Context ctx) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		return prefs.getString(ObdReaderConfigActivity.BLUETOOTH_LIST_KEY, null);
	}
	
	public static double getDouble(String key, Context ctx, double defValue) {
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
			String valStr = prefs.getString(key, Double.toString(defValue));
			double val = Double.parseDouble(valStr);
			return val;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defValue;
	}

	public static int getInt(String key, Context ctx, int defValue) {
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
			String valStr = prefs.getString(key, Integer.toString(defValue));
			int val = Integer.parseInt(valStr);
			return val;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defValue;
	}
	public static boolean getBool(String key, Context ctx, boolean defValue) {
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
			return prefs.getBoolean(key, defValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defValue;
	}

	public static int getMaxDataAgeMinutes(Context ctx) {
		int defValue = 4320;
		return getInt(MAX_DATA_AGE_KEY, ctx, defValue);
	}
}
