package com.example.yoga_app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class EditInstanceActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText edDate, edTeacher, edComments;
    private Button btnSelectDate, btnSave;
    private DatabaseHelper dbHelper;
    private int instanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_instance);

        // 1) Bind Views
        btnBack = findViewById(R.id.btnBack);
        edDate = findViewById(R.id.edDate);
        edTeacher = findViewById(R.id.edTeacher);
        edComments = findViewById(R.id.edComments);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSave = findViewById(R.id.btnSave);

        // 2) Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // 3) Retrieve data passed from the calling Activity/Adapter
        //    The adapter might do something like:
        //    intent.putExtra("INSTANCE_ID", instanceId);
        //    intent.putExtra("DATE", date);
        //    intent.putExtra("TEACHER", teacher);
        //    intent.putExtra("COMMENTS", comments);
        instanceId = getIntent().getIntExtra("INSTANCE_ID", -1);
        String date = getIntent().getStringExtra("DATE");
        String teacher = getIntent().getStringExtra("TEACHER");
        String comments = getIntent().getStringExtra("COMMENTS");

        // 4) Populate the fields if the values are not null
        if (date != null) {
            edDate.setText(date);
        }
        if (teacher != null) {
            edTeacher.setText(teacher);
        }
        if (comments != null) {
            edComments.setText(comments);
        }

        // 5) Handle Back Arrow
        btnBack.setOnClickListener(v -> finish());

        // 6) Handle "Select Date" Button
        btnSelectDate.setOnClickListener(v -> {
            // Show a DatePickerDialog to let user choose a date
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditInstanceActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Convert month from 0-based to 1-based
                        int displayMonth = selectedMonth + 1;
                        // Format the date as YYYY-MM-DD (basic version)
                        String formattedDate = selectedYear + "-" + displayMonth + "-" + selectedDay;
                        edDate.setText(formattedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // 7) Handle "Save" Button
        btnSave.setOnClickListener(v -> {
            // Gather updated values
            String newDate = edDate.getText().toString().trim();
            String newTeacher = edTeacher.getText().toString().trim();
            String newComments = edComments.getText().toString().trim();

            // Update the instance in the database
            boolean success = dbHelper.updateClassInstance(instanceId, newDate, newTeacher, newComments);
            if (success) {
                Toast.makeText(EditInstanceActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                // Optionally set a result to notify the caller to refresh
                setResult(RESULT_OK);
                finish(); // Go back
            } else {
                Toast.makeText(EditInstanceActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
