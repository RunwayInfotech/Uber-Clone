package com.gordonseto.uberclone;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends AppCompatActivity {

    Switch riderOrDriverSwitch;
    Boolean isDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        riderOrDriverSwitch = (Switch)findViewById(R.id.riderOrDriverSwitch);

        try {
            ParseUser.getCurrentUser().fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (ParseUser.getCurrentUser() == null) {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e != null) {
                        Log.i("MYAPP", "Error logging in");
                    } else {
                        Log.i("MYAPP", "Logged in");
                    }
                }
            });
        } else {
            if (ParseUser.getCurrentUser().get("isDriver") != null){
                redirectUser();
            }
        }

    }

    public void onGetStartedPressed(View view){
        isDriver = riderOrDriverSwitch.isChecked();

        ParseUser.getCurrentUser().put("isDriver", isDriver);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("MYAPP", "successful isDriver update");
                    redirectUser();
                } else {
                    Log.i("MYAPP", "unsuccessful isDriver update");
                    e.printStackTrace();
                }
            }
        });
    }

    public void redirectUser(){
        if (ParseUser.getCurrentUser().getBoolean("isDriver")){
            Intent intent = new Intent(getApplicationContext(), ViewRequests.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), YourLocation.class);
            startActivity(intent);
        }
    }
}
