package com.example.yoga_app;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CourseDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvDetailName, tvDetailDay, tvDetailTime, tvDetailDescription, tvDetailPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        dbHelper = new DatabaseHelper(this);

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailDay = findViewById(R.id.tvDetailDay);
        tvDetailTime = findViewById(R.id.tvDetailTime);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);

        int courseId = getIntent().getIntExtra("course_id", -1);
        if (courseId != -1) {
            loadCourse(courseId);
        }
    }

    private void loadCourse(int courseId) {
        Cursor cursor = dbHelper.getYogaCourseById(courseId);
        if (cursor != null && cursor.moveToFirst()) {
            String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE));
            String day = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DAYOFWEEK));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE));

            tvDetailName.setText("Name: " + type);
            tvDetailDay.setText("Day: " + day);
            tvDetailTime.setText("Time: " + time);
            tvDetailDescription.setText("Description: " + description);
            tvDetailPrice.setText("Price: $" + price);

            cursor.close();
        }
    }
}
