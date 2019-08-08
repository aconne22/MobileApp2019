package edu.wit.mobileapp.mailshere;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xmlpull.v1.sax2.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Home extends AppCompatActivity {

        private static final String TAG = "MyApp";

        private CalendarView mCalendarView;
        private TextView mDate;
        private TextView mTime;
        private TextView mNote;
        private Button note_btn;
        private Button view_btn;

        private Context mContext;
        private ScrollView mScrollView;

        Map<Date, Time> dateTimeMap = new LinkedHashMap<Date, Time>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            mContext = getApplicationContext();
            mScrollView = (ScrollView) findViewById(R.id.contents);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.date_content, null);
            mScrollView.addView(customView);
            Log.v(TAG, "View inflated");

            mTime = (TextView)customView.findViewById(R.id.time_display);
            mNote = (TextView)customView.findViewById(R.id.note_display);
            mDate = (TextView)findViewById(R.id.date_display);

            note_btn = (Button)findViewById(R.id.note_btn);
            view_btn = (Button) findViewById(R.id.view_btn);

            note_btn.setVisibility(View.GONE);

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
                    int mNew = mm+1;
                    String thisDate;
                    if (mNew < 10 && dd < 10) {
                        thisDate = yyyy + "-0" + (mNew) + "-0" + dd;
                    }
                    else if (mNew < 10) {
                        thisDate = yyyy + "-0" + (mNew) + "-" + dd;
                    }
                    else if (dd < 10) {
                        thisDate = yyyy + "-" + (mNew) + "-0" + dd;
                    }
                    else {
                        thisDate = yyyy + "-" + (mNew) + "-" + dd;
                    }
                    Log.v(TAG, "The date selected is: " + thisDate);
                    mTime.setText(getString(R.string.time));
                    mNote.setText(getString(R.string.note));
                    mDate.setText(thisDate);

                    //Database connection
                    GetCalendarData retrieveData = new GetCalendarData();
                    retrieveData.execute("");
                    note_btn.setVisibility(View.VISIBLE);
                }
            });
        }

        private class GetCalendarData extends AsyncTask<String,String,String> {

            //setting up class variables
            String msg = "";
            final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

            //making full DB_URL string
            final String DB_URL = "jdbc:mysql://" +
                    DBStrings.DATABASE_URL + "/" +
                    DBStrings.DATABASE_NAME;


            @Override
            protected String doInBackground(String... strings) {
                Connection connect = null;
                Statement state = null;
                Log.v(TAG, "DB_URL: " + DB_URL);

                try {
                    //initialize JDBC driver for connecting to DB
                    Class.forName(JDBC_DRIVER).newInstance();

                    //attempt connection
                    connect = DriverManager.getConnection(DB_URL, DBStrings.USERNAME, DBStrings.PASSWORD);
                    state = connect.createStatement();
                    String sql = "SELECT * FROM Mail";

                    //run query
                    ResultSet results = state.executeQuery(sql);

                    while(results.next()){
                        //parse the results of the query
                        Date dateEntry = results.getDate("Date");
                        Time timeEntry = results.getTime("Time");
                        if (mDate.getText().toString().equals(dateEntry.toString())) {
                            mTime.append("\n      " + timeEntry);
                        }
                        Log.v(TAG, "Date: " + dateEntry + " Time: " + timeEntry);
                    }

                    //now we do the Notes in a nearly identical fashion
                    String sql2 = "SELECT Content FROM Note";
                    ResultSet results2 = state.executeQuery(sql2);

                    while(results2.next()){
                        String noteEntry = results2.getString("Content");
                        mNote.append("\n      " + noteEntry);
                        Log.v(TAG, "Note: " + noteEntry);
                    }

                    msg = "Complete!";

                    results2.close();
                    state.close();
                    connect.close();

                } catch (SQLException connError) {
                    msg = "Exception thrown for JDBC.";
                    connError.printStackTrace();

                } catch (ClassNotFoundException e) {
                    msg = "Exception thrown; Class Not Found";
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } finally {

                   try {

                       if(state != null){
                            state.close();
                       }

                   } catch (SQLException e) {
                       msg = "Exception when closing state";
                       e.printStackTrace();
                   }

                   try {

                       if(connect != null){
                           connect.close();
                       }

                   } catch (SQLException e) {
                       msg = "Exception when closing connect";
                       e.printStackTrace();
                   }
                }
                return null;
            }


            @Override
            protected void onPreExecute(){
                Log.v(TAG, "PreExecute");
            }

            @Override
            protected void onPostExecute(String msg){

                Log.v(TAG, "PostExecute");
            }


        }
    }