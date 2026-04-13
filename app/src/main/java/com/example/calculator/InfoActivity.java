package com.example.calculator;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ImageButton btnBack = findViewById(R.id.btnBackInfo);
        TextView tvTitle = findViewById(R.id.tvInfoTitle);
        TextView tvContent = findViewById(R.id.tvInfoContent);

        btnBack.setOnClickListener(v -> finish());

        String type = getIntent().getStringExtra("type");

        if ("about".equals(type)) {
            tvTitle.setText("About Calculator");
            tvContent.setText("Advanced Scientific Calculator v1.0.2\n\n" +
                    "A high-precision tool designed for students and professionals. " +
                    "Built with a focus on clean UI and mathematical accuracy.\n\n" +
                    "Developer: Your Name/Project\n" +
                    "Build Date: April 2026");
        } else if ("privacy".equals(type)) {
            tvTitle.setText("Privacy Policy");
            tvContent.setText("Privacy Policy\n\n" +
                    "This calculator does not collect, store, or share any personal user data.\n\n" +
                    "1. Local Storage: Your calculation history is stored locally on your device only.\n" +
                    "2. Permissions: No internet or contact permissions are required.\n" +
                    "3. Security: We do not track your calculations.");
        }
    }
}