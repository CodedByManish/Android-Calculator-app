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
            String aboutHtml =
                    "<h2>Advanced Calculator v1.0.4</h2><br>" +
                            "<p>A fast, precise, and modern scientific calculator designed for Android users who value simplicity and accuracy.</p><br>" +

                            "<b>Core Features</b><br>" +
                            "<ul>" +
                            "<li><b>exp4j Engine:</b> High-performance expression parsing and evaluation.</li>" +
                            "<li><b>BigDecimal Precision:</b> Ensures accurate results for complex calculations.</li>" +
                            "<li><b>Material Design 3:</b> Clean, modern, and adaptive user interface.</li>" +
                            "</ul><br>" +

                            "<p>This app is built to deliver a smooth, ad-free, and reliable calculation experience for students, engineers, and professionals.</p>" +

                            "<br><b>Developer:</b> Manish Kafle <br>" +
                            "<b>Build:</b> Stable Release 2026";
            tvContent.setText(Html.fromHtml(aboutHtml, Html.FROM_HTML_MODE_COMPACT));

        } else if ("privacy".equals(type)) {
            tvTitle.setText("Privacy Policy");
            String privacyHtml =
                    "<h3>Your Privacy Matters</h3><br>" +
                            "<p>We believe your data belongs to you. This app follows a <b>Zero-Data Collection</b> approach.</p><br>" +

                            "<b>1. No Data Tracking</b><br>" +
                            "We do not track your calculations, location, or device information.<br><br>" +

                            "<b>2. Local Storage</b><br>" +
                            "Your history is stored only on your device and is never uploaded anywhere.<br><br>" +

                            "<b>3. Offline First</b><br>" +
                            "The app works completely offline and does not require internet access.<br><br>" +

                            "<b>4. Permissions</b><br>" +
                            "We only use essential system features like haptic feedback to improve usability.";
            tvContent.setText(Html.fromHtml(privacyHtml, Html.FROM_HTML_MODE_COMPACT));
        }
    }
}