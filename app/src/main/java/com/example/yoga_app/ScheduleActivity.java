package com.example.yoga_app;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScheduleActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvAllInstances;
    private AllInstancesAdapter adapter;
    private ImageButton btnBack;  // Back button as an ImageButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Initialize RecyclerView
        rvAllInstances = findViewById(R.id.rvAllInstances);
        rvAllInstances.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Database Helper
        dbHelper = new DatabaseHelper(this);

        // Query all instances from DB and set adapter
        Cursor cursor = dbHelper.readAllInstances();
        adapter = new AllInstancesAdapter(this, cursor);
        rvAllInstances.setAdapter(adapter);

        // Initialize Back button (ImageButton) and set click listener
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close this activity and return to MainActivity
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Refresh data in case new instances were added
        if (adapter != null) {
            adapter.swapCursor(dbHelper.readAllInstances());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.swapCursor(null); // Close cursor to prevent memory leaks
        }
    }
}
