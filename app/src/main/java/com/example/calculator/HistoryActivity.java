package com.example.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private List<String[]> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ImageButton btnBack = findViewById(R.id.btnBackHistory);
        ImageButton btnClear = findViewById(R.id.btnClearHistory);
        rvHistory = findViewById(R.id.rvHistory);

        btnBack.setOnClickListener(v -> finish());

        loadHistory();

        btnClear.setOnClickListener(v -> {
            if (historyList != null && !historyList.isEmpty()) {
                showDeleteConfirmation();
            }
        });
    }

    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Clear History")
                .setMessage("Are you sure you want to delete all calculation history?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    SharedPreferences sp = getSharedPreferences("calc_prefs", MODE_PRIVATE);
                    sp.edit().remove("history").apply();
                    historyList.clear();
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadHistory() {
        SharedPreferences sp = getSharedPreferences("calc_prefs", MODE_PRIVATE);
        String json = sp.getString("history", null);

        if (json == null) {
            historyList = new ArrayList<>();
        } else {
            historyList = new Gson().fromJson(json, new TypeToken<List<String[]>>(){}.getType());
        }

        adapter = new HistoryAdapter(historyList);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }
}