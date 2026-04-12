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

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private EditText etEquation;
    private boolean isRadianMode = false;
    private boolean shouldClearHeader = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tvDisplay);
        etEquation = findViewById(R.id.etEquation);
        etEquation.setShowSoftInputOnFocus(false);

        // Ensure both start completely blank
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
                doRealTimeEval();
            }
            @Override
            public void afterTextChanged(Editable s) {}
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
            tvDisplay.setText(""); // Blank instead of 0
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

    private void doRealTimeEval() {
        String input = etEquation.getText().toString();

        // If empty, keep display blank
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
            // Keep previous result or stay blank while typing is invalid
        }
    }

    private void calculate() {
        if (etEquation.getText().toString().isEmpty()) return;
        try {
            doRealTimeEval();
            if (tvDisplay.getText().toString().isEmpty()) {
                tvDisplay.setText("Error");
            }
        } catch (Exception e) {
            tvDisplay.setText("Error");
        }
    }

    private String formatResult(double res) {
        if (Double.isInfinite(res) || Double.isNaN(res)) return "Error";
        if (Math.abs(res) >= 1_000_000_000L || (Math.abs(res) < 0.0000001 && res != 0)) {
            return new DecimalFormat("0.######E0").format(res);
        }
        return new DecimalFormat("0.#######").format(res);
    }
}