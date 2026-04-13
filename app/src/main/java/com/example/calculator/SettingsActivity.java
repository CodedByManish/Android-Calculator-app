package com.example.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton btnBack = findViewById(R.id.btnBackSettings);
        SwitchMaterial switchVibration = findViewById(R.id.switchVibration);
        SwitchMaterial switchScreenOn = findViewById(R.id.switchScreenOn);

        SharedPreferences sp = getSharedPreferences("calc_prefs", MODE_PRIVATE);

        // Load current states (Vibration default true, Screen default false)
        switchVibration.setChecked(sp.getBoolean("vibration", true));
        switchScreenOn.setChecked(sp.getBoolean("screen_on", false));

        btnBack.setOnClickListener(v -> finish());

        switchVibration.setOnCheckedChangeListener((v, isChecked) ->
                sp.edit().putBoolean("vibration", isChecked).apply());

        switchScreenOn.setOnCheckedChangeListener((v, isChecked) -> {
            sp.edit().putBoolean("screen_on", isChecked).apply();
            updateScreenStatus(isChecked);
        });
    }

    private void updateScreenStatus(boolean keepOn) {
        if (keepOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}