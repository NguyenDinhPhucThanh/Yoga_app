package com.example.yoga_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "YogaDB";
    // Incremented version to 5 so that onUpgrade is triggered
    private static final int DATABASE_VERSION = 5;

    // Table: YogaCourse
    public static final String TABLE_COURSE = "YogaCourse";
    public static final String COLUMN_COURSE_ID = "_id";
    public static final String COLUMN_DAYOFWEEK = "dayofweek";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CAPACITY = "capacity";      // New: Capacity
    public static final String COLUMN_DURATION = "duration";      // New: Duration (in minutes)
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DESCRIPTION = "description";

    // Table: ClassInstance
    public static final String TABLE_INSTANCE = "ClassInstance";
    public static final String COLUMN_INSTANCE_ID = "_id";
    public static final String COLUMN_INSTANCE_COURSE_ID = "course_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TEACHER = "teacher";
    public static final String COLUMN_COMMENTS = "comments";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Create YogaCourse table with the new columns
            String CREATE_TABLE_YOGACOURSE = "CREATE TABLE " + TABLE_COURSE + " ("
                    + COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_DAYOFWEEK + " TEXT NOT NULL, "
                    + COLUMN_TIME + " TEXT NOT NULL, "
                    + COLUMN_CAPACITY + " INTEGER NOT NULL, "
                    + COLUMN_DURATION + " INTEGER NOT NULL, "
                    + COLUMN_PRICE + " REAL NOT NULL, "
                    + COLUMN_TYPE + " TEXT NOT NULL, "
                    + COLUMN_DESCRIPTION + " TEXT)";
            db.execSQL(CREATE_TABLE_YOGACOURSE);

            // Create ClassInstance table
            String CREATE_TABLE_CLASSINSTANCE = "CREATE TABLE " + TABLE_INSTANCE + " ("
                    + COLUMN_INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_INSTANCE_COURSE_ID + " INTEGER NOT NULL, "
                    + COLUMN_DATE + " TEXT NOT NULL, "
                    + COLUMN_TEACHER + " TEXT NOT NULL, "
                    + COLUMN_COMMENTS + " TEXT, "
                    + "FOREIGN KEY(" + COLUMN_INSTANCE_COURSE_ID + ") REFERENCES "
                    + TABLE_COURSE + "(" + COLUMN_COURSE_ID + ") ON DELETE CASCADE)";
            db.execSQL(CREATE_TABLE_CLASSINSTANCE);

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error creating database: " + e.getMessage());
        }
    }

    // Called when the database needs to be upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // If the old version is less than 5, add the new columns
        if (oldVersion < 5) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_COURSE + " ADD COLUMN " + COLUMN_CAPACITY + " INTEGER NOT NULL DEFAULT 0");
                db.execSQL("ALTER TABLE " + TABLE_COURSE + " ADD COLUMN " + COLUMN_DURATION + " INTEGER NOT NULL DEFAULT 0");
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Error upgrading database: " + e.getMessage());
            }
        }
    }

    // --------------------------
    // CRUD for YogaCourse
    // --------------------------
    public long createNewYogaCourse(String dow, String time, int capacity, int duration, float price, String type, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAYOFWEEK, dow);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_CAPACITY, capacity);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_DESCRIPTION, description);

        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_COURSE, null, values);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting course: " + e.getMessage());
        } finally {
            db.close();
        }
        return result;
    }

    public Cursor readAllYogaCourse() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_COURSE,
                new String[]{COLUMN_COURSE_ID, COLUMN_DAYOFWEEK, COLUMN_TIME, COLUMN_CAPACITY, COLUMN_DURATION, COLUMN_PRICE, COLUMN_TYPE, COLUMN_DESCRIPTION},
                null, null, null, null, COLUMN_COURSE_ID + " DESC");
    }

    public Cursor getYogaCourseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_COURSE,
                new String[]{COLUMN_COURSE_ID, COLUMN_DAYOFWEEK, COLUMN_TIME, COLUMN_CAPACITY, COLUMN_DURATION, COLUMN_PRICE, COLUMN_TYPE, COLUMN_DESCRIPTION},
                COLUMN_COURSE_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public boolean updateYogaCourse(int id, String dow, String time, int capacity, int duration, float price, String type, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAYOFWEEK, dow);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_CAPACITY, capacity);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_DESCRIPTION, description);
        int rows = db.update(TABLE_COURSE, values, COLUMN_COURSE_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean deleteYogaCourse(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_COURSE, COLUMN_COURSE_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_COURSE);
        db.execSQL("DELETE FROM " + TABLE_INSTANCE);
        db.close();
    }

    // --------------------------
    // CRUD for ClassInstance
    // --------------------------
    public long createClassInstance(int courseId, String date, String teacher, String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INSTANCE_COURSE_ID, courseId);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TEACHER, teacher);
        values.put(COLUMN_COMMENTS, comments);
        long result = db.insert(TABLE_INSTANCE, null, values);
        db.close();
        return result;
    }

    public Cursor readAllInstances() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_INSTANCE,
                new String[]{COLUMN_INSTANCE_ID, COLUMN_INSTANCE_COURSE_ID, COLUMN_DATE, COLUMN_TEACHER, COLUMN_COMMENTS},
                null, null, null, null, COLUMN_INSTANCE_ID + " DESC");
    }

    public Cursor readInstancesForCourse(int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_INSTANCE,
                new String[]{COLUMN_INSTANCE_ID, COLUMN_INSTANCE_COURSE_ID, COLUMN_DATE, COLUMN_TEACHER, COLUMN_COMMENTS},
                COLUMN_INSTANCE_COURSE_ID + "=?",
                new String[]{String.valueOf(courseId)},
                null, null, COLUMN_INSTANCE_ID + " DESC");
    }

    public Cursor getClassInstanceById(int instanceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_INSTANCE,
                new String[]{COLUMN_INSTANCE_ID, COLUMN_INSTANCE_COURSE_ID, COLUMN_DATE, COLUMN_TEACHER, COLUMN_COMMENTS},
                COLUMN_INSTANCE_ID + "=?",
                new String[]{String.valueOf(instanceId)},
                null, null, null);
    }

    public boolean updateClassInstance(int instanceId, String date, String teacher, String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TEACHER, teacher);
        values.put(COLUMN_COMMENTS, comments);
        int rows = db.update(TABLE_INSTANCE, values, COLUMN_INSTANCE_ID + "=?", new String[]{String.valueOf(instanceId)});
        db.close();
        return rows > 0;
    }

    public boolean deleteClassInstance(int instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_INSTANCE, COLUMN_INSTANCE_ID + "=?", new String[]{String.valueOf(instanceId)});
        db.close();
        return rows > 0;
    }

    // --------------------------
    // Additional Search Methods (optional)
    // --------------------------
    public Cursor searchCoursesByType(String partialType) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_COURSE +
                        " WHERE " + COLUMN_TYPE + " LIKE ? ORDER BY " + COLUMN_COURSE_ID + " DESC",
                new String[]{"%" + partialType + "%"});
    }

    public Cursor searchCoursesAdvanced(String partialType, String day, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder query = new StringBuilder();
        ArrayList<String> args = new ArrayList<>();

        if (!date.isEmpty()) {
            query.append("SELECT DISTINCT c.* FROM ").append(TABLE_COURSE)
                    .append(" c INNER JOIN ").append(TABLE_INSTANCE)
                    .append(" i ON c.").append(COLUMN_COURSE_ID).append(" = i.").append(COLUMN_INSTANCE_COURSE_ID)
                    .append(" WHERE 1=1");
        } else {
            query.append("SELECT * FROM ").append(TABLE_COURSE).append(" WHERE 1=1");
        }

        if (!partialType.isEmpty()) {
            query.append(" AND c.").append(COLUMN_TYPE).append(" LIKE ?");
            args.add("%" + partialType + "%");
        }

        if (!day.isEmpty()) {
            if (!date.isEmpty()) {
                query.append(" AND c.").append(COLUMN_DAYOFWEEK).append(" = ?");
            } else {
                query.append(" AND ").append(COLUMN_DAYOFWEEK).append(" = ?");
            }
            args.add(day);
        }

        if (!date.isEmpty()) {
            query.append(" AND i.").append(COLUMN_DATE).append(" = ?");
            args.add(date);
        }

        query.append(" ORDER BY c.").append(COLUMN_COURSE_ID).append(" DESC");

        return db.rawQuery(query.toString(), args.toArray(new String[0]));
    }
}
