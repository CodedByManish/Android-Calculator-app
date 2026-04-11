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

        // Scientific Functions - Passing raw math strings to insertText
        setupFunc(R.id.btnSin, "sin(");
        setupFunc(R.id.btnCos, "cos(");
        setupFunc(R.id.btnTan, "tan(");
        setupFunc(R.id.btnLog, "log10("); // Will be mapped to visual "log("
        setupFunc(R.id.btnLn, "log(");     // Will stay "log(" visually (exp4j uses log for natural log)
        setupFunc(R.id.btnRoot, "sqrt(");  // Will be mapped to visual "√("

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

        // 1. Convert math-engine strings to visual symbols before inserting
        String visualStr = strToAdd
                .replace("*", "×")
                .replace("/", "÷")
                .replace("log10(", "log(")
                .replace("sqrt(", "√(");

        // 2. Insert into the EditText at current cursor position
        editable.insert(cursorPosition, visualStr);

        // Cursor moves automatically with Editable.insert
    }

    private void calculate() {
        try {
            // Convert visual symbols back to math engine logic strings
            String formula = etEquation.getText().toString()
                    .replace("×", "*")
                    .replace("÷", "/")
                    .replace("log(", "log10(") // Base 10
                    .replace("√(", "sqrt(")
                    .replace("π", "pi")
                    .replace("e", "e");

            // Handle Trig Degree/Radian conversion
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