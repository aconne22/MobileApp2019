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
        private Button note_btn;
        private Button view_btn;

        private Context mContext;
        private ConstraintLayout mConstraintLayout;
        private PopupWindow mPopupWindow;

        Map<Date, Time> dateTimeMap = new LinkedHashMap<Date, Time>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            GetData retrieveData = new GetData();
            retrieveData.execute("");
            Log.v(TAG, "retrieveData executed");



            mContext = getApplicationContext();
            mConstraintLayout = (ConstraintLayout) findViewById(R.id.home_page);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.date_content, null);
            Log.v(TAG, "View inflated");
            mPopupWindow = new PopupWindow(
                    customView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            Log.v(TAG, "Popup Window created");

            mPopupWindow.setElevation(5.0f);
            Log.v(TAG, "Elevation set");

            final TextView mTime = (TextView)customView.findViewById(R.id.time_display);
            final TextView mNote = (TextView)customView.findViewById(R.id.note_display);


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
                    String thisDate = (mm + 1) + "/" + dd + "/" + yyyy;
                    mTime.setText(getString(R.string.time));
                    mNote.setText(getString(R.string.note));
                    mPopupWindow.showAtLocation(mConstraintLayout, Gravity.BOTTOM, -30, 285);
                    Log.v(TAG, "The date selected is: " + thisDate);
                    mDate.setText(thisDate);
                    mTime.append(" "+ thisDate);
                    mNote.append(" "+ thisDate);
                    note_btn.setVisibility(View.VISIBLE);
                }
            });
        }

        private class GetData extends AsyncTask<String,String,String> {

            String msg = "";
            final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
            final String DB_URL = "jdbc:mysql://" +
                    DBStrings.DATABASE_URL + "/" +
                    DBStrings.DATABASE_NAME;

            @Override
            protected String doInBackground(String... strings) {
                Connection connect = null;
                Statement state = null;

                try {
                    Class.forName(JDBC_DRIVER);
                    connect = DriverManager.getConnection(DB_URL, DBStrings.USERNAME, DBStrings.PASSWORD);
                    state = connect.createStatement();
                    String sql = "SELECT * FROM Mail";
                    ResultSet results = state.executeQuery(sql);

                    while(results.next()){
                        Date dateEntry = results.getDate("Date");
                        Time timeEntry = results.getTime("Time");
                        dateTimeMap.put(dateEntry, timeEntry);
                    }

                    msg = "Complete!";

                    results.close();
                    state.close();
                    connect.close();

                } catch (SQLException connError) {
                    msg = "Exception thrown for JDBC.";
                    connError.printStackTrace();

                } catch (ClassNotFoundException e) {
                    msg = "Exception thrown; Class Not Found";
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

            }

            @Override
            protected void onPostExecute(String msg){

                Set keys = dateTimeMap.keySet();

                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                    String key = (String) i.next().toString();
                    String value = (String) dateTimeMap.get(key).toString();
                    Log.v(TAG, "Key " + key + " Value: " + value + ".");
                }
            }


        }
    }