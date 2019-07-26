package edu.wit.mobileapp.mailshere;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

public class Home extends AppCompatActivity {

        private static final String TAG = "MyApp";

        private CalendarView mCalendarView;
        private TextView mDate;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            mDate = (TextView)findViewById(R.id.date_display);
            mCalendarView = (CalendarView)findViewById(R.id.my_calendar);
            mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int yyyy, int mm, int dd) {
                    String thisDate = (mm + 1) + "/" + dd + "/" + yyyy;
                    Log.v(TAG, "The date selected is: " + thisDate);
                    mDate.setText(thisDate);
                }
            });
        }
    }