package com.dnbitstudio.coffeenfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;

    private String lastParsedNfcTag;

    private static final String TAG = MainActivity.class.getName();

    @Bind(R.id.tv_main) TextView mainText;
    @Bind(R.id.btn_redeem) TextView btnRedeem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        ButterKnife.bind(this);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Log.i("****TAG", "onCreate MainActivity");
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        lastParsedNfcTag = NfcMessageParser.parseNfcIntent(getIntent());

        loginParse();
        if (lastParsedNfcTag != null) {
            mainText.setText(lastParsedNfcTag);
            registerPoint(lastParsedNfcTag);
        } else {
            mainText.setText("waiting for nfc scan");
        }

        btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Redeeme pressed", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        lastParsedNfcTag = NfcMessageParser.parseNfcIntent(intent);
        if (lastParsedNfcTag != null) {
            mainText.setText(lastParsedNfcTag);
            registerPoint(lastParsedNfcTag);
        }
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