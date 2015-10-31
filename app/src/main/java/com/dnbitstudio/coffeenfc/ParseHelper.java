package com.dnbitstudio.coffeenfc;

import android.support.annotation.NonNull;
import android.util.Log;

import com.parse.CountCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by jose on 31/10/15.
 */
public class ParseHelper {
    private static final String TAG = MainActivity.class.getName();

    public static void loginParse() {
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
    }

    public static void countPoint(final String userId, @NonNull final OnQueryComplete<Integer> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Points");
        query.whereEqualTo("userId", userId);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
                    callback.onSuccess(count);
                } else {
                    Log.d(TAG, "Message error: " + e);
                }
            }
        });
    }

    public static void registerPoint(final String idUser, @NonNull final OnQueryComplete<String> callback) {
        ParseObject points = new ParseObject("Points");
        points.put("userId", idUser);
        points.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                    Log.d(TAG, "Message saved!");
                    callback.onSuccess(idUser);
                } else {
                    // The save failed.
                    Log.d(TAG, "Message error: " + e);
                }
            }
        });
    }

    public interface OnQueryComplete<T> {
        void onSuccess(T result);
        // void onError(); // not yet :p
    }


}
