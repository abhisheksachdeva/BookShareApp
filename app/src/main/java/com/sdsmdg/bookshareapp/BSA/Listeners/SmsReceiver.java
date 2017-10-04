package com.sdsmdg.bookshareapp.BSA.Listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;

/**
 * Created by harshit on 4/10/17.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle data  = intent.getExtras();
        // bundle contains protocol data unit
        // objects used in sending mails.
        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
            //You must check here if the sender is your provider and not another one with same text.
            if (sender.equals(CommonUtilities.OTP_SENDER_ONE)) {
                String messageBody = smsMessage.getMessageBody();
                //Pass on the text to our listener.
                mListener.messageReceived(messageBody);
            }
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
