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
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;

    private String lastParsedNfcTag;

    private static final String TAG = MainActivity.class.getName();
    public static final int REDEEM_TRESHOLD = 10;

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

        ParseHelper.loginParse();
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
            ParseHelper.registerPoint(lastParsedNfcTag, new ParseHelper.OnQueryComplete<String>() {
                @Override
                public void onSuccess(String result) {
                    ParseHelper.countPoint(result, new ParseHelper.OnQueryComplete<Integer>() {
                        @Override
                        public void onSuccess(final Integer result) {
                            mStampText.setText("Current stamp count is: " + result);
                            if (result >= REDEEM_TRESHOLD) {
                                mBtnRedeem.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });
        }
    }

    @OnClick(R.id.btn_redeem)
    public void redeemDrink()
    {
        ParseHelper.erasePoint(lastParsedNfcTag);
        Toast.makeText(MainActivity.this, "Redeem pressed - removing " + lastParsedNfcTag, Toast.LENGTH_SHORT).show();
    }
}