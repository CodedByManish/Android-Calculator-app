package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay, tvEquation;
    private String currentInput = "";
    private String operator = "";
    private String fullEquation = "";
    private double firstValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Displays
        tvDisplay = findViewById(R.id.tvDisplay);
        tvEquation = findViewById(R.id.tvEquation);

        // --- Number Buttons (0-9 and Dot) ---
        int[] numberIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot};
        for (int id : numberIds) {
            findViewById(id).setOnClickListener(v -> {
                Button b = (Button) v;
                currentInput += b.getText().toString();
                fullEquation += b.getText().toString();
                tvDisplay.setText(currentInput);
                tvEquation.setText(fullEquation);
            });
        }

        // --- Standard Operator Buttons (+, -, x, /) ---
        int[] operatorIds = {R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide};
        for (int id : operatorIds) {
            findViewById(id).setOnClickListener(v -> {
                if (!currentInput.isEmpty()) {
                    firstValue = Double.parseDouble(currentInput);
                    operator = ((Button) v).getText().toString();
                    fullEquation += " " + operator + " ";
                    currentInput = "";
                    tvEquation.setText(fullEquation);
                }
            });
        }

        // --- Power Button (^) ---
        findViewById(R.id.btnPower).setOnClickListener(v -> {
            if (!currentInput.isEmpty()) {
                firstValue = Double.parseDouble(currentInput);
                operator = "^";
                fullEquation += " ^ ";
                currentInput = "";
                tvEquation.setText(fullEquation);
            }
        });

        // --- Root Button (√) ---
        findViewById(R.id.btnRoot).setOnClickListener(v -> {
            if (!currentInput.isEmpty()) {
                double val = Double.parseDouble(currentInput);
                double result = Math.sqrt(val);
                tvEquation.setText("√(" + currentInput + ")");
                currentInput = formatResult(result);
                tvDisplay.setText(currentInput);
            }
        });

        // --- Equals Button (=) ---
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculate());

        // --- AC Button (All Clear) ---
        findViewById(R.id.btnAC).setOnClickListener(v -> {
            currentInput = "";
            fullEquation = "";
            operator = "";
            firstValue = 0;
            tvDisplay.setText("0");
            tvEquation.setText("");
        });

        // --- Backspace Button ---
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (currentInput.length() > 0) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                // Also remove from equation string
                if (fullEquation.length() > 0) {
                    fullEquation = fullEquation.substring(0, fullEquation.length() - 1);
                }
                tvDisplay.setText(currentInput.isEmpty() ? "0" : currentInput);
                tvEquation.setText(fullEquation);
            }
        });
    }

    private void calculate() {
        if (currentInput.isEmpty() || operator.isEmpty()) return;

        double secondValue = Double.parseDouble(currentInput);
        double result = 0;

        switch (operator) {
            case "+": result = firstValue + secondValue; break;
            case "-": result = firstValue - secondValue; break;
            case "x": result = firstValue * secondValue; break;
            case "/":
                if (secondValue != 0) result = firstValue / secondValue;
                else { tvDisplay.setText("Error"); return; }
                break;
            case "^": result = Math.pow(firstValue, secondValue); break;
        }

        currentInput = formatResult(result);
        tvDisplay.setText(currentInput);

        // After calculation, the result becomes the new base for the next operation
        fullEquation = currentInput;
        operator = "";
    }

    // Helper to keep the display clean (removes .0 from whole numbers)
    private String formatResult(double result) {
        if (result % 1 == 0) return String.valueOf((int) result);
        return String.valueOf(result);
    }
}