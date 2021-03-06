/*
*EditHabitActivity
*
* version 1.0
*
* Dec 3, 2017
*
*Copyright (c) 2017 Team 16 (Jonah Cowan, Alexander Mackenzie, Hao Yuan, Jacy Mark, Shu-Ting Lin), CMPUT301, University of Alberta - All Rights Reserved.
*You may use, distribute, or modify this code under terms and conditions of the Code of Student Behavior at University of Alberta.
*You can find a copy of the license in this project. Otherwise please contact contact@abc.ca.
*
*/

package com.example.habittracker2017;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.example.habittracker2017.UserManager.user;


/**
 * Activity for edit details of habit and saves changes
 *
 * @author team 16
 * @version 1.0
 * @see viewManageHabits
 * @since 1.0
 */
public class EditHabitActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText habitName;
    private TextView habitStart;
    private EditText reason;
    private Button datePicker;
    private Button SaveButton;
    private Button CreateEventButton;
    private Button DeleteButton;
    private Button ViewStatsButton;
    private int day;
    private int month;
    private int year;

    private CheckBox monday;
    private CheckBox tuesday;
    private CheckBox wednesday;
    private CheckBox thursday;
    private CheckBox friday;
    private CheckBox saturday;
    private CheckBox sunday;
    HashMap<Integer, Boolean> habitHash = new HashMap<Integer, Boolean>();
    private int position;
    private Habit habit;

    /**
     * Called when activity created
     * Set instance variables
     * Set button onClick
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit);
        position = this.getIntent().getIntExtra("Habit",0);
        habit = viewManageHabits.allHabits.get(position);
        datePicker = (Button) findViewById(R.id.timePicker);
        datePicker.setOnClickListener(this);

        Date startDateString = habit.getStartDate();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String newDateString = df.format(startDateString);
        datePicker.setText(newDateString);

        HashMap<Integer, Boolean> schedule = habit.getSchedule();
        habitName = (EditText) findViewById(R.id.habitName);
        habitName.setText(habit.getTitle());
        reason = (EditText) findViewById(R.id.reason);
        reason.setText(habit.getReason());
        SaveButton = (Button) findViewById(R.id.button_save);
        SaveButton.setOnClickListener(this);
        CreateEventButton = (Button) findViewById(R.id.button_create_event);

        ViewStatsButton = (Button) findViewById(R.id.view_Stats);

        DeleteButton = (Button) findViewById(R.id.button_delete);
        monday = (CheckBox) findViewById(R.id.monday);
        tuesday = (CheckBox) findViewById(R.id.tuesday);
        wednesday = (CheckBox) findViewById(R.id.wednesday);
        thursday = (CheckBox) findViewById(R.id.thursday);
        friday = (CheckBox) findViewById(R.id.friday);
        saturday = (CheckBox) findViewById(R.id.saturday);
        sunday = (CheckBox) findViewById(R.id.sunday);
        sunday.setChecked(schedule.get(1));
        monday.setChecked(schedule.get(2));
        tuesday.setChecked(schedule.get(3));
        wednesday.setChecked(schedule.get(4));
        thursday.setChecked(schedule.get(5));
        friday.setChecked(schedule.get(6));
        saturday.setChecked(schedule.get(7));

        ViewStatsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getBaseContext(),StatView.class);
                intent.putExtra("Habit",position);
                getBaseContext().startActivity(intent);
            }
        });

        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.deleteHabit(habit);
                UserManager.save();
                finish();
            }
        });

        CreateEventButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (habit.getStartDate().before(new Date())) {
                    if (habit.isDue()) {

                        HabitEvent checkNull = habit.getLastEvent();
                        if (checkNull != null) {
                            if (DateUtils.isToday(checkNull.getDate().getTime())){
                                Toast.makeText(getBaseContext(), "You have already done this habit today!", Toast.LENGTH_LONG).show();
                            } else{
                                position = viewTodayFragment.allHabits.indexOf(habit);
                                Intent intent = new Intent(getBaseContext(), CreateEventActivity.class);
                                intent.putExtra("Habit", position);
                                getBaseContext().startActivity(intent);
                            }
                        }else {
                            position = viewTodayFragment.allHabits.indexOf(habit);
                            Intent intent = new Intent(getBaseContext(), CreateEventActivity.class);
                            intent.putExtra("Habit", position);
                            getBaseContext().startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Habit not due today!", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Habit not started yet!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Set date checked, put into habitHash
     */
    public void setHabitHash() {
        this.habitHash.put(1, sunday.isChecked());
        this.habitHash.put(2, monday.isChecked());
        this.habitHash.put(3, tuesday.isChecked());
        this.habitHash.put(4, wednesday.isChecked());
        this.habitHash.put(5, thursday.isChecked());
        this.habitHash.put(6, friday.isChecked());
        this.habitHash.put(7, saturday.isChecked());
        this.habitHash.put(1, sunday.isChecked());
    }

    /**
     * Do corresponding tasks when certain button pressed
     * Set date
     * Create habit from parameters. If required field is not given, then habit not created
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v == datePicker){
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    datePicker.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                }
            }, year, month, day);
            datePickerDialog.show();
        }

        /*if (v==CreateEventButton) {
            Intent intent = new Intent(this,CreateEventActivity.class);
            intent.putExtra("Habit",position);
            this.startActivity(intent);
        }*/

//        if (v==DeleteButton){
//            viewManageHabits.allHabits.remove(habit);
//            viewManageHabits.adapter.notifyDataSetChanged();
//            saveToFile();
//            finish();
//        }

        if (v == SaveButton) {
            setResult(RESULT_OK);
            String habitTitleName = habitName.getText().toString();
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String habitStartString = datePicker.getText().toString();
            setHabitHash();
            Boolean CorrectStartDate = true;
            Date habitStartDate = null;
            try {
                habitStartDate = format.parse(habitStartString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (HabitEvent event:habit.getEvents()){
                if(event.getDate().before(habitStartDate)){
                    CorrectStartDate = false;
                    break;
                }
            }
            String habitReason = reason.getText().toString();

            /**
             * Try to create habit from parameters
             */
            try {
                //createHabitManager.create(habitTitleName, habitStartDate, habitReason, habitHash,user.getName());
                if (habitTitleName.equals("")){
                    Toast.makeText(getBaseContext(), "Please enter a proper title! ", Toast.LENGTH_SHORT).show();
                }else if (habitStartDate == null){
                    Toast.makeText(getBaseContext(), "Please select a start date! ", Toast.LENGTH_SHORT).show();
                }else if(!CorrectStartDate){
                    Toast.makeText(getApplicationContext(), "There is a habit event before the new start date! Select Again!", Toast.LENGTH_LONG).show();
                }
                else {
                    habit.setTitle(habitTitleName);
                    habit.setStartDate(habitStartDate);
                    habit.setReason(habitReason);
                    habit.setSchedule(habitHash);
                    viewManageHabits.adapter.notifyDataSetChanged();
                    UserManager.save();
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

