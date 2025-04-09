package com.example.yoga_app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AllInstancesAdapter extends RecyclerView.Adapter<AllInstancesAdapter.InstanceViewHolder> {

    private Context context;
    private Cursor cursor;

    public AllInstancesAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public InstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the updated layout (all_instance_item.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.all_instance_item, parent, false);
        return new InstanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstanceViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;

        // Get column indexes from your cursor
        int instanceIdIndex = cursor.getColumnIndex("_id");
        int courseIdIndex = cursor.getColumnIndex("course_id");
        int dateIndex = cursor.getColumnIndex("date");
        int teacherIndex = cursor.getColumnIndex("teacher");
        int commentsIndex = cursor.getColumnIndex("comments");

        // Retrieve values from cursor
        int instanceId = cursor.getInt(instanceIdIndex);
        int courseId = cursor.getInt(courseIdIndex);
        String date = cursor.getString(dateIndex);
        String teacher = cursor.getString(teacherIndex);
        String comments = (commentsIndex != -1) ? cursor.getString(commentsIndex) : "";

        // For demonstration, hardcoded values for course details (subject, time, day)
        String subject = "Flow Yoga";
        String time = "Time: 5 AM";
        String day = "On: Mon";

        // Bind the values to the views
        holder.tvType.setText(subject);
        holder.tvTime.setText(time);
        holder.tvDayOfWeek.setText(day);
        holder.tvDate.setText("Date: " + date);
        holder.tvTeacher.setText("Teacher: " + teacher);
        holder.tvComments.setText("Comments: " + comments);

        // --------------------------
        // Handle Edit button click
        // --------------------------
        holder.btnEdit.setOnClickListener(v -> {
            // Launch an EditInstanceActivity passing current instance data
            Intent intent = new Intent(context, EditInstanceActivity.class);
            intent.putExtra("INSTANCE_ID", instanceId);
            intent.putExtra("COURSE_ID", courseId);
            intent.putExtra("DATE", date);
            intent.putExtra("TEACHER", teacher);
            intent.putExtra("COMMENTS", comments);
            context.startActivity(intent);
        });

        // --------------------------
        // Handle Delete button click
        // --------------------------
        holder.btnDelete.setOnClickListener(v -> {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            // Delete instance from the database
            dbHelper.deleteClassInstance(instanceId);
            // Refresh the cursor and update the list
            Cursor newCursor = dbHelper.readAllInstances();
            swapCursor(newCursor);
        });
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    // Swap the current cursor with a new one and update the list
    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    static class InstanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvTime, tvDayOfWeek, tvDate, tvTeacher, tvComments;
        Button btnEdit, btnDelete;

        public InstanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvComments = itemView.findViewById(R.id.tvComments);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
