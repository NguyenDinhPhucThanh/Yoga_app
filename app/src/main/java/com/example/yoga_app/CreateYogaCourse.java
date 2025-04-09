package com.example.yoga_app;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateYogaCourse extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Spinner spDayOfWeek, spTime, spType, spDuration; // Added spDuration
    private EditText edPrice, edmDes, edCapacity; // Remove edDuration since duration now comes from Spinner
    private Button btnAdd, btnClear;
    private ImageButton btnBack; // Back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_yoga_course);

        // Initialize Database Helper
        dbHelper = new DatabaseHelper(this);

        // Initialize UI elements
        spDayOfWeek = findViewById(R.id.spDayOfWeek);
        spTime = findViewById(R.id.spTime);
        spType = findViewById(R.id.spType);
        spDuration = findViewById(R.id.spDuration);  // New spinner for duration
        edPrice = findViewById(R.id.edPrice);
        edmDes = findViewById(R.id.edmDes);
        edCapacity = findViewById(R.id.edCapacity);
        btnAdd = findViewById(R.id.btnAdd);
        btnClear = findViewById(R.id.btnClear);
        btnBack = findViewById(R.id.btnBack); // Initialize back button

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

        // Populate duration spinner with duration options
        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(this,
                R.array.duration_options, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDuration.setAdapter(durationAdapter);

        // Set Click Listeners
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCreateYogaCourse();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        // Back Button Functionality
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Closes this activity and returns to the previous screen
            }
        });
    }

    public void onClickCreateYogaCourse() {
        // Retrieve spinner selections
        String dayOfWeek = (spDayOfWeek.getSelectedItem() != null) ? spDayOfWeek.getSelectedItem().toString() : "";
        String time = (spTime.getSelectedItem() != null) ? spTime.getSelectedItem().toString() : "";
        String type = (spType.getSelectedItem() != null) ? spType.getSelectedItem().toString() : "";
        String durationStr = (spDuration.getSelectedItem() != null) ? spDuration.getSelectedItem().toString() : "";
        String description = edmDes.getText().toString().trim();

        // Validate required text fields: price, capacity, and duration spinner selection
        String priceText = edPrice.getText().toString().trim();
        String capacityText = edCapacity.getText().toString().trim();

        if (dayOfWeek.isEmpty() || time.isEmpty() || type.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(this, "Please select all required options", Toast.LENGTH_SHORT).show();
            return;
        }

        if (priceText.isEmpty()) {
            Toast.makeText(this, "Please enter a price", Toast.LENGTH_SHORT).show();
            return;
        }
        if (capacityText.isEmpty()) {
            Toast.makeText(this, "Please enter the capacity", Toast.LENGTH_SHORT).show();
            return;
        }

        float price;
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

        // Parse the duration from the spinner selection (assuming the spinner items are numeric strings)
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

        // Insert into database (Note: Adjust your DatabaseHelper.createNewYogaCourse method signature accordingly)
        long result = dbHelper.createNewYogaCourse(dayOfWeek, time, capacity, duration, price, type, description);

        if (result > 0) {
            Toast.makeText(this, "Yoga class created successfully!", Toast.LENGTH_LONG).show();
            clearFields();
        } else {
            Toast.makeText(this, "Failed to create yoga class", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        edPrice.setText("");
        edmDes.setText("");
        edCapacity.setText("");
        // Reset the spinners to their first item if desired:
        spDayOfWeek.setSelection(0);
        spTime.setSelection(0);
        spType.setSelection(0);
        spDuration.setSelection(0);
    }
}
