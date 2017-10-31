package com.example.hemangini.planyourday;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import made.by.hemangini.planyourday.InstantTask;
import made.by.hemangini.planyourday.Time;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


        private List<InstantTask> InstantTaskList = new ArrayList<InstantTask>();

        public void Method() {

            GregorianCalendar date = new GregorianCalendar();
            Time time = new Time(12,34,23);
            Time time2 = new Time(14,34,34);
            Time time3 = new Time(10,34,55);
            InstantTask task1 = new InstantTask(date, time, "Victoria BC", "task1");
            InstantTask task2 = new InstantTask(date, time2, "Seattle", "task2");
            InstantTask task3 = new InstantTask(date, time3, "Vancouver BC", "task3");
            InstantTaskList.add(task1);
            InstantTaskList.add(task2);
            InstantTaskList.add(task3);

            //Sort the instant task list based on time
            for (InstantTask tsk : InstantTaskList) {
                String taskprint = String.valueOf(tsk.getDate()) + String.valueOf(tsk.getTimeHour()) + String.valueOf(tsk.getTimeMinute())
                        + String.valueOf(tsk.getTimeSecond()) + tsk.getPlace() + tsk.getNote();
                Log.d("before", taskprint);
            }
            Collections.sort(InstantTaskList, new InstantTaskComparator());

            for (InstantTask tsk : InstantTaskList) {
                String taskprint = String.valueOf(tsk.getDate()) + String.valueOf(tsk.getTimeHour()) + String.valueOf(tsk.getTimeMinute())
                        + String.valueOf(tsk.getTimeSecond()) + tsk.getPlace() + tsk.getNote();
                Log.d("After", taskprint);
            }

        }





        public class InstantTaskComparator implements Comparator<InstantTask> {
            //method to compare class instances

            public int compare(InstantTask one, InstantTask other) {
                return one.getTimeHour() - other.getTimeHour();
            }

        }

    }

