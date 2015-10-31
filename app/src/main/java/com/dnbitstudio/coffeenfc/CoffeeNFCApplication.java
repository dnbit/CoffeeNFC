package com.dnbitstudio.coffeenfc;

import android.app.Application;

import com.parse.Parse;

public class CoffeeNFCApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
// Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "E2Q8Tdvl7ZK61Joh3WsFNVny5xYyFamRgFPcl87d", "E28x2GDViCwYxYYSWtXD4E9klp0MTVMGpqkadHrr");

    }
}
