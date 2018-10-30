package com.bibmovel.client.settings;

import android.os.Bundle;

import com.bibmovel.client.R;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        Preference update = findPreference("pref_update");
        update.setOnPreferenceClickListener(preference -> {
            // TODO: 30/10/2018 Verificar updates

            return true;
        });
    }
}
