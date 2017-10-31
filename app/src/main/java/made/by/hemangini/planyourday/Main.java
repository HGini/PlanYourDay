package made.by.hemangini.planyourday;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.*;

import made.by.hemangini.planyourday.R;

import java.util.Calendar;
import java.util.Locale;


public class Main extends FragmentActivity implements GetTimeDistance.AsyncResponse, AutocompleteAdapter.GetAddressFromAdapter, GetLatLong.GetLatLongInterface{

    private String Note;
    private String Place;
    private static GregorianCalendar date;
    private Time TimeInstant = null;
    private Time TimeFrom = null;
    private Time TimeTo = null;
    private boolean isTimeInstant; //'true' if the task entered is InstantTask
    private DatabaseHandlerDurationTask dbDu;
    private DatabaseHandlerInstantTask dbIn;
    private List<InstantTask> InstantTaskList = new ArrayList<InstantTask>();
    private List<DurationTask> DurationTaskList = new ArrayList<DurationTask>();
    final GregorianCalendar today = new GregorianCalendar();
    private ArrayList<String> address = new ArrayList<String>();
    private Context MainContext = this;
    private Main MainInstance = this;
    private String LatLong = "";
    private AutocompleteAdapter PlaceAutocompleteAdapter;
    private List<InstantTask> FinalTaskList = new ArrayList<InstantTask>();
    private ArrayList<IDdDistanceTime> IDdDistanceTimeList;
    private int TaskTimeHours = 1;
    private ProgressDialog progressBar;
    private Toast toastFrom;
    private Toast toastTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = new ProgressDialog(this);

        final EditText note = (EditText) findViewById(R.id.EnteredNote);
        final DelayAutocompleteTextview country = (DelayAutocompleteTextview) findViewById(R.id.autocomplete_country);
        final DelayAutocompleteTextview city = (DelayAutocompleteTextview) findViewById(R.id.autocomplete_city);
        final DelayAutocompleteTextview place = (DelayAutocompleteTextview) findViewById(R.id.autocomplete_locality);
        //Work with 'country' DelayAutocompleteTextview
        country.setAdapter(new AutocompleteAdapter(this, android.R.layout.simple_dropdown_item_1line));
        country.setLoadingIndicator(
                (android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator_country));
        country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String countryString = (String) adapterView.getItemAtPosition(position);
                country.setText(countryString);

