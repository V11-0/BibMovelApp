package com.bibmovel.client.settings;

import android.content.Intent;
import android.os.Bundle;

import com.bibmovel.client.BuildConfig;
import com.bibmovel.client.R;
import com.bibmovel.client.services.DownloadService;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.snackbar.Snackbar;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        Preference update = findPreference("pref_update");
        update.setSummary("Versão " + BuildConfig.VERSION_NAME);
        update.setOnPreferenceClickListener(preference -> {

            preference.setEnabled(false);

            Snackbar.make(getView(), "Verificando por Atualizações", Snackbar.LENGTH_LONG).show();

            Intent download = new Intent(getActivity(), DownloadService.class);
            download.putExtra("isBook", false);

            // TODO: 09/11/18 Verificar versão antes

            getContext().startService(download);

            return true;
        });

        Preference about = findPreference("pref_licenses");
        about.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getContext(), OssLicensesMenuActivity.class));
            return false;
        });
    }
}
