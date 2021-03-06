package com.dnbitstudio.coffeenfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

import java.io.UnsupportedEncodingException;

public class NfcMessageParser {

    public static String parseNfcIntent(Intent intent) {
        if (intent == null) {
            return null;
        } else {
            String action = intent.getAction();
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
                NdefMessage[] msgs;
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (rawMsgs != null) {
                    msgs = new NdefMessage[rawMsgs.length];
                    for (int i = 0; i < rawMsgs.length; i++) {
                        msgs[i] = (NdefMessage) rawMsgs[i];
                        NdefRecord[] ndefRecords = msgs[i].getRecords();
                        for (NdefRecord record : ndefRecords) {
//                        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
//                        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));
                            try {
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
                                String idUser = new String(payload, languageCodeLength + 1,
                                        payload.length - languageCodeLength - 1, textEncoding);
                                Log.i("****TAG", "RECORD " + idUser);
                                return idUser;
                            } catch (UnsupportedEncodingException e) {
                                // should never happen unless we get a malformed tag.
                                throw new IllegalArgumentException(e);
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

}
