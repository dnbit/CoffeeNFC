package com.dnbitstudio.coffeenfc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String userId = "yuuu";
        loginParse();
        //registerPoint(userId);
        countPoint(userId);
    }

    private void countPoint(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Points");
        query.whereEqualTo("userId", userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> countPoints, ParseException e) {
                if (e == null) {
                    Log.d("score", "Your number of point is: " + countPoints.size());

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void loginParse() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.d("MyApp", "Anonymous login failed.");
                } else {
                    Log.d("MyApp", "Anonymous user logged in.");
                }
            }
        });

        /*ParseUser user = new ParseUser();
        user.setUsername("my name");
        user.setPassword("my pass");
        user.setEmail("email@example.com");

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });*/
    }

    private void registerPoint(String idUser) {
        ParseObject points = new ParseObject("Points");
        points.put("userId", idUser);
        points.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                    Log.d(TAG, "Message saved!");
                } else {
                    // The save failed.
                    Log.d(TAG, "Message error: " + e);
                }
            }
        });
    }
}
