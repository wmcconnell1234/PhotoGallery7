package com.example.photogallery2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Search extends AppCompatActivity {
    private EditText fromDate;
    private EditText toDate;
    //private int yesSearchTime=0;
    private EditText Captions;
    private EditText fromLatitude;
    private EditText toLatitude;
    private EditText fromLongitude;
    private EditText toLongitude;
    private Calendar fromCalendar;
    private Calendar toCalendar;
    private DatePickerDialog.OnDateSetListener fromListener;
    private DatePickerDialog.OnDateSetListener toListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //TextView textViewforsearchCaption = findViewById(R.id.search_by_caption);
        //TextView textViewforsearchDate = findViewById(R.id.search_by_date);
        //TextView textViewforsearchLocation = findViewById(R.id.search_by_location);
        fromDate = (EditText) findViewById(R.id.search_fromDate);
        toDate   = (EditText) findViewById(R.id.search_toDate);

        fromLatitude = (EditText) findViewById(R.id.search_fromLatitude);
        toLatitude = (EditText) findViewById(R.id.search_toLatitude);
        fromLongitude = (EditText) findViewById(R.id.search_fromLongitude);
        toLongitude = (EditText) findViewById(R.id.search_toLongitude);

    }
    public void cancel(final View v) {
        finish();
    }
    public void searchCaption(final View v) {
        Intent i = new Intent();
        Captions = (EditText) findViewById(R.id.search_Captions);
        String mCaption = Captions.getText().toString();
        i.putExtra("CAPTION",mCaption);

        i.putExtra("STARTDATE", fromDate.getText().toString());
        i.putExtra("ENDDATE", toDate.getText().toString());

        setResult(RESULT_OK, i);
        finish();
    }


    public void searchTime(final View v) {
        Intent i = new Intent();
        i.putExtra("STARTDATE", fromDate.getText().toString());
        i.putExtra("ENDDATE", toDate.getText().toString());
       // yesSearchTime = 1;
       // i.putExtra("wantToSearchTimeInstead",);
        setResult(RESULT_OK, i);
        finish();
    }

    public void searchLocation(final View v) {

    }


}

