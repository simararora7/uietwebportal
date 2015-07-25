package uietwebportal.simararora.uietwebportal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AutoCompleteTextViewDatabase {
    private static final String KEY_ROLL_NUMBER = "rollNo";
    private static final String DATABASE_NAME = "rollNoDatabase";
    private static final String DATABASE_TABLE_RESULT_ROLL_NUMBER = "resultRollNo";
    private static final String DATABASE_TABLE_ATTENDANCE_ROLL_NUMBER = "attendanceRollNo";
    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper databaseHelper;
    private final Context context;
    private SQLiteDatabase sqliteDatabase;

    public AutoCompleteTextViewDatabase(Context context) {
        this.context = context;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE_RESULT_ROLL_NUMBER + "(" + KEY_ROLL_NUMBER + " TEXT NOT NULL," + "PRIMARY KEY(" + KEY_ROLL_NUMBER + "));");
            db.execSQL("CREATE TABLE " + DATABASE_TABLE_ATTENDANCE_ROLL_NUMBER + "(" + KEY_ROLL_NUMBER + " TEXT NOT NULL," + "PRIMARY KEY(" + KEY_ROLL_NUMBER + "));");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + DATABASE_NAME);
            onCreate(db);
        }
    }

    public AutoCompleteTextViewDatabase open() {
        databaseHelper = new DatabaseHelper(context);
        sqliteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    public void addRollNumberToResultTable(String rollNo) {
        addRollNumber(rollNo, DATABASE_TABLE_RESULT_ROLL_NUMBER);
    }

    public void addRollNumberToAttendanceTable(String rollNo) {
        addRollNumber(rollNo, DATABASE_TABLE_ATTENDANCE_ROLL_NUMBER);
    }

    private void addRollNumber(String rollNo, String table) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ROLL_NUMBER, rollNo);
        sqliteDatabase.insert(table, null, contentValues);
    }

    public ArrayList<String> getRollNumbersFromResultTable() {
        return getRollNumbers(DATABASE_TABLE_RESULT_ROLL_NUMBER);
    }

    public ArrayList<String> getRollNumbersFromAttendanceTable() {
        return getRollNumbers(DATABASE_TABLE_ATTENDANCE_ROLL_NUMBER);
    }

    private ArrayList<String> getRollNumbers(String table) {
        ArrayList<String> rollNumbers = new ArrayList<>();
        String[] columns = {KEY_ROLL_NUMBER};
        int indexOfRollNumber;
        Cursor cursor = sqliteDatabase.query(table, columns, null, null, null, null, null);
        indexOfRollNumber = cursor.getColumnIndex(KEY_ROLL_NUMBER);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            rollNumbers.add(cursor.getString(indexOfRollNumber));
        return rollNumbers;
    }
}