                //Get the ISO-3166 country code
                String[] locales = Locale.getISOCountries();
                for (String countryCode : locales) {
                    Locale obj = new Locale("", countryCode);
                    if( obj.getDisplayCountry().equals(countryString)){

                        final String CountryCode = obj.getCountry().toLowerCase();

                        //Work with 'city' DelayAutocompleteTextview
                        city.setAdapter(new AutocompleteAdapter(MainContext, android.R.layout.simple_dropdown_item_1line,"locality", CountryCode, ""));
                        city.setLoadingIndicator(
                                (android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator_city));
                        city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                String cityString = ((String) adapterView.getItemAtPosition(position)).split(",")[0];
                                city.setText(cityString);
                                if(position < address.size()){
                                    GetLatLong getLatLong = new GetLatLong(address.get(position), MainContext);


                                    //Work with 'place' DelayAutocompleteTextview
                                    PlaceAutocompleteAdapter = new AutocompleteAdapter(MainContext, android.R.layout.simple_dropdown_item_1line,"sublocality_level_", CountryCode, cityString);
                                    place.setAdapter(PlaceAutocompleteAdapter);
                                    place.setLoadingIndicator((android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator_place));
                                    place.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                            Place = address.get(position);
                                            String placeString = (String) adapterView.getItemAtPosition(position);
                                            place.setText(placeString.split(",")[0]);

                                        }
                                    });
                                }
                            }
                        });
                    }}
            }
        });


        final Button but1 = (Button) findViewById(R.id.Save);
        but1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Get the data entered by user
                Note = note.getText().toString();
                if (isTimeInstant) {
                    //Create database handler object for InstantTask
                    DbHandlerInstantAsyncTask dbHandlerInstantAsyncTask = new DbHandlerInstantAsyncTask();
                    dbHandlerInstantAsyncTask.execute(MainContext);

                } else {
                    //Create database handler object for DurationTask
                    DbHandlerDurationAsyncTask dbHandlerDurationAsyncTask = new DbHandlerDurationAsyncTask();
                    dbHandlerDurationAsyncTask.execute(MainContext);
                }
            }
        });
    }




    public void getAddressFromAdapter(ArrayList<String> addrss){
        address = addrss;
    }



    public void getLatLongInterfaceMethod(String[] latlong){
        LatLong = "&location=" + latlong[0] + "," + latlong[1] +"&radius=50000";
    }



    public String getLatLongFromMain(){
        return LatLong;
    }


    public class InstantTaskComparator implements Comparator<InstantTask> {
        //method to compare class instances

        public int compare(InstantTask one, InstantTask other) {
            if (one.getTimeHour() == other.getTimeHour()) {
                if (one.getTimeMinute() == other.getTimeMinute()) {
                    return one.getTimeSecond() - other.getTimeSecond();
                }else{
                    return one.getTimeMinute() - other.getTimeMinute();
                }
            } else {
                return one.getTimeHour() - other.getTimeHour();
            }
        }

    }

    public class IDdDistanceTimeComparator implements Comparator<IDdDistanceTime>{

        public int compare(IDdDistanceTime one, IDdDistanceTime two){
            return one.distance - two.distance;
        }
    }

    public class DbHandlerInstantAsyncTask extends AsyncTask<Context, Void, DatabaseHandlerInstantTask> {

        @Override
        public DatabaseHandlerInstantTask doInBackground(Context... contexts) {
            dbIn = new DatabaseHandlerInstantTask(contexts[0]);
            dbDu = new DatabaseHandlerDurationTask(contexts[0]);

            // Insert task
            dbIn.add(new InstantTask(date, TimeInstant, Place, Note));
            return dbIn;
        }

        @Override
        public void onPostExecute(DatabaseHandlerInstantTask db) {
            super.onPostExecute(db);

            //Get the list of distinct cities to choose from to plan the day
            GetCities();
        }
    }


    public void GetCities(){
        //Get a list of distinct cities added to the database
        final ArrayList<String> cities = new ArrayList<String>();
        cities.addAll(dbIn.GetInstantTskCities(today));
        cities.addAll(dbDu.GetDurationTskCities(today));

        //Pick out the name of the cities from the whole address
        final ArrayList<String> citiesName = new ArrayList<String>();
        for(String str : cities){
            citiesName.add(str.split(",")[1]);
        }
        LinkedHashSet<String> hs = new LinkedHashSet<String>();
        hs.addAll(citiesName);
        citiesName.clear();
        citiesName.addAll(hs);
        Collections.sort(citiesName);



        if (citiesName.size() > 1){

            //Create dialog to pick a city to plan the day
            DialogFragment dialogFragment = new DialogFragment() {
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    // Set the dialog title
                    builder.setTitle("Plan your day for")
                            .setSingleChoiceItems(citiesName.toArray(new CharSequence[citiesName.size()]), -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Plan the best route for the selected city
                                    Globals.place = citiesName.get(i);
                                    PlanTheBestRoute(citiesName.get(i));

                                    //dismiss the dialog
                                    dialogInterface.dismiss();
                                }
                            });

                    return builder.create();
                }
            };
            dialogFragment.show(getSupportFragmentManager(), "pickTheCity");
        }else if(citiesName.size() == 1) {
            //If only one city, no need to display alertdialog
            //Plan the best route for the city
            Globals.place = citiesName.get(0);
            PlanTheBestRoute(citiesName.get(0));

        }else{
            Intent j = new Intent(this, NoTasksForToday.class);
            startActivity(j);
        }
        //Display the progress of the application
        progressBar.show(this,"","Finding the best route for today...  Please wait  :)",false);

    }




    public void PlanTheBestRoute(String city) {
        InstantTaskList = dbIn.getAllTasksOnDayAndCity(today, city);
        DurationTaskList = dbDu.getAllTasksOnDayAndCity(today, city);
        if (InstantTaskList.size() == 0) {
            //No instant tasks planned for today
            Time dtime = new Time((DurationTaskList.get(0).getTimeFromHour() + DurationTaskList.get(0).getTimeToHour()) / 2, (DurationTaskList.get(0).getTimeFromMinute() + DurationTaskList.get(0).getTimeToMinute()) / 2, (DurationTaskList.get(0).getTimeFromSecond() + DurationTaskList.get(0).getTimeToSecond()) / 2);
            InstantTask instantTask = new InstantTask(DurationTaskList.get(0).getDate(), dtime, DurationTaskList.get(0).getPlace(), DurationTaskList.get(0).getNote());
            InstantTaskList.add(instantTask);
            DurationTaskList.remove(0);
        }



        //Sort the instant task list based on time
        Collections.sort(InstantTaskList, new InstantTaskComparator());
        FinalTaskList = InstantTaskList;


        if(DurationTaskList.size() == 0){
            progressBar.cancel();
            Globals.FinalTasklist = (ArrayList<InstantTask>) FinalTaskList;
            Intent i = new Intent(this, PlannedDay.class);
            startActivity(i);
        }else {
            CreateIDdDistanceTimeList(DurationTaskList.get(0));
        }

    }


    public void CreateIDdDistanceTimeList(DurationTask tsk){
        InstantTask finalValidTask = null;
        boolean LastTaskIsValidInstantTask = false;
        IDdDistanceTimeList = new ArrayList<IDdDistanceTime>();

        boolean isThisFirstValidInstTask = true;
        //Insert this task after the instant task whose location is the closest to this task's location
        for (InstantTask ftsk : FinalTaskList) {

            if ((ftsk.getTimeHour() > tsk.getTimeFromHour()) && ftsk.getTimeHour() < tsk.getTimeToHour()) {
                finalValidTask = ftsk;

                if (isThisFirstValidInstTask && (FinalTaskList.indexOf(ftsk) > 0)) {
                    InstantTask beforeftsk = FinalTaskList.get(FinalTaskList.indexOf(ftsk) - 1);
                    String[][] OriginDestinations3 = new String[1][2];
                    OriginDestinations3[0][0] = beforeftsk.getPlace();
                    OriginDestinations3[0][1] = tsk.getPlace();
                    GetTimeDistance getTimeDistanceNew = new GetTimeDistance(this, OriginDestinations3, beforeftsk, 0, 0, FinalTaskList.indexOf(finalValidTask), false);
                }
                String[][] OriginDestinations = new String[1][2];
                OriginDestinations[0][0] = ftsk.getPlace();
                OriginDestinations[0][1] = tsk.getPlace();
                GetTimeDistance getTimeDistanceNew = new GetTimeDistance(this, OriginDestinations, ftsk, 0, 0, FinalTaskList.indexOf(finalValidTask), false);
                isThisFirstValidInstTask = false;
            }
        }
        //Below condition tests whether the last valid instant task was the last instant task in today's sorted instant task list 'FinalTaskList'
        if (FinalTaskList.indexOf(finalValidTask) == FinalTaskList.size() - 1 )
        {
            LastTaskIsValidInstantTask = true;
        } else if (FinalTaskList.indexOf(finalValidTask) < FinalTaskList.size() - 1 && FinalTaskList.indexOf(finalValidTask) >= 0 ) {
            //Then we also include the instant task in the FinalTaskList after the last valid instant
            LastTaskIsValidInstantTask = false;
            String[][] OriginDestinations = new String[1][2];
            OriginDestinations[0][0] = FinalTaskList.get(FinalTaskList.indexOf(finalValidTask) + 1).getPlace();
            OriginDestinations[0][1] = tsk.getPlace();
            GetTimeDistance getTimeDistanceNew = new GetTimeDistance(this, OriginDestinations, FinalTaskList.get(FinalTaskList.indexOf(finalValidTask) + 1), 0,
                    0, FinalTaskList.indexOf(finalValidTask), LastTaskIsValidInstantTask);

        }

        //The below code is to call the 'FindClosestInstantTask' method for the one last time for the last duration task
        String[][] OriginDestinations2 = new String[1][2];
        OriginDestinations2[0][0] = FinalTaskList.get(0).getPlace();
        OriginDestinations2[0][1] = DurationTaskList.get(0).getPlace();
        GetTimeDistance getTimeDistanceNew = new GetTimeDistance(this, OriginDestinations2, FinalTaskList.get(0), DurationTaskList.indexOf(tsk) + 1, DurationTaskList.indexOf(tsk), FinalTaskList.indexOf(finalValidTask), LastTaskIsValidInstantTask);



    }



    //Implement the AsyncResponse interface's method 'onProcessFinish'
    public void onProcessFinish(String[][] OriginDestinations, InstantTask ftsk, ArrayList<Integer> TimeDistance, int IDofCurrentDurationTask, int IDofLastDurationTask , int finalValidInstantTaskID, boolean LastTaskIsValidInstantTask){
        if(IDofLastDurationTask < IDofCurrentDurationTask){
            FindClosestInstantTask(DurationTaskList.get(IDofLastDurationTask), IDdDistanceTimeList, finalValidInstantTaskID, LastTaskIsValidInstantTask);

        }else {
            int calculatedDistance = TimeDistance.get(0);
            Time TravelTime = timeConversion(TimeDistance.get(1));
            IDdDistanceTime iDdDistanceTime = new IDdDistanceTime(FinalTaskList.indexOf(ftsk), calculatedDistance, TravelTime);
            IDdDistanceTimeList.add(iDdDistanceTime);
        }
    }


    public void FindClosestInstantTask(DurationTask tsk , ArrayList<IDdDistanceTime> IDdDistanceTimeList, int finalValidInstantTaskID, boolean LastTaskIsValidInstantTask){
        if(IDdDistanceTimeList.size() == 0){
            if(tsk.getTimeFromHour() >= FinalTaskList.get(FinalTaskList.size()-1).getTimeHour()){
                if(tsk.getTimeFromHour()+ 2 < 24) {
                    InstantTask InsTsk = new InstantTask(tsk.getDate(), new Time(tsk.getTimeFromHour() + 2, tsk.getTimeFromMinute(), tsk.getTimeFromSecond()), tsk.getPlace(), tsk.getNote());
                    FinalTaskList.add(InsTsk);
                }
            }else if( tsk.getTimeToHour() <=  FinalTaskList.get(0).getTimeHour()){
                if(tsk.getTimeToHour()-2 >= 0) {
                    InstantTask InsTsk = new InstantTask(tsk.getDate(), new Time(tsk.getTimeToHour() - 2, tsk.getTimeToMinute(), tsk.getTimeToSecond()), tsk.getPlace(), tsk.getNote());
                    InstantTask FirstTask = FinalTaskList.get(0);
                    FinalTaskList.add(0, InsTsk);
                    FinalTaskList.add(1, FirstTask);
                    FinalTaskList.remove(0);
                }
            }
        }else if(IDdDistanceTimeList.size() > 0){
            boolean DurationTaskPlaced = false;
            IDdDistanceTime lastValidInstantTask = null;
            if(LastTaskIsValidInstantTask){
                lastValidInstantTask = IDdDistanceTimeList.get(IDdDistanceTimeList.size()-1);
            }else{
                lastValidInstantTask = IDdDistanceTimeList.get(IDdDistanceTimeList.size()-2);
            }
            Collections.sort(IDdDistanceTimeList, new IDdDistanceTimeComparator());
            for(IDdDistanceTime iDdDistanceTime : IDdDistanceTimeList) {
                int SelectedInstantTaskID = iDdDistanceTime.ID;
                int TravelTime = iDdDistanceTime.time.hours*3600 + iDdDistanceTime.time.minutes*60 + iDdDistanceTime.time.seconds;
                int InstantTaskTime = FinalTaskList.get(SelectedInstantTaskID).getTime().hours*3600 + FinalTaskList.get(SelectedInstantTaskID).getTime().minutes*60 + FinalTaskList.get(SelectedInstantTaskID).getTime().seconds;
                int expectedDurTskTime = InstantTaskTime - TravelTime;
                Time expDur = timeConversion(expectedDurTskTime);
                Time ExpectedDurationTaskTime = new Time(expDur.hours - TaskTimeHours, expDur.minutes, expDur.seconds);
                if (iDdDistanceTime.ID == 0) {
                    InstantTask InsTsk;
                    if (ExpectedDurationTaskTime.hours > tsk.getTimeFromHour() && ExpectedDurationTaskTime.hours < 24 && ExpectedDurationTaskTime.hours >= 0) {
                        //Above condition checks whether the expected task time that this app would assign to the duration task comes within the duration task's
                        //time window
                        InsTsk = new InstantTask(tsk.getDate(), ExpectedDurationTaskTime, tsk.getPlace(), tsk.getNote());
                        if (FinalTaskList.size() == 1) {
                            InstantTask OnlyInstantTask = FinalTaskList.get(0);
                            FinalTaskList.add(InsTsk);
                            FinalTaskList.add(OnlyInstantTask);
                            FinalTaskList.remove(SelectedInstantTaskID);

                        } else {
                            FinalTaskList.add(SelectedInstantTaskID, InsTsk);
                        }
                        DurationTaskPlaced = true;
                        break;
                    }
                } else if (iDdDistanceTime.ID > 0) {
                    int TaskBeforeInstTskID = (iDdDistanceTime.ID - 1);
                    Time TaskBeforeInstTskTravelTime = new Time();
                    Time TaskBeforeInstTskTime = FinalTaskList.get(TaskBeforeInstTskID).getTime();
                    if (ExpectedDurationTaskTime.hours > tsk.getTimeFromHour() && ExpectedDurationTaskTime.hours < 24 && ExpectedDurationTaskTime.hours >= 0) {
                        //Above condition checks whether the expected task time that this app would assign to the duration task comes within the duration task's
                        //time window
                        for (IDdDistanceTime iDdDistanceTime2 : IDdDistanceTimeList) {
                            if (iDdDistanceTime2.ID == TaskBeforeInstTskID) {
                                TaskBeforeInstTskTravelTime = iDdDistanceTime2.time;
                            }
                        }
                        if (ExpectedDurationTaskTime.hours - TaskTimeHours - TaskBeforeInstTskTravelTime.hours > TaskBeforeInstTskTime.hours)
                        //Above condition checks whether the travel time to go from the previous instant task to this selected (closest)
                        //instant task via this duration task is less than the difference between the time instants of the two instant tasks
                        {
                            InstantTask InsTsk = new InstantTask(tsk.getDate(), ExpectedDurationTaskTime, tsk.getPlace(), tsk.getNote());
                            if (FinalTaskList.size() == 1) {
                                InstantTask OnlyInstantTask = FinalTaskList.get(0);
                                FinalTaskList.add(InsTsk);
                                FinalTaskList.add(OnlyInstantTask);
                                FinalTaskList.remove(SelectedInstantTaskID);
                            } else {
                                FinalTaskList.add(SelectedInstantTaskID, InsTsk);
                            }
                            DurationTaskPlaced = true;
                            break;
                        }
                    }
                }
            }
            if (DurationTaskPlaced == false) {
                //checks if we were unable to place the duration task before any instant task
                int IDLastValidInstantTask = lastValidInstantTask.ID;
                Time TravelTimeLastValidTask = lastValidInstantTask.time;
                Time TravelTimeTaskAfterLastValidTask = new Time();
                for (IDdDistanceTime iDdDistanceTime3 : IDdDistanceTimeList) {
                    if (iDdDistanceTime3.ID == IDLastValidInstantTask + 1) {
                        TravelTimeTaskAfterLastValidTask = iDdDistanceTime3.time;
                    }
                }
                InstantTask LastValidInstantTask = FinalTaskList.get(IDLastValidInstantTask);

                if (IDLastValidInstantTask == FinalTaskList.size() - 1) {
                    //checks if the last valid instant task is also the last instant task in the FinalTaskList
                    if ((LastValidInstantTask.getTimeHour() + TravelTimeLastValidTask.hours + TaskTimeHours < tsk.getTimeToHour())) {
                        Time NewTskTime = new Time(LastValidInstantTask.getTimeHour() + lastValidInstantTask.time.hours +TaskTimeHours, LastValidInstantTask.getTimeMinute(), LastValidInstantTask.getTimeSecond());
                        InstantTask InsTsk = new InstantTask(tsk.getDate(), NewTskTime, tsk.getPlace(), tsk.getNote());
                        FinalTaskList.add(InsTsk);
                    }
                } else {
                    InstantTask TaskAfterLastValidInstantTask = FinalTaskList.get(IDLastValidInstantTask + 1);
                    if ((LastValidInstantTask.getTimeHour() + TravelTimeLastValidTask.hours + TaskTimeHours < tsk.getTimeToHour()) &&
                            (LastValidInstantTask.getTimeHour() + lastValidInstantTask.time.hours + TravelTimeTaskAfterLastValidTask.hours + TaskTimeHours <
                                    TaskAfterLastValidInstantTask.getTimeHour())) {
                        //checks whether we can do the duration task between the last valid instant task and the instant task after last valid instant task
                        Time NewTskTime = new Time(LastValidInstantTask.getTimeHour() + lastValidInstantTask.time.hours + TaskTimeHours, LastValidInstantTask.getTimeMinute(), LastValidInstantTask.getTimeSecond());
                        InstantTask InsTsk = new InstantTask(tsk.getDate(), NewTskTime, tsk.getPlace(), tsk.getNote());
                        FinalTaskList.add(InsTsk);
                    }
                }

            }
        }


        if(tsk.equals(DurationTaskList.get(DurationTaskList.size() - 1))){
            //if this was the last duration task, display the planned day in the next activity
            progressBar.cancel();
            Globals.FinalTasklist = (ArrayList<InstantTask>) FinalTaskList;
            Intent i = new Intent(this, PlannedDay.class);
            startActivity(i);

        }else {
            CreateIDdDistanceTimeList(DurationTaskList.get(DurationTaskList.indexOf(tsk)+1));
        }

    }




    private Time timeConversion(int totalSeconds) {
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return new Time (hours, minutes, seconds);
    }




    public class DbHandlerDurationAsyncTask extends AsyncTask<Context, Void, DatabaseHandlerDurationTask> {
        @Override
        public DatabaseHandlerDurationTask doInBackground(Context... contexts) {
            dbDu = new DatabaseHandlerDurationTask(contexts[0]);
            dbIn = new DatabaseHandlerInstantTask(contexts[0]);

            // Insert task
            dbDu.add(new DurationTask(date, TimeFrom, TimeTo, Place, Note));
            return dbDu;
        }

        @Override
        public void onPostExecute(DatabaseHandlerDurationTask db) {
            super.onPostExecute(db);
            //PlanTheBestRoute();

            //Get the list of distinct cities to choose from to plan the day
            GetCities();
        }
    }



    public void onDateButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        //Check which radio button was checked
        switch (view.getId()){
            case R.id.radio_today:
                if(checked)
                    //set today's date
                    date = today;
                break;
            case R.id.radio_otherday:
                if(checked)
                    //open DatePickerFragment
                    inputDate();
                break;

        }
    }


    public void inputDate(){
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "date_picker");
        datePickerFragment.setMainInstance(this);
    }




    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        private Main mainInstance;

        public void setMainInstance(Main mainInstance){
            this.mainInstance = mainInstance;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setScaleX(0.7F);
            datePickerDialog.getDatePicker().setScaleY(0.7F);

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){
            GregorianCalendar date = new GregorianCalendar(year, month, day);
            mainInstance.date = date;
        }

    }



    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was checked
        switch (view.getId()) {
            case R.id.radio_instant:
                if (checked)
                    //open TimePickerFragment for Time Instant
                    inputTimeInstant();
                break;
            case R.id.radio_duration:
                if (checked)
                    //open TimePickerFragment for Time Duration
                    inputTimeDuration();
                break;
        }
    }




    public void inputTimeInstant() {
        TimePickerFragment timefragment = new TimePickerFragment();
        timefragment.setMainInstance(this,1);
        isTimeInstant = true;
        timefragment.show(getSupportFragmentManager(), "time_picker");
    }



    public void inputTimeDuration(){
        isTimeInstant = false;
        toastFrom = Toast.makeText(this, "Enter task start time", Toast.LENGTH_LONG);
        toastTo = Toast.makeText(this, "Enter task end time", Toast.LENGTH_LONG);
        toastFrom.show();

        TimePickerFragment timefragmentFrom = new TimePickerFragment();
        timefragmentFrom.setMainInstance(this, 1);

        TimePickerFragment timefragmentTo = new TimePickerFragment();
        timefragmentTo.setMainInstance(this,2);

        timefragmentTo.show(getSupportFragmentManager(), "time_picker_To");
        timefragmentFrom.show(getSupportFragmentManager(),"time_picker_From");

    }



    public static class TimePickerFragment extends android.support.v4.app.DialogFragment implements TimePickerDialog.OnTimeSetListener{
        private Main mainInstance;
        private int Count;

        public void setMainInstance(Main mainInstance, int Count){
            this.mainInstance = mainInstance;
            this.Count = Count;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog =  new TimePickerDialog(mainInstance, this, hour, minute, DateFormat.is24HourFormat(mainInstance));
            return timePickerDialog;
        }



        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            if (mainInstance.isTimeInstant) {
                //set the time instant
                mainInstance.TimeInstant = new Time(hourOfDay, minutes, 0);
            } else {
                //set the time duration
                if (Count == 1) {
                    mainInstance.TimeFrom = new Time(hourOfDay, minutes, 0);
                    mainInstance.toastFrom.cancel();
                    mainInstance.toastTo.show();
                } else {
                    mainInstance.TimeTo = new Time(hourOfDay, minutes, 0);
                    mainInstance.toastTo.cancel();
                }
            }
        }

    }




    public class IDdDistanceTime{
        public int ID;
        public int distance;
        public Time time;

        IDdDistanceTime(int ID, int distance, Time time){
            this.ID = ID;
            this.distance = distance;
            this.time = time;
        }
    }


}