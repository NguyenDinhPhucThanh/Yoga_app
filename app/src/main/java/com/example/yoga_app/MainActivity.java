package com.example.yoga_app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    public static DatabaseHelper helper;
    private YogaCourseRecyclerAdapter adapter;
    private EditText edSearch;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        // Open / create local DB
        helper = new DatabaseHelper(this);

        // Location client & permission
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();

        // Top buttons:
        findViewById(R.id.btnAddCourse).setOnClickListener(v ->
                startActivity(new Intent(this, CreateYogaCourse.class))
        );
        findViewById(R.id.btnViewSchedule).setOnClickListener(v ->
                startActivity(new Intent(this, ScheduleActivity.class))
        );
        findViewById(R.id.btnUploadAll).setOnClickListener(v ->
                new UploadAllCoursesTask(this).execute()
        );
        findViewById(R.id.btnLocation).setOnClickListener(v ->
                getCurrentLocation()
        );
        // **Reset DB** button:
        Button btnReset = findViewById(R.id.btnResetDatabase);
        btnReset.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Reset Database")
                        .setMessage("Are you sure you want to delete ALL data?")
                        .setPositiveButton("Yes", (dlg, which) -> {
                            helper.resetDatabase();
                            adapter.swapCursor(helper.readAllYogaCourse());
                            Toast.makeText(this, "All data cleared", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        // Search & list
        edSearch = findViewById(R.id.edSearch);
        setupRecyclerView();
        setupSearch();
    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rvCourses);
        rv.setLayoutManager(new LinearLayoutManager(this));
        Cursor c = helper.readAllYogaCourse();
        adapter = new YogaCourseRecyclerAdapter(this, c);
        rv.setAdapter(adapter);
    }

    private void setupSearch() {
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                String q = s.toString().trim();
                Cursor cur = q.isEmpty()
                        ? helper.readAllYogaCourse()
                        : helper.searchCoursesByType(q);
                adapter.swapCursor(cur);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        String msg = "Lat: " + location.getLatitude() +
                                ", Lon: " + location.getLongitude();
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.swapCursor(helper.readAllYogaCourse());
        }
    }

    @Override
    public void onRequestPermissionsResult(int req, @NonNull String[] perms, @NonNull int[] res) {
        super.onRequestPermissionsResult(req, perms, res);
        if (req == LOCATION_PERMISSION_REQUEST_CODE) {
            if (res.length > 0 && res[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
