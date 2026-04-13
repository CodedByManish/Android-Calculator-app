package com.example.calculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private EditText etEquation;
    private boolean isRadianMode = false;
    private boolean shouldClearHeader = false;
    private boolean isFormatting = false;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("calc_prefs", MODE_PRIVATE);
        applyScreenSetting();

        tvDisplay = findViewById(R.id.tvDisplay);
        etEquation = findViewById(R.id.etEquation);
        etEquation.setShowSoftInputOnFocus(false);

        ImageButton btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            handleVibration();
            showPopupMenu(v);
        });

        SwitchMaterial modeToggle = findViewById(R.id.modeToggle);
        modeToggle.setOnCheckedChangeListener((v, isChecked) -> {
            handleVibration();
            isRadianMode = isChecked;
            doRealTimeEval();
        });

        etEquation.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFormatting) return;
                doRealTimeEval();
            }
            @Override public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                applyCommaFormatting(s);
            }
        });

        setupButtons();
    }

    @Override protected void onResume() { super.onResume(); applyScreenSetting(); }

    private void applyScreenSetting() {
        if (sp.getBoolean("screen_on", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void handleVibration() {
        // We still keep the logic here, but the setting toggle will be removed as requested
        if (sp.getBoolean("vibration", true)) {
            etEquation.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }

    private void showPopupMenu(android.view.View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            } else if (id == R.id.action_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void applyCommaFormatting(Editable s) {
        isFormatting = true;
        String original = s.toString().replace(",", "");
        StringBuilder formatted = new StringBuilder();
        String[] parts = original.split("(?<=[^0-9.])|(?=[^0-9.])");
        for (String part : parts) {
            if (part.matches("[0-9.]+")) { formatted.append(formatNumberWithCommas(part)); }
            else { formatted.append(part); }
        }
        int cursorPosition = etEquation.getSelectionStart();
        int oldLen = s.length();
        etEquation.setText(formatted.toString());
        int newLen = etEquation.getText().length();
        etEquation.setSelection(Math.max(0, Math.min(cursorPosition + (newLen - oldLen), newLen)));
        isFormatting = false;
    }

    private String formatNumberWithCommas(String number) {
        try {
            if (number.contains(".")) {
                String[] split = number.split("\\.");
                BigDecimal integerPart = new BigDecimal(split[0]);
                return NumberFormat.getNumberInstance(Locale.US).format(integerPart) + "." + (split.length > 1 ? split[1] : "");
            } else {
                return NumberFormat.getNumberInstance(Locale.US).format(new BigDecimal(number));
            }
        } catch (Exception e) { return number; }
    }

    private void doRealTimeEval() {
        String input = etEquation.getText().toString().replace(",", "");
        if (input.isEmpty()) { tvDisplay.setText(""); return; }
        try {
            tvDisplay.setText(evaluateExpression(input));
        } catch (Exception e) {
            tvDisplay.setText(""); // Keep clean during typing
        }
    }

    private String evaluateExpression(String input) throws Exception {
        String formula = input.replace("×", "*").replace("÷", "/")
                .replace("log(", "log10(").replace("ln(", "log")
                .replace("√(", "sqrt(").replace("π", "pi").replace("e", "e");
        if (!isRadianMode) {
            formula = formula.replace("sin(", "sin(pi/180*")
                    .replace("cos(", "cos(pi/180*")
                    .replace("tan(", "tan(pi/180*");
        }
        Operator factorial = new Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {
            @Override public double apply(double... args) {
                long arg = (long) args[0];
                double res = 1;
                for (int i = 1; i <= arg; i++) res *= i;
                return res;
            }
        };
        Expression e = new ExpressionBuilder(formula).operator(factorial).build();
        return formatResult(e.evaluate());
    }

    private String formatResult(double res) {
        if (Double.isInfinite(res)) return "Cannot divide by 0";
        if (Double.isNaN(res)) return "Expression Error";
        if (Math.abs(res) >= 1_000_000_000_000_000L || (Math.abs(res) < 0.000001 && res != 0)) {
            return new DecimalFormat("0.######E0").format(res);
        }
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern("#,###.#######");
        return df.format(res);
    }

    private void setupButtons() {
        int[] ids = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnOpen, R.id.btnClose, R.id.btnPlus, R.id.btnMinus, R.id.btnPower, R.id.btnFact};
        for (int id : ids) findViewById(id).setOnClickListener(v -> { handleVibration(); insertText(((Button)v).getText().toString()); });
        findViewById(R.id.btnSin).setOnClickListener(v -> { handleVibration(); insertText("sin("); });
        findViewById(R.id.btnCos).setOnClickListener(v -> { handleVibration(); insertText("cos("); });
        findViewById(R.id.btnTan).setOnClickListener(v -> { handleVibration(); insertText("tan("); });
        findViewById(R.id.btnLog).setOnClickListener(v -> { handleVibration(); insertText("log("); });
        findViewById(R.id.btnLn).setOnClickListener(v -> { handleVibration(); insertText("ln("); });
        findViewById(R.id.btnRoot).setOnClickListener(v -> { handleVibration(); insertText("√("); });
        findViewById(R.id.btnMultiply).setOnClickListener(v -> { handleVibration(); insertText("×"); });
        findViewById(R.id.btnDivide).setOnClickListener(v -> { handleVibration(); insertText("÷"); });
        findViewById(R.id.btnPi).setOnClickListener(v -> { handleVibration(); insertText("π"); });
        findViewById(R.id.btnE).setOnClickListener(v -> { handleVibration(); insertText("e"); });
        findViewById(R.id.btnAC).setOnClickListener(v -> { handleVibration(); etEquation.setText(""); tvDisplay.setText(""); });
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            handleVibration();
            int pos = etEquation.getSelectionStart();
            if (pos > 0) etEquation.getText().delete(pos - 1, pos);
        });
        findViewById(R.id.btnEquals).setOnClickListener(v -> { handleVibration(); calculate(); });
    }

    private void insertText(String str) {
        if (shouldClearHeader) {
            etEquation.setText("");
            shouldClearHeader = false;
        }

        int pos = etEquation.getSelectionStart();
        String currentText = etEquation.getText().toString();

        if (str.matches("[+×÷^\\-]") && pos > 0) {
            char lastChar = currentText.charAt(pos - 1);

            if (String.valueOf(lastChar).matches("[+×÷^\\-]")) {
                if (str.equals("-") && lastChar != '-') {
                    etEquation.getText().insert(pos, str);
                    return;
                }
                etEquation.getText().replace(pos - 1, pos, str);
                return;
            }
        }

        etEquation.getText().insert(pos, str);
    }

    private void calculate() {
        String input = etEquation.getText().toString().replace(",", "");
        if (input.isEmpty()) return;

        try {
            String resStr = evaluateExpression(input);
            if (resStr.equals("Cannot divide by 0")) {
                tvDisplay.setText(resStr);
                shouldClearHeader = true;
            } else {
                saveToHistory(etEquation.getText().toString(), resStr);
                etEquation.setText(resStr);
                etEquation.setSelection(etEquation.getText().length());
                tvDisplay.setText("");
                shouldClearHeader = false;
            }
        } catch (Exception e) {
            tvDisplay.setText("Expression Error");
            shouldClearHeader = true;
        }
    }

    private void saveToHistory(String eq, String res) {
        if (res.contains("Error") || res.contains("divide")) return;
        Gson gson = new Gson();
        String json = sp.getString("history", null);
        List<String[]> list = (json == null) ? new ArrayList<>() : gson.fromJson(json, new TypeToken<List<String[]>>(){}.getType());
        list.add(0, new String[]{eq, res});
        if (list.size() > 50) list.remove(50);
        sp.edit().putString("history", gson.toJson(list)).apply();
    }
}