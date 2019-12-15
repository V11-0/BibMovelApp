package com.bibmovel.client.settings;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by vinibrenobr11 on 30/10/2018 at 11:36
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
