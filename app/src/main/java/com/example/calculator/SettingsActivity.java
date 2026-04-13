package com.example.calculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sp = getSharedPreferences("calc_prefs", MODE_PRIVATE);

        findViewById(R.id.btnBackSettings).setOnClickListener(v -> finish());

        SwitchMaterial switchScreenOn = findViewById(R.id.switchScreenOn);

        // Initial state
        boolean isScreenOn = sp.getBoolean("screen_on", false);
        switchScreenOn.setChecked(isScreenOn);
        updateLocalScreenFlag(isScreenOn);

        switchScreenOn.setOnCheckedChangeListener((v, isChecked) -> {
            sp.edit().putBoolean("screen_on", isChecked).apply();
            updateLocalScreenFlag(isChecked);
        });

        findViewById(R.id.tvAbout).setOnClickListener(v ->
                startActivity(new Intent(this, InfoActivity.class).putExtra("type", "about")));

        findViewById(R.id.tvPrivacy).setOnClickListener(v ->
                startActivity(new Intent(this, InfoActivity.class).putExtra("type", "privacy")));
    }

    private void updateLocalScreenFlag(boolean keepOn) {
        if (keepOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}