package com.mobica.beacondemo.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.MenuItem;

import com.mobica.beacondemo.BeaconApplication;
import com.mobica.beacondemo.R;
import com.mobica.beacondemo.DiscoveryManager;
import com.mobica.beacondemo.config.ConfigStorage;
import com.mobica.discoverysdk.DiscoveryMode;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by woos on 2015-11-13.
 */
public class BluetoothPreferenceFragment extends PreferenceFragment {
    private SwitchPreference autoSwitchModeSwitch;
    private MultiSelectListPreference autoSwitchModes;

    @Inject
    DiscoveryManager discoveryManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BeaconApplication.getGraph().inject(this);

        addPreferencesFromResource(R.xml.pref_bluetooth);
        setHasOptionsMenu(true);

        autoSwitchModeSwitch = (SwitchPreference) findPreference("bt_auto_mode_switch");
        autoSwitchModes = (MultiSelectListPreference) findPreference("bt_auto_switch_modes");

        bindPropertyListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDiscoveryModes(boolean enabled, EnumSet<DiscoveryMode> modes) {
        if (enabled == ConfigStorage.bleAutoModeEnabled.get() && modes.equals(ConfigStorage.bleSwitchModes.get())) {
            // no changes, skip
            return;
        }

        if (enabled) {
            discoveryManager.updateModes(modes);
        } else {
            discoveryManager.updateModes(EnumSet.noneOf(DiscoveryMode.class));
        }
    }

    private void bindPropertyListeners() {
        autoSwitchModeSwitch.setOnPreferenceChangeListener(bleAutoModeSwitchListener);
        bleAutoModeSwitchListener.onPreferenceChange(autoSwitchModeSwitch, ConfigStorage.bleAutoModeEnabled.get());

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        autoSwitchModes.setOnPreferenceChangeListener(bleAutoModesListListener);
        bleAutoModesListListener.onPreferenceChange(autoSwitchModes,
                sharedPreferences.getStringSet(autoSwitchModes.getKey(), new HashSet<String>()));
    }

    private final Preference.OnPreferenceChangeListener bleAutoModeSwitchListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    final boolean on = (Boolean) value;
                    autoSwitchModes.setEnabled(on);
                    updateDiscoveryModes(on, ConfigStorage.bleSwitchModes.get());

                    return true;
                }
            };

    private final Preference.OnPreferenceChangeListener bleAutoModesListListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    final Set<String> values = (Set<String>) value;
                    MultiSelectListPreference listPreference = (MultiSelectListPreference) preference;
                    String summary = "";

                    for (String v : values) {
                        int index = listPreference.findIndexOfValue(v);
                        if (index >= 0) {
                            if (!summary.isEmpty()) {
                                summary += ", ";
                            }
                            summary += listPreference.getEntries()[index];
                        }
                    }

                    preference.setSummary(summary);
                    updateDiscoveryModes(ConfigStorage.bleAutoModeEnabled.get(), Mapping.mapDiscoveryModes(values));

                    return true;
                }
            };
}
