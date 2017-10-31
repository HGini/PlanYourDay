package made.by.hemangini.planyourday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHandlerDurationTask extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 21;

    private static final String DATABASE_NAME = "DurationTasks";

    // table name
    private static final String TABLE_DurationTasks = "DurationTasks";

    // Table Columns names
    private static final String KEY_DATE_DAY = "Date_day";
    private static final String KEY_DATE_MONTH = "Date_month";
    private static final String KEY_DATE_YEAR = "Date_year";
    private static final String KEY_TIME_FROM_HOUR = "Time_from_hour";
    private static final String KEY_TIME_FROM_MINUTE = "Time_from_minute";
    private static final String KEY_TIME_FROM_SECOND = "Time_from_second";
    private static final String KEY_TIME_TO_HOUR = "Time_to_hour";
    private static final String KEY_TIME_TO_MINUTE = "Time_to_minute";
    private static final String KEY_TIME_TO_SECOND = "Time_to_second";
    private static final String KEY_PLACE = "Place";
    private static final String KEY_NOTE = "Note";

    public DatabaseHandlerDurationTask(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE;

        CREATE_TABLE = "create table " + TABLE_DurationTasks + "("
                + KEY_DATE_DAY + " int not null, " + KEY_DATE_MONTH + " int not null, " + KEY_DATE_YEAR + " int not null, " + KEY_TIME_FROM_HOUR + " int not null, " + KEY_TIME_FROM_MINUTE + " int not null, " + KEY_TIME_FROM_SECOND + " int not null, " + KEY_TIME_TO_HOUR + " int not null, " + KEY_TIME_TO_MINUTE + " int not null, " + KEY_TIME_TO_SECOND + " int not null, " + KEY_PLACE + " text not null, " + KEY_NOTE
                + " text);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DurationTasks);
        onCreate(db);
    }

    void add(DurationTask DurationTask) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues task = new ContentValues();
        task.put(KEY_DATE_DAY, DurationTask.getDate().get(Calendar.DAY_OF_MONTH));
        task.put(KEY_DATE_MONTH, DurationTask.getDate().get(Calendar.MONTH)+1);
        task.put(KEY_DATE_YEAR, DurationTask.getDate().get(Calendar.YEAR));
        task.put(KEY_TIME_FROM_HOUR, DurationTask.getTimeFromHour()); //Task Time From hour
        task.put(KEY_TIME_FROM_MINUTE, DurationTask.getTimeFromMinute()); //Task Time From minute
        task.put(KEY_TIME_FROM_SECOND, DurationTask.getTimeFromSecond()); //Task Time From second
        task.put(KEY_TIME_TO_HOUR, DurationTask.getTimeToHour()); //Task Time To hour
        task.put(KEY_TIME_TO_MINUTE, DurationTask.getTimeToMinute()); //Task Time To minute
        task.put(KEY_TIME_TO_SECOND, DurationTask.getTimeToSecond()); //Task Time To second
        task.put(KEY_PLACE, DurationTask.getPlace()); //Task Place
        task.put(KEY_NOTE, DurationTask.getNote()); //Task Note



        // Inserting Row
        db.insert(TABLE_DurationTasks, null, task);
        db.close(); // Closing database connection
    }

    // Getting single task
    DurationTask getTaskAtFromTime(Time taskTime) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DurationTasks, null, KEY_TIME_FROM_HOUR + "=? AND " + KEY_TIME_FROM_MINUTE + "=? AND " + KEY_TIME_FROM_SECOND,
                new String[] { String.valueOf(taskTime.hours), String.valueOf(taskTime.minutes), String.valueOf(taskTime.seconds) },
                null, null, null, null);
        //public method query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
        if (cursor != null) {
            cursor.moveToFirst();
        }
        GregorianCalendar Date = new GregorianCalendar(Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(0)));
        Time timefrom = new Time(Integer.parseInt(cursor.getString(3)),Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)));
        Time timeto = new Time(Integer.parseInt(cursor.getString(6)), (Integer.parseInt(cursor.getString(7))), Integer.parseInt(cursor.getString(8)));
        DurationTask task = new DurationTask(Date, timefrom, timeto, cursor.getString(9), cursor.getString(10));

        return task;
    }

    // Getting all data for the day 'day' and 'city'
    public List<DurationTask> getAllTasksOnDayAndCity(GregorianCalendar today , String city) {
        List<DurationTask> TaskList = new ArrayList<DurationTask>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DurationTasks + " WHERE Date_day = " + String.valueOf(today.get(Calendar.DAY_OF_MONTH)) +
                " AND Date_month = " + String.valueOf(today.get(Calendar.MONTH)+1) + " AND Date_year = " + String.valueOf(today.get(Calendar.YEAR)) +
                " AND Place LIKE " +  ("'%").trim() + city.trim() + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DurationTask task = new DurationTask();
                GregorianCalendar Date = new GregorianCalendar(Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(0)));
                Time timefrom = new Time(Integer.parseInt(cursor.getString(3)),Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)));
                Time timeto = new Time(Integer.parseInt(cursor.getString(6)), (Integer.parseInt(cursor.getString(7))), Integer.parseInt(cursor.getString(8)));
                task.setDate(Date);
                task.setTimeFrom(timefrom);
                task.setTimeTo(timeto);
                task.setPlace(cursor.getString(9));
                task.setNote(cursor.getString(10));
                TaskList.add(task);
            } while (cursor.moveToNext());
        }

        return TaskList;
    }


    //Get the list of cities
    public ArrayList<String> GetDurationTskCities(GregorianCalendar today){
        ArrayList<String> cities = new ArrayList<String>();

        //Select distinct cities
        String selectQuery = "SELECT * FROM " + TABLE_DurationTasks + " WHERE Date_day = " + String.valueOf(today.get(Calendar.DAY_OF_MONTH)) +
                " AND Date_month = " + String.valueOf(today.get(Calendar.MONTH)+1) + " AND Date_year = " + String.valueOf(today.get(Calendar.YEAR));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping through all the rows
        if(cursor.moveToFirst()){
            do{
               cities.add(cursor.getString(9));

            }while (cursor.moveToNext());
        }

        return cities;
    }




    // Updating single record
    public int update(DurationTask oldDurationTask, DurationTask newDurationTask) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues task = new ContentValues();
        task.put(KEY_DATE_DAY, newDurationTask.getDate().get(Calendar.DAY_OF_MONTH));
        task.put(KEY_DATE_MONTH, newDurationTask.getDate().get(Calendar.MONTH)+1);
        task.put(KEY_DATE_YEAR, newDurationTask.getDate().get(Calendar.YEAR));
        task.put(KEY_TIME_FROM_HOUR, newDurationTask.getTimeFromHour()); //Task Time From hour
        task.put(KEY_TIME_FROM_MINUTE, newDurationTask.getTimeFromMinute()); //Task Time From minute
        task.put(KEY_TIME_FROM_SECOND, newDurationTask.getTimeFromSecond()); //Task Time From second
        task.put(KEY_TIME_TO_HOUR, newDurationTask.getTimeToHour()); //Task Time To hour
        task.put(KEY_TIME_TO_MINUTE, newDurationTask.getTimeToMinute()); //Task Time To minute
        task.put(KEY_TIME_TO_SECOND, newDurationTask.getTimeToSecond()); //Task Time To second
        task.put(KEY_PLACE, newDurationTask.getPlace()); //Task Place
        task.put(KEY_NOTE, newDurationTask.getNote()); //Task Note


        // updating row
        return db.update(TABLE_DurationTasks, task, KEY_TIME_FROM_HOUR + " =?" + "and" + KEY_TIME_FROM_SECOND + " =?",
                new String[] { String.valueOf(oldDurationTask.getTimeFromHour()), String.valueOf(oldDurationTask.getTimeFromSecond()) });
    }
}