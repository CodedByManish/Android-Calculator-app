package com.example.calculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private EditText etEquation;
    private boolean isRadianMode = false;
    private boolean shouldClearHeader = false;
    private boolean isFormatting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tvDisplay);
        etEquation = findViewById(R.id.etEquation);
        etEquation.setShowSoftInputOnFocus(false);

        ImageButton btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> showPopupMenu(v));

        SwitchMaterial modeToggle = findViewById(R.id.modeToggle);
        modeToggle.setOnCheckedChangeListener((v, isChecked) -> {
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

    private void showPopupMenu(android.view.View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_history) {
                Toast.makeText(this, "History clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_settings) {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
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
            if (part.matches("[0-9.]+")) {
                formatted.append(formatNumberWithCommas(part));
            } else {
                formatted.append(part);
            }
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
                String formattedInt = NumberFormat.getNumberInstance(Locale.US).format(integerPart);
                return formattedInt + "." + (split.length > 1 ? split[1] : "");
            } else {
                return NumberFormat.getNumberInstance(Locale.US).format(new BigDecimal(number));
            }
        } catch (Exception e) {
            return number;
        }
    }

    private void doRealTimeEval() {
        String input = etEquation.getText().toString().replace(",", "");
        if (input.isEmpty()) { tvDisplay.setText(""); return; }

        try {
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
            double res = e.evaluate();
            tvDisplay.setText(formatResult(res));
        } catch (ArithmeticException e) {
            tvDisplay.setText("Cannot divide by 0");
        } catch (Exception e) {
            tvDisplay.setText(""); // Keep blank during real-time typing errors
        }
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
        for (int id : ids) findViewById(id).setOnClickListener(v -> insertText(((Button)v).getText().toString()));

        findViewById(R.id.btnSin).setOnClickListener(v -> insertText("sin("));
        findViewById(R.id.btnCos).setOnClickListener(v -> insertText("cos("));
        findViewById(R.id.btnTan).setOnClickListener(v -> insertText("tan("));
        findViewById(R.id.btnLog).setOnClickListener(v -> insertText("log10("));
        findViewById(R.id.btnLn).setOnClickListener(v -> insertText("ln("));
        findViewById(R.id.btnRoot).setOnClickListener(v -> insertText("sqrt("));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> insertText("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> insertText("/"));
        findViewById(R.id.btnPi).setOnClickListener(v -> insertText("π"));
        findViewById(R.id.btnE).setOnClickListener(v -> insertText("e"));
        findViewById(R.id.btnAC).setOnClickListener(v -> { etEquation.setText(""); tvDisplay.setText(""); });
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            int pos = etEquation.getSelectionStart();
            if (pos > 0) etEquation.getText().delete(pos - 1, pos);
        });
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculate());
    }

    private void insertText(String str) {
        if (shouldClearHeader) {
            etEquation.setText("");
            shouldClearHeader = false;
        }
        int pos = etEquation.getSelectionStart();
        String visual = str.replace("*", "×").replace("/", "÷").replace("log10(", "log(").replace("sqrt(", "√(");
        etEquation.getText().insert(pos, visual);
    }

    private void calculate() {
        String result = tvDisplay.getText().toString();
        if (result.isEmpty()) return;

        if (result.equals("Cannot divide by 0") || result.equals("Expression Error")) {
            // Keep the error in the display but don't move it up
            shouldClearHeader = true;
        } else {
            // Move result to equation and clear display
            etEquation.setText(result);
            etEquation.setSelection(etEquation.getText().length());
            tvDisplay.setText("");
            shouldClearHeader = false; // Reset so user can keep typing from the result
        }
    }
}