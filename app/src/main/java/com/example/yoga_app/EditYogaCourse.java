package com.example.yoga_app;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditYogaCourse extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Spinner spDayOfWeek, spTime, spType, spDuration;
    private EditText edPrice, edmDes, edCapacity;
    private Button btnUpdate, btnCancel;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_yoga_course);

        dbHelper = new DatabaseHelper(this);

        // Initialize UI elements
        spDayOfWeek = findViewById(R.id.spDayOfWeek);
        spTime = findViewById(R.id.spTime);
        spType = findViewById(R.id.spType);
        spDuration = findViewById(R.id.spDuration);  // Spinner for duration
        edPrice = findViewById(R.id.edPrice);
        edmDes = findViewById(R.id.edmDes);
        edCapacity = findViewById(R.id.edCapacity);  // EditText for capacity
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        // Populate Spinners with data
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(this,
                R.array.days_of_week, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDayOfWeek.setAdapter(dayAdapter);

        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this,
                R.array.class_times, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTime.setAdapter(timeAdapter);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.yoga_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(this,
                R.array.duration_options, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDuration.setAdapter(durationAdapter);

        // Get the course ID from the Intent extras
        courseId = getIntent().getIntExtra("course_id", -1);
        if (courseId != -1) {
            populateFields(courseId);
        } else {
            Toast.makeText(this, "Invalid course ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateYogaCourse();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close activity without saving
            }
        });
    }

    private void populateFields(int id) {
        Cursor cursor = dbHelper.getYogaCourseById(id);
        if (cursor != null && cursor.moveToFirst()) {
            String day = cursor.getString(cursor.getColumnIndex("dayofweek"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            float price = cursor.getFloat(cursor.getColumnIndex("price"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            int capacity = cursor.getInt(cursor.getColumnIndex("capacity"));
            int duration = cursor.getInt(cursor.getColumnIndex("duration"));

            setSpinnerSelection(spDayOfWeek, day);
            setSpinnerSelection(spTime, time);
            setSpinnerSelection(spType, type);
            setSpinnerSelection(spDuration, String.valueOf(duration));

            edPrice.setText(String.valueOf(price));
            edmDes.setText(description);
            edCapacity.setText(String.valueOf(capacity));
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * Safely set a spinner's selection by first checking that the value exists in the adapter.
     */
    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        int pos = adapter.getPosition(value);
        if (pos >= 0 && pos < adapter.getCount()) {
            spinner.setSelection(pos);
        }
    }

    private void updateYogaCourse() {
        String dayOfWeek = (spDayOfWeek.getSelectedItem() != null)
                ? spDayOfWeek.getSelectedItem().toString()
                : "";
        String time = (spTime.getSelectedItem() != null)
                ? spTime.getSelectedItem().toString()
                : "";
        String type = (spType.getSelectedItem() != null)
                ? spType.getSelectedItem().toString()
                : "";
        String durationStr = (spDuration.getSelectedItem() != null)
                ? spDuration.getSelectedItem().toString()
                : "";
        String description = edmDes.getText().toString().trim();
        String capacityText = edCapacity.getText().toString().trim();
        float price;

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }

        String priceText = edPrice.getText().toString().trim();
        if (priceText.isEmpty()) {
            Toast.makeText(this, "Please enter a price", Toast.LENGTH_SHORT).show();
            return;
        }
        if (capacityText.isEmpty()) {
            Toast.makeText(this, "Please enter the capacity", Toast.LENGTH_SHORT).show();
            return;
        }
        if (durationStr.isEmpty()) {
            Toast.makeText(this, "Please select a duration", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacity, duration;
        try {
            price = Float.parseFloat(priceText);
            if (price <= 0) {
                Toast.makeText(this, "Price must be greater than zero", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            capacity = Integer.parseInt(capacityText);
            if (capacity <= 0) {
                Toast.makeText(this, "Capacity must be greater than zero", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid capacity format", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                Toast.makeText(this, "Duration must be greater than zero", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid duration format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the database record (updateYogaCourse now requires capacity and duration parameters)
        boolean updated = dbHelper.updateYogaCourse(courseId, dayOfWeek, time, capacity, duration, price, type, description);
        if (updated) {
            Toast.makeText(this, "Yoga course updated successfully!", Toast.LENGTH_LONG).show();
            finish(); // Close the activity
        } else {
            Toast.makeText(this, "Failed to update yoga course", Toast.LENGTH_SHORT).show();
        }
    }
}
