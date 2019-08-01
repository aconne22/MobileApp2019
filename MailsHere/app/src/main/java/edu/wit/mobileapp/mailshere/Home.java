package edu.wit.mobileapp.mailshere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Home extends AppCompatActivity {

        private static final String TAG = "MyApp";

        private CalendarView mCalendarView;
        private TextView mDate;
        private TextView mTime;
        private TextView mNote;
        private Button note_btn;
        private Button view_btn;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            mDate = (TextView)findViewById(R.id.date_display);
            mTime = (TextView)findViewById(R.id.time_display);
            mNote = (TextView)findViewById(R.id.note_display);
            note_btn = (Button)findViewById(R.id.note_btn);
            view_btn = (Button) findViewById(R.id.view_btn);

            mTime.setVisibility(View.INVISIBLE);
            mNote.setVisibility(View.INVISIBLE);
            note_btn.setVisibility(View.INVISIBLE);

            note_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), EditNote.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Date", mDate.toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            view_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Note.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Date", mDate.toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            mCalendarView = (CalendarView)findViewById(R.id.my_calendar);
            mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int yyyy, int mm, int dd) {
                    String thisDate = (mm + 1) + "/" + dd + "/" + yyyy;
                    Log.v(TAG, "The date selected is: " + thisDate);
                    mDate.setText(thisDate);
                    mTime.setVisibility(View.VISIBLE);
                    mNote.setVisibility(View.VISIBLE);
                    note_btn.setVisibility(View.VISIBLE);
                }
            });
        }
    }