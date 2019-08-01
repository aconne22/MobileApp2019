package edu.wit.mobileapp.mailshere;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashSet;

import edu.wit.mobileapp.mailshere.Note;
import edu.wit.mobileapp.mailshere.R;

public class EditNote extends AppCompatActivity {

    private Button add_btn;

    int noteId;

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_note_menu,menu);

        return super.onCreateOptionsMenu(menu);


    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Bundle bundle = this.getIntent().getExtras();
        String date = bundle.getString("Date");

        add_btn = (Button)findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EditText editText = (EditText)findViewById(R.id.editText);

        //
        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId",-1);

        if(noteId !=-1){
            editText.setText(Note.notes.get(noteId));
        }
        else {
            Note.notes.add("");
            noteId=Note.notes.size()-1;
            Note.arrayAdapter.notifyDataSetChanged();
        }

        //

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Note.notes.set(noteId,String.valueOf(charSequence));
                Note.arrayAdapter.notifyDataSetChanged();

                SharedPreferences sharedPreferences = getApplication().getSharedPreferences("edu.wit.mobileapp.finalproject_notes", Context.MODE_PRIVATE);
                HashSet<String> set = new HashSet<>(Note.notes);

                sharedPreferences.edit().putStringSet("set",set).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}
