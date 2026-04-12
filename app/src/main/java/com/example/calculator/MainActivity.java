package com.example.calculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private EditText etEquation;
    private boolean isRadianMode = false;
    private boolean shouldClearHeader = false;
    private boolean isFormatting = false; // Prevents infinite loops in TextWatcher

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tvDisplay);
        etEquation = findViewById(R.id.etEquation);
        etEquation.setShowSoftInputOnFocus(false);

        etEquation.setText("");
        tvDisplay.setText("");

        SwitchMaterial modeToggle = findViewById(R.id.modeToggle);
        modeToggle.setOnCheckedChangeListener((v, isChecked) -> {
            isRadianMode = isChecked;
            doRealTimeEval();
        });

        etEquation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFormatting) return; // Skip if we are currently re-formatting
                doRealTimeEval();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                applyCommaFormatting(s);
            }
        });

        int[] ids = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnOpen, R.id.btnClose, R.id.btnPlus, R.id.btnMinus, R.id.btnPower, R.id.btnFact};
        for (int id : ids) {
            findViewById(id).setOnClickListener(v -> insertText(((Button)v).getText().toString()));
        }

        setupFunc(R.id.btnSin, "sin(");
        setupFunc(R.id.btnCos, "cos(");
        setupFunc(R.id.btnTan, "tan(");
        setupFunc(R.id.btnLog, "log10(");
        setupFunc(R.id.btnLn, "ln(");
        setupFunc(R.id.btnRoot, "sqrt(");

        findViewById(R.id.btnMultiply).setOnClickListener(v -> insertText("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> insertText("/"));
        findViewById(R.id.btnPi).setOnClickListener(v -> insertText("π"));
        findViewById(R.id.btnE).setOnClickListener(v -> insertText("e"));

        findViewById(R.id.btnAC).setOnClickListener(v -> {
            etEquation.setText("");
            tvDisplay.setText("");
            shouldClearHeader = false;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            int cursorPosition = etEquation.getSelectionStart();
            if (cursorPosition > 0) {
                etEquation.getText().delete(cursorPosition - 1, cursorPosition);
            }
        });

        findViewById(R.id.btnEquals).setOnClickListener(v -> {
            calculate();
            shouldClearHeader = true;
        });
    }

    private void setupFunc(int id, String text) {
        findViewById(id).setOnClickListener(v -> insertText(text));
    }

    private void insertText(String strToAdd) {
        String currentResult = tvDisplay.getText().toString();

        if (shouldClearHeader) {
            boolean isOperator = strToAdd.matches("[\\+\\-\\*/\\^!×÷]");
            if (isOperator && !currentResult.isEmpty() && !currentResult.equals("Error")) {
                etEquation.setText(currentResult);
                etEquation.setSelection(etEquation.getText().length());
            } else {
                etEquation.setText("");
            }
            shouldClearHeader = false;
        }

        int cursorPosition = etEquation.getSelectionStart();
        String visualStr = strToAdd.replace("*", "×").replace("/", "÷")
                .replace("log10(", "log(").replace("sqrt(", "√(");

        etEquation.getText().insert(cursorPosition, visualStr);
    }

    // New Helper to apply commas to the EditText as you type
    private void applyCommaFormatting(Editable s) {
        isFormatting = true;
        String original = s.toString().replace(",", ""); // Remove existing commas to re-calculate

        // Use regex to find numbers and add commas to them without breaking operators/functions
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
        int oldLength = s.length();

        etEquation.setText(formatted.toString());

        // Adjust cursor position so it doesn't jump to the start
        int newLength = etEquation.getText().length();
        int newCursor = cursorPosition + (newLength - oldLength);
        etEquation.setSelection(Math.max(0, Math.min(newCursor, newLength)));

        isFormatting = false;
    }

    private String formatNumberWithCommas(String number) {
        try {
            if (number.contains(".")) {
                String[] split = number.split("\\.");
                double integerPart = Double.parseDouble(split[0]);
                String formattedInt = NumberFormat.getNumberInstance(Locale.US).format(integerPart);
                return formattedInt + "." + (split.length > 1 ? split[1] : "");
            } else {
                return NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(number));
            }
        } catch (Exception e) {
            return number;
        }
    }

    private void doRealTimeEval() {
        // Clean formula: MUST remove commas before passing to exp4j
        String input = etEquation.getText().toString().replace(",", "");

        if (input.isEmpty()) {
            tvDisplay.setText("");
            return;
        }

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
                @Override
                public double apply(double... args) {
                    int arg = (int) args[0];
                    if (arg < 0) throw new IllegalArgumentException();
                    double result = 1;
                    for (int i = 1; i <= arg; i++) result *= i;
                    return result;
                }
            };

            Expression e = new ExpressionBuilder(formula).operator(factorial).build();
            double res = e.evaluate();
            tvDisplay.setText(formatResult(res));
        } catch (Exception e) {
            // Silently ignore typing errors
        }
    }

    private void calculate() {
        if (etEquation.getText().toString().isEmpty()) return;
        try {
            doRealTimeEval();
        } catch (Exception e) {
            tvDisplay.setText("Error");
        }
    }

    private String formatResult(double res) {
        if (Double.isInfinite(res) || Double.isNaN(res)) return "Error";

        // Scientific notation for very large numbers
        if (Math.abs(res) >= 1_000_000_000L || (Math.abs(res) < 0.0000001 && res != 0)) {
            return new DecimalFormat("0.######E0").format(res);
        }

        // For standard numbers, use grouping (commas)
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern("#,###.#######");
        return df.format(res);
    }
}