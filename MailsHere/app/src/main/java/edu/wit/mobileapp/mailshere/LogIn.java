package edu.wit.mobileapp.mailshere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LogIn extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        error = (TextView)findViewById(R.id.error);

        Button open = (Button)findViewById(R.id.login_btn);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().equals("Username") && password.getText().toString().equals("Password")){
                    Intent intent = new Intent().setClass(LogIn.this, Home.class);
                    startActivity(intent);
                }
                else {
                    error.setVisibility(View.VISIBLE);
                    error.setError("INCORRECT");
                }

            }
        });
    }
}
