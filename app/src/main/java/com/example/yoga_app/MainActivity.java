package com.example.yoga_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    public static DatabaseHelper helper;  // Shared so adapter can reference
    private YogaCourseRecyclerAdapter adapter;
    private EditText edSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);

        // Create / open the local database
        helper = new DatabaseHelper(this);

        // "Add Course" button
        ImageButton btnAddCourse = findViewById(R.id.btnAddCourse);
        btnAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateYogaCourse.class);
            startActivity(intent);
        });

        // "View Schedule" button
        ImageButton btnViewSchedule = findViewById(R.id.btnViewSchedule);
        btnViewSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        // "Upload All Courses" button
        Button btnUploadAll = findViewById(R.id.btnUploadAll);
        btnUploadAll.setOnClickListener(v -> {
            // Trigger the Firestore upload task
            new UploadAllCoursesTask(MainActivity.this).execute();
        });

        edSearch = findViewById(R.id.edSearch);
        setupRecyclerView();
        setupSearch();
    }

    private void setupRecyclerView() {
        RecyclerView rvCourses = findViewById(R.id.rvCourses);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));

        // Initial cursor with all courses
        Cursor cursor = helper.readAllYogaCourse();
        adapter = new YogaCourseRecyclerAdapter(this, cursor);
        rvCourses.setAdapter(adapter);
    }

    private void setupSearch() {
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    Cursor cursor = helper.searchCoursesByType(query);
                    adapter.swapCursor(cursor);
                } else {
                    adapter.swapCursor(helper.readAllYogaCourse());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Refresh the list each time we return
        if (adapter != null) {
            adapter.swapCursor(helper.readAllYogaCourse());
        }
    }
}
