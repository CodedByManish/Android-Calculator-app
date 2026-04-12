package com.example.calculator;

import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
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

        SwitchMaterial modeToggle = findViewById(R.id.modeToggle);
        modeToggle.setOnCheckedChangeListener((v, isChecked) -> isRadianMode = isChecked);

        int[] ids = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnOpen, R.id.btnClose, R.id.btnPlus, R.id.btnMinus, R.id.btnPower};
        for (int id : ids) {
            findViewById(id).setOnClickListener(v -> insertText(((Button)v).getText().toString()));
        }

        setupFunc(R.id.btnSin, "sin(");
        setupFunc(R.id.btnCos, "cos(");
        setupFunc(R.id.btnTan, "tan(");
        setupFunc(R.id.btnLog, "log10(");
        setupFunc(R.id.btnLn, "ln(");
        setupFunc(R.id.btnRoot, "sqrt(");
        setupFunc(R.id.btnFact, "fact("); // Changed to function style for exp4j

        findViewById(R.id.btnMultiply).setOnClickListener(v -> insertText("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> insertText("/"));
        findViewById(R.id.btnPi).setOnClickListener(v -> insertText("π"));
        findViewById(R.id.btnE).setOnClickListener(v -> insertText("e"));

        findViewById(R.id.btnAC).setOnClickListener(v -> {
            etEquation.setText("");
            tvDisplay.setText("0");
            shouldClearHeader = false;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            int cursorPosition = etEquation.getSelectionStart();
            if (cursorPosition > 0) {
                Editable text = etEquation.getText();
                text.delete(cursorPosition - 1, cursorPosition);
            }
        });

        findViewById(R.id.btnEquals).setOnClickListener(v -> calculate());
    }

    private void setupFunc(int id, String text) {
        findViewById(id).setOnClickListener(v -> insertText(text));
    }

    private void insertText(String strToAdd) {
        if (shouldClearHeader) {
            etEquation.setText("");
            shouldClearHeader = false;
        }

        int cursorPosition = etEquation.getSelectionStart();
        Editable editable = etEquation.getText();

        String visualStr = strToAdd
                .replace("*", "×")
                .replace("/", "÷")
                .replace("log10(", "log(")
                .replace("sqrt(", "√(")
                .replace("fact(", "!("); // Show ! visually

        editable.insert(cursorPosition, visualStr);
    }

    private void calculate() {
        try {
            String formula = etEquation.getText().toString()
                    .replace("×", "*")
                    .replace("÷", "/")
                    .replace("log(", "log10(")
                    .replace("ln(", "log")
                    .replace("√(", "sqrt(")
                    .replace("!(", "fact(") // Logic back to function
                    .replace("π", "pi")
                    .replace("e", "e");

            if (!isRadianMode) {
                formula = formula.replace("sin(", "sin(pi/180*")
                        .replace("cos(", "cos(pi/180*")
                        .replace("tan(", "tan(pi/180*");
            }

            // Custom Factorial Function for exp4j
            Function fact = new Function("fact", 1) {
                @Override
                public double apply(double... args) {
                    double n = args[0];
                    if (n < 0) return Double.NaN;
                    double result = 1;
                    for (int i = 1; i <= n; i++) result *= i;
                    return result;
                }
            };

            Expression e = new ExpressionBuilder(formula)
                    .function(fact)
                    .build();

            double res = e.evaluate();
            tvDisplay.setText(formatResult(res));
            shouldClearHeader = true;

        } catch (Exception e) {
            tvDisplay.setText("Error");
        }
    }

    // New method to handle scientific notation and precision
    private String formatResult(double res) {
        if (Double.isInfinite(res) || Double.isNaN(res)) return "Error";

        // If number is larger than 10 digits or very tiny, use scientific notation
        if (Math.abs(res) >= 1_000_000_000L || (Math.abs(res) < 0.0000001 && res != 0)) {
            return new DecimalFormat("0.######E0").format(res);
        }

        // Otherwise, format as standard decimal, removing trailing zeros
        DecimalFormat df = new DecimalFormat("0.##########");
        return df.format(res);
    }
}