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

public class DatabaseHandlerInstantTask extends SQLiteOpenHelper {

    //Note: This class creates a handler to create/ manage SQLite databases

    private static final int DATABASE_VERSION = 8;

    private static final String DATABASE_NAME = "InstantTasks";

    // table name
    private static final String TABLE_InstantTasks = "InstantTasks";

    // Table Columns names
    private static final String KEY_DATE_DAY = "Date_day";
    private static final String KEY_DATE_MONTH = "Date_month";
    private static final String KEY_DATE_YEAR = "Date_year";
    private static final String KEY_TIME_HOUR = "Time_hour";
    private static final String KEY_TIME_MINUTE = "Time_minute";
    private static final String KEY_TIME_SECOND = "Time_second";
    private static final String KEY_PLACE = "Place";
    private static final String KEY_NOTE = "Note";

    public DatabaseHandlerInstantTask(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE;

        CREATE_TABLE = "create table " + TABLE_InstantTasks + "("
                + KEY_DATE_DAY + " int not null, " + KEY_DATE_MONTH + " int not null, " + KEY_DATE_YEAR + " int not null, " +  KEY_TIME_HOUR + " int not null, " + KEY_TIME_MINUTE + " int not null, " + KEY_TIME_SECOND + " int not null, " + KEY_PLACE + " text not null, " + KEY_NOTE
                + " text);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_InstantTasks);
        onCreate(db);
    }

    void add(InstantTask instantTask) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues task = new ContentValues();
        task.put(KEY_DATE_DAY, instantTask.getDate().get(Calendar.DAY_OF_MONTH));
        task.put(KEY_DATE_MONTH, instantTask.getDate().get(Calendar.MONTH)+1);
        task.put(KEY_DATE_YEAR, instantTask.getDate().get(Calendar.YEAR));
        task.put(KEY_TIME_HOUR, instantTask.getTimeHour()); //Task Time hour
        task.put(KEY_TIME_MINUTE, instantTask.getTimeMinute()); //Task Time minute
        task.put(KEY_TIME_SECOND, instantTask.getTimeSecond()); //Task Time second
        task.put(KEY_PLACE, instantTask.getPlace()); //Task Place
        task.put(KEY_NOTE, instantTask.getNote()); //Task Note


        // Inserting Row
        db.insert(TABLE_InstantTasks, null, task);
        db.close(); // Closing database connection
    }



    // Getting All data corresponding to the day 'day'
    public List<InstantTask> getAllTasksOnDayAndCity(GregorianCalendar today, String city) {
        List<InstantTask> TaskList = new ArrayList<InstantTask>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_InstantTasks + " WHERE Date_day = " + String.valueOf(today.get(Calendar.DAY_OF_MONTH)) +
                " AND Date_month = " + String.valueOf(today.get(Calendar.MONTH)+1) + " AND Date_year = " + String.valueOf(today.get(Calendar.YEAR)) +
                " AND Place LIKE " +  ("'%").trim() + city.trim() + "%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                InstantTask task = new InstantTask();
                GregorianCalendar Date = new GregorianCalendar(Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(0)));
                Time time = new Time(Integer.parseInt(cursor.getString(3)),Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)));
                task.setDate(Date);
                task.setTime(time);
                task.setPlace(cursor.getString(6));
                task.setNote(cursor.getString(7));
                TaskList.add(task);
            } while (cursor.moveToNext());
        }

        return TaskList;
    }



    //Get the list of cities
    public ArrayList<String> GetInstantTskCities(GregorianCalendar today){
        ArrayList<String> cities = new ArrayList<String>();

        //Select distinct cities
        String selectQuery = "SELECT * FROM " + TABLE_InstantTasks + " WHERE Date_day = " + String.valueOf(today.get(Calendar.DAY_OF_MONTH)) +
                " AND Date_month = " + String.valueOf(today.get(Calendar.MONTH)+1) + " AND Date_year = " + String.valueOf(today.get(Calendar.YEAR));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping through all the rows
        if(cursor.moveToFirst()){
            do{
                cities.add(cursor.getString(6));
            }while (cursor.moveToNext());
        }

        return cities;
    }



    // Updating single record
    public int update(InstantTask oldInstantTask, InstantTask newInstantTask) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues task = new ContentValues();
        task.put(KEY_DATE_DAY, newInstantTask.getDate().get(Calendar.DAY_OF_MONTH));
        task.put(KEY_DATE_MONTH, newInstantTask.getDate().get(Calendar.MONTH)+1);
        task.put(KEY_DATE_YEAR, newInstantTask.getDate().get(Calendar.YEAR));
        task.put(KEY_TIME_HOUR, newInstantTask.getTimeHour()); //Task Time hour
        task.put(KEY_TIME_MINUTE, newInstantTask.getTimeMinute()); //Task Time minute
        task.put(KEY_TIME_SECOND, newInstantTask.getTimeSecond()); //Task Time second
        task.put(KEY_PLACE, newInstantTask.getPlace()); //Task Place
        task.put(KEY_NOTE, newInstantTask.getNote()); //Task Note

        // updating row
        return db.update(TABLE_InstantTasks, task, KEY_TIME_HOUR + " =?" + "and" + KEY_TIME_SECOND + " =?",
                new String[] { String.valueOf(oldInstantTask.getTimeHour()), String.valueOf(oldInstantTask.getTimeSecond()) });
    }
}