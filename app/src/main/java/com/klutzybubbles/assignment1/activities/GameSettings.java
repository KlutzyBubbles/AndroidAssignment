package com.klutzybubbles.assignment1.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class GameSettings extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener preferenceChange = (preference, value) -> {
        String stringValue = value.toString();
        Log.i("GameSettings:onPrefChg", "stringValue = " + stringValue);
        try {
            Log.i("GameSettings:onPrefChg", "int Value = " + (int) value);
        } catch (ClassCastException e) {
            Log.e("GameSettings:onPrefChg", "Unable to cast preference to an Integer");
        }
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);
        } else
            preference.setSummary(stringValue);
        return true;
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #preferenceChange
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(preferenceChange);
        preferenceChange.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), "3"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_activity_game_settings);
        }
        this.getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
    }


    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     *
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("blank"));
            bindPreferenceSummaryToValue(findPreference("color_a"));
            bindPreferenceSummaryToValue(findPreference("color_b"));
            bindPreferenceSummaryToValue(findPreference("highlight"));
            bindPreferenceSummaryToValue(findPreference("border"));
            bindPreferenceSummaryToValue(findPreference("size"));
            bindPreferenceSummaryToValue(findPreference("difficulty"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), MainMenu.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
