package com.gordonseto.uberclone;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by gordonseto on 16-08-16.
 */
public class ParseInitializer {
    public ParseInitializer() {
    }

    public void initalizeParse(Context context){
        Parse.enableLocalDatastore(context);

        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId("uber999")
                .server("http://uber999.herokuapp.com/parse/")
                .build()
        );

        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
