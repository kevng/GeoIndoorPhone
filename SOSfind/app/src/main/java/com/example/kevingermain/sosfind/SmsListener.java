package com.example.kevingermain.sosfind;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by KEVINGermain on 07/12/2017.
 */

public class SmsListener extends BroadcastReceiver {

    public static boolean wantedActive;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();

        // Variable where the message content will be stored
        String strMessage = "";

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {

                String messageBody = smsMessage.getMessageBody();
                String senderNumber= smsMessage.getOriginatingAddress();

                String msgBodyAndSender = "SMS from " + senderNumber + " : " + messageBody;
                Log.d("test", msgBodyAndSender);
                Toast.makeText(context.getApplicationContext(), msgBodyAndSender, Toast.LENGTH_SHORT).show();

                // If the mobile 1 are looking for the mobile 2, he send him a SMS with as message body "Wanted"
                if (messageBody.equals("Wanted"))
                {
                    wantedActive = true;
                    Intent intent20 = new Intent(context,MainActivity.class);
                    intent20.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent20);
                }
            }
        }

    }

}