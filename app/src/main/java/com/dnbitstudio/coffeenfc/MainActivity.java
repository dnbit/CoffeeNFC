package com.dnbitstudio.coffeenfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;

    private String lastParsedNfcTag;

    private static final String TAG = MainActivity.class.getName();
    public static final int REDEEM_THRESHOLD = 4;
    public ParseHelper parseHelper;

    @Bind(R.id.tv_customer)
    TextView mCustomerText;
    @Bind(R.id.tv_stamps)
    TextView mStampText;
    @Bind(R.id.btn_redeem)
    TextView mBtnRedeem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        ButterKnife.bind(this);
        parseHelper = new ParseHelper();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!Utils.isNetworkConnected(this))
        {
            mCustomerText.setText("Network unavailable");
            return;
        }

        parseHelper.loginParse();
        final Intent intent = getIntent();
        handleNfcIntent(intent);
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
            mCustomerText.setText(getString(R.string.customer_name, lastParsedNfcTag));
            parseHelper.registerPoint(lastParsedNfcTag, new ParseHelper.OnQueryComplete<String>()
            {
                @Override
                public void onSuccess(String result)
                {
                    countPoint(lastParsedNfcTag);
                }
            });
        }
    }

    @OnClick(R.id.btn_redeem)
    public void redeemDrink()
    {
        parseHelper.erasePoint(lastParsedNfcTag);
        mStampText.setText("Free Coffee!");
        mBtnRedeem.setVisibility(View.INVISIBLE);
    }

    public void countPoint(String userId)
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
                    mStampText.setText("" + countPoints.size());
                    if (countPoints.size() >= REDEEM_THRESHOLD)
                    {
                        mBtnRedeem.setVisibility(View.VISIBLE);
                    }
                } else
                {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }
}