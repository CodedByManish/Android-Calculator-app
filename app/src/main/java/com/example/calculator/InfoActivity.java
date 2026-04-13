package com.example.calculator;

import android.os.Bundle;
import android.text.Html;
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
            String aboutHtml = "<h3>Advanced Calculator v1.0.3</h3>" +
                    "<p>A precision-focused scientific tool built for the modern Android experience.</p>" +
                    "<b>Key Technologies:</b>" +
                    "<ul>" +
                    "<li><b>exp4j Engine:</b> For high-speed expression parsing.</li>" +
                    "<li><b>BigDecimal:</b> To ensure financial-grade precision.</li>" +
                    "<li><b>Material Design 3:</b> For a clean, adaptive UI.</li>" +
                    "</ul>" +
                    "<p>Our mission is to provide a reliable, ad-free, and beautiful calculating experience for students, engineers, and scientists.</p>" +
                    "<br><b>Developer:</b> [Your Name]<br>" +
                    "<b>Build:</b> Stable Release 2026";
            tvContent.setText(Html.fromHtml(aboutHtml, Html.FROM_HTML_MODE_COMPACT));

        } else if ("privacy".equals(type)) {
            tvTitle.setText("Privacy Policy");
            String privacyHtml = "<h3>Your Privacy Matters</h3>" +
                    "<p>We believe that your data belongs to you. This app is designed with a <b>Zero-Data Collection</b> philosophy.</p>" +
                    "<b>1. No Data Tracking</b><br>" +
                    "We do not track your calculations, location, or device ID. Everything stays on your phone.<br><br>" +
                    "<b>2. Local Storage</b><br>" +
                    "Your history is stored in a local private database. It is never uploaded to any server.<br><br>" +
                    "<b>3. Offline First</b><br>" +
                    "This app works 100% offline. It does not require internet permissions to function.<br><br>" +
                    "<b>4. Permissions</b><br>" +
                    "We only use standard system permissions (like Haptic Feedback) to improve your user experience.";
            tvContent.setText(Html.fromHtml(privacyHtml, Html.FROM_HTML_MODE_COMPACT));
        }
    }
}