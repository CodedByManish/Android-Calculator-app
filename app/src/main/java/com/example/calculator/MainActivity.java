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

        // Standard Numbers and Operators
        int[] ids = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnOpen, R.id.btnClose, R.id.btnPlus, R.id.btnMinus, R.id.btnPower};
        for (int id : ids) {
            findViewById(id).setOnClickListener(v -> insertText(((Button)v).getText().toString()));
        }

        // Scientific Functions Mapping
        setupFunc(R.id.btnSin, "sin(");
        setupFunc(R.id.btnCos, "cos(");
        setupFunc(R.id.btnTan, "tan(");
        setupFunc(R.id.btnLog, "log10("); // Becomes "log(" visually
        setupFunc(R.id.btnLn, "ln(");     // Becomes "ln(" visually, later mapped to exp4j "log("
        setupFunc(R.id.btnRoot, "sqrt(");  // Becomes "√(" visually

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

        // Visual mapping: What the user sees in the EditText
        String visualStr = strToAdd
                .replace("*", "×")
                .replace("/", "÷")
                .replace("log10(", "log(")
                .replace("sqrt(", "√(");
        // "ln(" remains "ln(" visually

        editable.insert(cursorPosition, visualStr);
    }

    private void calculate() {
        try {
            // Logic mapping: Convert visual symbols to exp4j internal functions
            String formula = etEquation.getText().toString()
                    .replace("×", "*")
                    .replace("÷", "/")
                    .replace("log(", "log10(") // log base 10
                    .replace("ln(", "log(")     // exp4j uses log() for natural log (ln)
                    .replace("√(", "sqrt(")
                    .replace("π", "pi")
                    .replace("e", "e");

            if (!isRadianMode) {
                formula = formula.replace("sin(", "sin(pi/180*")
                        .replace("cos(", "cos(pi/180*")
                        .replace("tan(", "tan(pi/180*");
            }

            Expression e = new ExpressionBuilder(formula).build();
            double res = e.evaluate();

            String resultStr = (res % 1 == 0) ? String.valueOf((long)res) : String.valueOf(res);
            tvDisplay.setText(resultStr);

            shouldClearHeader = true;

        } catch (Exception e) {
            tvDisplay.setText("Error");
        }
    }
}