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
    private boolean shouldClearHeader = false; // Flag to handle post-calculation typing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tvDisplay);
        etEquation = findViewById(R.id.etEquation);
        etEquation.setShowSoftInputOnFocus(false); // Keeps keyboard hidden

        SwitchMaterial modeToggle = findViewById(R.id.modeToggle);
        modeToggle.setOnCheckedChangeListener((v, isChecked) -> isRadianMode = isChecked);

        // Standard Numbers and Operators
        int[] ids = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnOpen, R.id.btnClose, R.id.btnPlus, R.id.btnMinus, R.id.btnPower};
        for (int id : ids) {
            findViewById(id).setOnClickListener(v -> insertText(((Button)v).getText().toString()));
        }

        // Scientific Functions
        setupFunc(R.id.btnSin, "sin("); setupFunc(R.id.btnCos, "cos(");
        setupFunc(R.id.btnTan, "tan("); setupFunc(R.id.btnLog, "log10(");
        setupFunc(R.id.btnLn, "log("); setupFunc(R.id.btnRoot, "sqrt(");

        findViewById(R.id.btnMultiply).setOnClickListener(v -> insertText("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> insertText("/"));
        findViewById(R.id.btnPi).setOnClickListener(v -> insertText("π"));
        findViewById(R.id.btnE).setOnClickListener(v -> insertText("e"));

        // Clear All
        findViewById(R.id.btnAC).setOnClickListener(v -> {
            etEquation.setText("");
            tvDisplay.setText("0");
            shouldClearHeader = false;
        });

        // Backspace logic at cursor position
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
        // If a calculation was just finished, clear before starting new input
        if (shouldClearHeader) {
            etEquation.setText("");
            shouldClearHeader = false;
        }

        String oldStr = etEquation.getText().toString();
        int cursorPosition = etEquation.getSelectionStart();

        String leftStr = oldStr.substring(0, cursorPosition);
        String rightStr = oldStr.substring(cursorPosition);

        String newStr = leftStr + strToAdd + rightStr;

        // Visual replacements
        etEquation.setText(newStr.replace("*", "×").replace("/", "÷"));
        etEquation.setSelection(cursorPosition + strToAdd.length());
    }

    private void calculate() {
        try {
            String formula = etEquation.getText().toString()
                    .replace("×", "*")
                    .replace("÷", "/")
                    .replace("π", "pi")
                    .replace("e", "e");

            formula = formula.replace("log(", "log10(")
                    .replace("√(", "sqrt(");

            if (!isRadianMode) {
                formula = formula.replace("sin(", "sin(pi/180*")
                        .replace("cos(", "cos(pi/180*")
                        .replace("tan(", "tan(pi/180*");
            }

            Expression e = new ExpressionBuilder(formula).build();
            double res = e.evaluate();

            String resultStr = (res % 1 == 0) ? String.valueOf((long)res) : String.valueOf(res);
            tvDisplay.setText(resultStr);

            // Flag to clear the top bar on the next button press
            shouldClearHeader = true;

        } catch (Exception e) {
            tvDisplay.setText("Error");
        }
    }
}