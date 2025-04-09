package com.example.yoga_app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class YogaCourseRecyclerAdapter extends RecyclerView.Adapter<YogaCourseRecyclerAdapter.CourseViewHolder> {

    private Context context;
    private Cursor cursor;

    public YogaCourseRecyclerAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.yoga_course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        if (cursor == null || !cursor.moveToPosition(position)) return;

        // Retrieve column indexes
        int idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_ID);
        int dayIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DAYOFWEEK);
        int typeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE);
        int timeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME);
        int descIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION);
        int priceIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE);
        int capacityIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAPACITY);
        int durationIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DURATION);

        // Get course ID
        final int courseId = cursor.getInt(idIndex);

        // Bind data to views
        holder.tvDayOfWeek.setText("On: " + cursor.getString(dayIndex));
        holder.tvType.setText(cursor.getString(typeIndex));
        holder.tvTime.setText("Time: " + cursor.getString(timeIndex));
        holder.tvDescription.setText("Details: " + cursor.getString(descIndex));
        holder.tvPrice.setText("$" + cursor.getDouble(priceIndex));
        holder.tvCapacity.setText("Capacity: " + cursor.getInt(capacityIndex));
        holder.tvDuration.setText("Duration: " + cursor.getInt(durationIndex) + " min");

        // Handle item click (view instances)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, InstanceListActivity.class);
            intent.putExtra("COURSE_ID", courseId);
            context.startActivity(intent);
        });

        // Delete button functionality
        holder.btnDelete.setOnClickListener(v -> {
            boolean deleted = MainActivity.helper.deleteYogaCourse(courseId);
            if (deleted) {
                Toast.makeText(context, "Course deleted", Toast.LENGTH_SHORT).show();
                swapCursor(MainActivity.helper.readAllYogaCourse());
            } else {
                Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Edit button functionality
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditYogaCourse.class);
            intent.putExtra("course_id", courseId);
            context.startActivity(intent);
        });

        // Add schedule instance
        holder.btnAddInstance.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateClassInstanceActivity.class);
            intent.putExtra("course_id", courseId);
            context.startActivity(intent);
        });

        // Upload course data
        holder.btnUploadCourse.setOnClickListener(v -> {


            // Retrieve single course from DB
            Cursor c = MainActivity.helper.getYogaCourseById(courseId);
            if (c != null && c.moveToFirst()) {
                Course singleCourse = new Course();
                singleCourse.setId(courseId);
                singleCourse.setDayOfWeek(c.getString(dayIndex));
                singleCourse.setTime(c.getString(timeIndex));
                singleCourse.setPrice(c.getDouble(priceIndex));
                singleCourse.setType(c.getString(typeIndex));
                singleCourse.setDescription(c.getString(descIndex));
                singleCourse.setCapacity(c.getInt(capacityIndex));
                singleCourse.setDuration(c.getInt(durationIndex));
                c.close();

                // Start the upload task

            } else {
                Toast.makeText(context, "Cannot find course in DB", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek, tvType, tvTime, tvDescription, tvPrice, tvCapacity, tvDuration;
        Button btnEdit, btnDelete, btnAddInstance, btnUploadCourse;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvType = itemView.findViewById(R.id.tvType);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvDuration = itemView.findViewById(R.id.tvDuration);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnAddInstance = itemView.findViewById(R.id.btnAddInstance);
            btnUploadCourse = itemView.findViewById(R.id.btnUploadCourse);
        }
    }
}
