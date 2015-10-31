package com.dnbitstudio.coffeenfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity
{
    private NfcAdapter mNfcAdapter;

    private static final String TAG = MainActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Log.i("****TAG", "onCreate MainActivity");
        if (mNfcAdapter == null)
        {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        handleIntent(getIntent());
        String idUser = "idUser";
        loginParse();
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

    private void registerPoint(String idUser) {
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
                } else
                {
                    // The save failed.
                    Log.d(TAG, "Message error: " + e);
                }
            }
        });
    }

    private void handleIntent(Intent intent)
    {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            NdefMessage[] msgs;
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null)
            {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++)
                {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    NdefRecord[] ndefRecords = msgs[i].getRecords();
                    for (NdefRecord record : ndefRecords)
                    {
//                        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
//                        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));
                        try
                        {
                            byte[] payload = record.getPayload();
            /*
             * payload[0] contains the "Status Byte Encodings" field, per the
             * NFC Forum "Text Record Type Definition" section 3.2.1.
             *
             * bit7 is the Text Encoding Field.
             *
             * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
             * The text is encoded in UTF16
             *
             * Bit_6 is reserved for future use and must be set to zero.
             *
             * Bits 5 to 0 are the length of the IANA language code.
             */
                            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                            int languageCodeLength = payload[0] & 0077;
                            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                            String idUser =
                                    new String(payload, languageCodeLength + 1,
                                            payload.length - languageCodeLength - 1, textEncoding);
                            registerPoint(idUser);
                            Log.i("****TAG", "RECORD " + idUser);
                        } catch (UnsupportedEncodingException e)
                        {
                            // should never happen unless we get a malformed tag.
                            throw new IllegalArgumentException(e);
                        }
                    }
                }
            }
        }
    }
}