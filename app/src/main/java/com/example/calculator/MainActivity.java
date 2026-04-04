package com.example.calculator; // Change this to your package name!

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private String currentInput = "";
    private String operator = "";
    private double firstValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tvDisplay);

        // Setup Number Buttons
        int[] numberIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot};
        for (int id : numberIds) {
            findViewById(id).setOnClickListener(v -> {
                Button b = (Button) v;
                currentInput += b.getText().toString();
                tvDisplay.setText(currentInput);
            });
        }

        // Setup Operator Buttons
        int[] operatorIds = {R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide};
        for (int id : operatorIds) {
            findViewById(id).setOnClickListener(v -> {
                if (!currentInput.isEmpty()) {
                    firstValue = Double.parseDouble(currentInput);
                    Button b = (Button) v;
                    operator = b.getText().toString();
                    currentInput = ""; // clear input for next number
                }
            });
        }

        // Setup Equals Button
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculate());

        // Setup Backspace
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (currentInput.length() > 0) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                tvDisplay.setText(currentInput.isEmpty() ? "0" : currentInput);
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
        }

        // Remove decimal if it's a whole number
        if (result % 1 == 0) {
            currentInput = String.valueOf((int) result);
        } else {
            currentInput = String.valueOf(result);
        }

        tvDisplay.setText(currentInput);
        operator = ""; // Reset operator
    }
}