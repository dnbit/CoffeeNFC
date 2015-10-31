package com.dnbitstudio.coffeenfc;

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

/**
 * Created by jose on 31/10/15.
 */
public class ParseHelper
{
    private static final String TAG = MainActivity.class.getName();

    protected static void loginParse()
    {
        ParseAnonymousUtils.logIn(new LogInCallback()
        {
            @Override
            public void done(ParseUser user, ParseException e)
            {
                if (e != null)
                {
                    Log.d("MyApp", "Anonymous login failed.");
                } else
                {
                    Log.d("MyApp", "Anonymous user logged in.");
                }
            }
        });
    }

    protected static void countPoint(final String userId)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Points");
        query.whereEqualTo("userId", userId);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            public void done(List<ParseObject> countPoints, ParseException e)
            {
                if (e == null)
                {
                    Log.d("score", "Your number of point is: " + countPoints.size());
                } else
                {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    protected static void registerPoint(final String idUser)
    {
        ParseObject points = new ParseObject("Points");
        points.put("userId", idUser);
        points.saveInBackground(new SaveCallback()
        {
            public void done(ParseException e)
            {
                if (e == null)
                {
                    // Saved successfully.
                    Log.d(TAG, "Message saved!");
                    countPoint(idUser);
                } else
                {
                    // The save failed.
                    Log.d(TAG, "Message error: " + e);
                }
            }
        });
    }

}
