package edu.wit.mobileapp.mailshere;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.HashSet;


public class Note extends AppCompatActivity {

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
            notes= new ArrayList<>(set);
        }


        listTypeface = Typeface.createFromAsset(getAssets(), "fonts/AnticSlab-Regular.ttf");

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,notes){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Cast the list view each item as text view
                TextView item = (TextView) super.getView(position, convertView, parent);

                // Set the typeface/font for the current item
                item.setTypeface(listTypeface);

                // Set the item text style to bold
                item.setTypeface(item.getTypeface(), Typeface.BOLD);

                // Change the item text size
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                // return the view
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

                //setting a vraible so there is no conficlt with the int i in the onclick method below
                final int deleteItem = i;
                //creates a dilog box wwhen the user does a longpress wo bring up a mini alert asking them if they are sure if they
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


}
