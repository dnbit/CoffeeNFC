package com.dnbitstudio.coffeenfc;

import android.app.Application;

import com.parse.Parse;

public class CoffeeNFCApplication extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, Key.applicationId, Key.clientID);
    }
}
