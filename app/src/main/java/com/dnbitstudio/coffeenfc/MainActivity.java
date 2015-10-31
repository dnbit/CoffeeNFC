package com.dnbitstudio.coffeenfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ParseHelper.loginParse();
        final Intent intent = getIntent();
        handleNfcIntent(intent);

        btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Redeem pressed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNfcIntent(intent);
    }

    private void handleNfcIntent(Intent intent) {
        lastParsedNfcTag = NfcMessageParser.parseNfcIntent(intent);
        if (lastParsedNfcTag != null) {
            mainText.setText(lastParsedNfcTag);
            ParseHelper.registerPoint(lastParsedNfcTag, new ParseHelper.OnQueryComplete<String>() {
                @Override
                public void onSuccess(String result) {
                    ParseHelper.countPoint(result, new ParseHelper.OnQueryComplete<Integer>() {
                        @Override
                        public void onSuccess(final Integer result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Count: " + result, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

}