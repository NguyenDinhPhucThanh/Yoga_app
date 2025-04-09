package com.example.yoga_app;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateClassInstanceActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText edDate, edTeacher, edComments;
    private Button btnCreate, btnSelectDate;
    private ImageButton btnBack;  // NEW: back arrow button
    private int courseId;
    private String courseDayOfWeek; // e.g. "Monday"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class_instance);

        dbHelper = new DatabaseHelper(this);

        // Find views
        edDate = findViewById(R.id.edDate);
        edTeacher = findViewById(R.id.edTeacher);
        edComments = findViewById(R.id.edComments);
        btnCreate = findViewById(R.id.btnCreateInstance);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnBack = findViewById(R.id.btnBack); // Initialize the back arrow button

        // Handle back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Closes this activity and returns to the previous screen
            }
        });

        // Get courseId from intent
        courseId = getIntent().getIntExtra("course_id", -1);

        // Fetch the dayOfWeek for this course from DB
        if (courseId != -1) {
            Cursor cursor = dbHelper.getYogaCourseById(courseId);
            if (cursor != null && cursor.moveToFirst()) {
                courseDayOfWeek = cursor.getString(cursor.getColumnIndex("dayofweek"));
                cursor.close();
            }
        }

        // Button click for creating instance
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInstance();
            }
        });

        // Button click for selecting date
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        // Get current date as default
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CreateClassInstanceActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        // Format date as yyyy-MM-dd (selectedMonth is 0-based)
                        String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        edDate.setText(formattedDate);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void createInstance() {
        String dateStr = edDate.getText().toString().trim();
        String teacher = edTeacher.getText().toString().trim();
        String comments = edComments.getText().toString().trim();

        // Required fields
        if (dateStr.isEmpty()) {
            Toast.makeText(this, "Please enter a date (yyyy-MM-dd)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (teacher.isEmpty()) {
            Toast.makeText(this, "Please enter a teacher name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate dayOfWeek matches course
        if (!matchesDayOfWeek(dateStr, courseDayOfWeek)) {
            Toast.makeText(this,
                    "The date must match the day of the week you selected. It is a " + courseDayOfWeek,
                    Toast.LENGTH_LONG).show();
            return;
        }

        long result = dbHelper.createClassInstance(courseId, dateStr, teacher, comments);
        if (result > 0) {
            Toast.makeText(this, "Class instance created!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to create instance", Toast.LENGTH_SHORT).show();
        }
    }

    // Check if dateStr’s day of week matches the course’s dayOfWeek
    private boolean matchesDayOfWeek(String dateStr, String requiredDay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            String actualDayName = getDayName(dayOfWeek); // e.g. "Monday"
            return actualDayName.equalsIgnoreCase(requiredDay);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY: return "Monday";
            case Calendar.TUESDAY: return "Tuesday";
            case Calendar.WEDNESDAY: return "Wednesday";
            case Calendar.THURSDAY: return "Thursday";
            case Calendar.FRIDAY: return "Friday";
            case Calendar.SATURDAY: return "Saturday";
            case Calendar.SUNDAY: return "Sunday";
        }
        return "";
    }
}
