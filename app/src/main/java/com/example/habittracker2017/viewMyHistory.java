package com.example.habittracker2017;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jlin7 on 2017/11/12.
 */

public class viewMyHistory extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_history);

        ArrayList<HabitEvent> eventArray= new ArrayList<HabitEvent>();
        HistoryAdapter adapter = new HistoryAdapter(this, eventArray);
        ListView listView=(ListView) findViewById(R.id.myHistory_list);
        listView.setAdapter(adapter);

        /*doesn't show habit event, need to fix*/
        Habit habit = new Habit("Title", "Reason", new Date(), null);
        String comment = "Test comment";

        Location location = null;

        /*Bitmap picture = Bitmap.createBitmap(64, 64, Bitmap.Config.RGBA_F16);*/   //Or whatever format we end up using
        Bitmap picture =null;
        HabitEvent eventL = new HabitEvent(comment, location);
        HabitEvent eventP = new HabitEvent(comment, picture);
        HabitEvent eventPL = new HabitEvent(comment, picture, location);
        habit.addEvent(eventL);
        habit.addEvent(eventP);
        habit.addEvent(eventPL);

        adapter.add(eventL);
        adapter.add(eventP);
        adapter.add(eventPL);

        adapter.notifyDataSetChanged();

    }

    /*fix: when click on map, app stops*/

    public void openMap(View view) {
        Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }
}
