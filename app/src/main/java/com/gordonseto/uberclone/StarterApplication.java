package com.gordonseto.uberclone;

import android.app.Application;

/**
 * Created by gordonseto on 16-08-16.
 */
public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseInitializer initializer = new ParseInitializer();
        initializer.initalizeParse(this);
    }
}
