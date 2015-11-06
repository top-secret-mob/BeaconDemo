package com.mobica.beacondemo;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else if (preference instanceof MultiSelectListPreference && value instanceof Set) {
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
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(preference.getContext());

        // Trigger the listener immediately with the preference's
        // current value.
        if (preference instanceof MultiSelectListPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    sharedPreferences.getStringSet(preference.getKey(), new HashSet<String>()));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    sharedPreferences.getString(preference.getKey(), ""));
        }
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || BluetoothPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows bluetooth preferences
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class BluetoothPreferenceFragment extends PreferenceFragment {


        private SwitchPreference autoSwitchModeSwitch;
        private MultiSelectListPreference autoSwitchModes;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_bluetooth);
            setHasOptionsMenu(true);

            autoSwitchModeSwitch = (SwitchPreference) findPreference("bt_auto_mode_switch");
            autoSwitchModes = (MultiSelectListPreference) findPreference("bt_auto_switch_modes");
            bindPreferenceSummaryToValue(autoSwitchModes);

            bindSwitchWithList();
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

        private void bindSwitchWithList() {
            autoSwitchModeSwitch.setOnPreferenceChangeListener(bindAutoSwitchWithModeList);
            bindAutoSwitchWithModeList.onPreferenceChange(autoSwitchModeSwitch,
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getBoolean(autoSwitchModeSwitch.getKey(), false));
        }

        private final Preference.OnPreferenceChangeListener bindAutoSwitchWithModeList =
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        final boolean on = (Boolean) o;
                        autoSwitchModes.setEnabled(on);
                        return true;
                    }
                };
    }
}
