package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay, tvEquation;
    private StringBuilder expression = new StringBuilder();
    private boolean isRadianMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tvDisplay);
        tvEquation = findViewById(R.id.tvEquation);
        SwitchMaterial modeToggle = findViewById(R.id.modeToggle);

        modeToggle.setOnCheckedChangeListener((v, isChecked) -> isRadianMode = isChecked);

        // Buttons for appending text
        int[] ids = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnOpen, R.id.btnClose, R.id.btnPlus, R.id.btnMinus, R.id.btnPower};
        for (int id : ids) {
            findViewById(id).setOnClickListener(v -> append(((Button)v).getText().toString()));
        }

        // Functions with brackets
        setupFunc(R.id.btnSin, "sin("); setupFunc(R.id.btnCos, "cos(");
        setupFunc(R.id.btnTan, "tan("); setupFunc(R.id.btnLog, "log10(");
        setupFunc(R.id.btnLn, "log("); setupFunc(R.id.btnRoot, "sqrt(");

        findViewById(R.id.btnMultiply).setOnClickListener(v -> append("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> append("/"));
        findViewById(R.id.btnPi).setOnClickListener(v -> append("π"));
        findViewById(R.id.btnE).setOnClickListener(v -> append("e"));

        findViewById(R.id.btnAC).setOnClickListener(v -> {
            expression.setLength(0);
            tvDisplay.setText("0");
            tvEquation.setText("");
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (expression.length() > 0) {
                expression.deleteCharAt(expression.length() - 1);
                updateUI();
            }
        });

        findViewById(R.id.btnEquals).setOnClickListener(v -> calculate());
    }

    private void setupFunc(int id, String text) {
        findViewById(id).setOnClickListener(v -> append(text));
    }

    private void append(String str) {
        expression.append(str);
        updateUI();
    }

    private void updateUI() {
        tvEquation.setText(expression.toString().replace("*", "×").replace("/", "÷"));
    }

    private void calculate() {
        try {
            String formula = expression.toString()
                    .replace("π", "pi")
                    .replace("e", "e");

            // Handle Trig Degree/Radian conversion like your JS processTrigFunctions
            if (!isRadianMode) {
                formula = formula.replace("sin(", "sin(pi/180*")
                        .replace("cos(", "cos(pi/180*")
                        .replace("tan(", "tan(pi/180*");
            }

            Expression e = new ExpressionBuilder(formula).build();
            double res = e.evaluate();

            String resultStr = (res % 1 == 0) ? String.valueOf((long)res) : String.valueOf(res);
            tvDisplay.setText(resultStr);
            expression.setLength(0);
            expression.append(resultStr); // Chain calculations
        } catch (Exception e) {
            tvDisplay.setText("Error");
        }
    }
}