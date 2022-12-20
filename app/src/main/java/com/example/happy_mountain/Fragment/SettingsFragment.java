package com.example.happy_mountain.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.happy_mountain.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference connectStatus;
    private ListPreference actionMode;
    private SwitchPreference controlMode;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        if (rootKey == null) {
            connectStatus = findPreference("connectStatus");
            actionMode = findPreference("actionMode");
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String mode = sharedPreferences.getString("actionMode", "auto_mode");
        Log.d("hello", mode);
    }

    public ListPreference getActionMode() {
        return actionMode;
    }

    public void setActionMode(String mode) {
        this.actionMode.setValue(mode);
    }
}