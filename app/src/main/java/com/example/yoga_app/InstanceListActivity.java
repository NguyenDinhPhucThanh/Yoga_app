package com.example.yoga_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InstanceListActivity extends AppCompatActivity {

    private AllInstancesAdapter adapter;
    private DatabaseHelper dbHelper;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instance_list);

        // 1) Back button setup
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Option A: Simply finish this Activity to go back
            // finish();

            // Option B: Return to MainActivity
            Intent intent = new Intent(InstanceListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // 2) Get the course_id from the Intent
        courseId = getIntent().getIntExtra("COURSE_ID", -1);

        // 3) Initialize DB and RecyclerView
        dbHelper = new DatabaseHelper(this);
        RecyclerView rvInstances = findViewById(R.id.rvInstances);
        rvInstances.setLayoutManager(new LinearLayoutManager(this));

        // 4) Query the DB for only this course's instances
        Cursor cursor = dbHelper.readInstancesForCourse(courseId);

        // 5) Check if no schedules exist (empty cursor)
        if (cursor != null && cursor.getCount() == 0) {
            showNoScheduleDialog();
        }

        // 6) Set up adapter
        adapter = new AllInstancesAdapter(this, cursor);
        rvInstances.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Refresh data in case new instances were added or changed
        if (adapter != null) {
            Cursor newCursor = dbHelper.readInstancesForCourse(courseId);

            // Check again if empty
            if (newCursor != null && newCursor.getCount() == 0) {
                showNoScheduleDialog();
            }

            adapter.swapCursor(newCursor);
        }
    }

    /**
     * Displays an AlertDialog informing the user there are no schedules for this lesson,
     * and returns to MainActivity when the user taps OK.
     */
    private void showNoScheduleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Schedule")
                .setMessage("There is no schedule for this lesson yet.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    // Return to MainActivity
                    Intent intent = new Intent(InstanceListActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // End this activity so it's not on the back stack
                })
                .show();
    }
}
