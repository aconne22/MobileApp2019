package edu.wit.mobileapp.mailshere;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;


public class Note extends AppCompatActivity {

    private static final String TAG = "MyApp";

    //Used to set font for ListView items
    private Typeface listTypeface;

    //Buttons to end activity/add note respectively
    private Button back_btn;
    private Button add_btn;

    //Array list and adapter to keep track of the list of notes
    static ArrayList<String> notes = new ArrayList<>();
    static  ArrayAdapter arrayAdapter;

    //random number
    private static final int notification_ID = 25000;

    //creating the menu for the notes page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_note_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }
    //if add not is selected on the menu in the right hand corner the user is taken to the editacticty page to create a note
    //technically they are just editting an empty note
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.add_note){
            Intent intent = new Intent(getApplicationContext(), EditNote.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        GetNoteData retrieveData = new GetNoteData();
        retrieveData.execute("");

        Bundle bundle = this.getIntent().getExtras();
        final String date = bundle.getString("Date");

        ListView listView = (ListView)findViewById(R.id.listView);

        back_btn = (Button)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add_btn = (Button)findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditNote.class);
                Bundle bundle = new Bundle();
                bundle.putString("Date", date.toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("edu.wit.mobileapp.mailshere", Context.MODE_PRIVATE);
        //Getting the Stringset and if null(set empty) will add "click here to edit first note!, or else will display
        //the set
        HashSet<String>set=(HashSet)sharedPreferences.getStringSet("notes",null);
        if(set==null){

        }
        else {
            notes = new ArrayList<>(set);
        }


        listTypeface = Typeface.createFromAsset(getAssets(), "fonts/AnticSlab-Regular.ttf");

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,notes){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView item = (TextView) super.getView(position, convertView, parent);
                item.setTypeface(listTypeface);
                item.setTypeface(item.getTypeface(), Typeface.BOLD);
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                return item;
            }
        };

        listView.setAdapter(arrayAdapter);


        //will take the user to the editActivity page if they click on a note
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),EditNote.class);
                intent.putExtra("noteId",i);
                startActivity(intent);

            }
        });



        //onlongclick listener for the user to bring up the option to delete after pressing a note for more than a couple seconds
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                //setting a variable so there is no conflict with the int i in the onclick method below
                final int deleteItem = i;
                //creates a dialog box when the user does a longpress we bring up a mini alert asking them if they are sure if they
                //want to delete this note.
                new AlertDialog.Builder(Note.this)
                        .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Are you sure?")
                        .setMessage("Do you want to delete this note?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //removing the not if the user says yes
                        notes.remove(deleteItem);
                        arrayAdapter.notifyDataSetChanged();

                        //shared preferences that allow the user to save the notes locally
                        // having issues with this although for when the app is turned off
                        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("edu.wit.mobileapp.finalproject_notes", Context.MODE_PRIVATE);
                        HashSet<String> set = new HashSet<>(Note.notes);

                        sharedPreferences.edit().putStringSet("set",set).apply();



                    }
                })
                        //setting negative button to say no and if they say no nothing will change
                        .setNegativeButton("no",null)
                        .show();
                return true;
            }
        });
    }

    private class GetNoteData extends AsyncTask<String,String,String> {

        String msg = "";
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://" +
                DBStrings.DATABASE_URL + "/" +
                DBStrings.DATABASE_NAME;


        @Override
        protected String doInBackground(String... strings) {
            Connection connect = null;
            Statement state = null;
            Log.v(TAG, "DB_URL: " + DB_URL);

            try {
                Class.forName(JDBC_DRIVER).newInstance();
                connect = DriverManager.getConnection(DB_URL, DBStrings.USERNAME, DBStrings.PASSWORD);
                //connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/motionsensor", "mobileapp", "mobileapp");
                state = connect.createStatement();
                String sql = "SELECT Content FROM Note";
                ResultSet results = state.executeQuery(sql);
                Log.v(TAG, "This tag is after connection");

                while(results.next()){
                    String noteEntry = results.getString("Content");
                    Log.v(TAG, "Note: " + noteEntry);
                    Log.v(TAG, "In the resultsSet");
                    notes.add(noteEntry);


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
