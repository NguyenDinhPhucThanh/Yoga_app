package com.example.yoga_app;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class UploadAllCoursesTask extends AsyncTask<Void, Void, Void> {

    private Context context;

    public UploadAllCoursesTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Retrieve all courses from the local database
        Cursor cursor = MainActivity.helper.readAllYogaCourse();
        if (cursor == null) {
            Log.e("Firestore", "Cursor is null. No courses to upload.");
            return null;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch(); // Using batch for optimized Firestore writes

        try {
            while (cursor.moveToNext()) {
                // Creating a course data map
                Map<String, Object> courseData = new HashMap<>();
                courseData.put("id", cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_ID)));
                courseData.put("dayOfWeek", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DAYOFWEEK)));
                courseData.put("time", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME)));
                courseData.put("price", cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE)));
                courseData.put("type", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE)));
                courseData.put("description", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)));
                courseData.put("capacity", cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAPACITY))); // Added
                courseData.put("duration", cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DURATION))); // Added

                // Use course ID as the document ID to prevent duplicates
                String documentId = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_ID)));
                batch.set(db.collection("YogaCourses").document(documentId), courseData);
            }
        } catch (Exception e) {
            Log.e("Firestore", "Error reading from database: ", e);
        } finally {
            cursor.close(); // Ensure cursor is closed
        }

        // Commit the batch operation
        batch.commit()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "All courses successfully uploaded!"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error uploading courses", e));

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Toast.makeText(context, "All courses are being uploaded to Firestore", Toast.LENGTH_SHORT).show();
    }
}
