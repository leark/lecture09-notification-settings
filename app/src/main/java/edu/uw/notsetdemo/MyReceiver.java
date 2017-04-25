package edu.uw.notsetdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by joelross on 4/16/16.
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("TAG", "received! "+intent.toString());
        if(intent.getAction() == Intent.ACTION_POWER_CONNECTED) {
            Toast.makeText(context, "Now charging...", Toast.LENGTH_SHORT).show();
        }
        else if(intent.getAction() == Intent.ACTION_POWER_DISCONNECTED) {
            Toast.makeText(context, "Power disconnected...", Toast.LENGTH_SHORT).show();

        }
        else if(intent.getAction() == Intent.ACTION_BATTERY_LOW){
            Toast.makeText(context, "Battery is low!", Toast.LENGTH_SHORT).show();
        }
        else if(intent.getAction() == Intent.ACTION_BATTERY_OKAY) {
            Toast.makeText(context, "Battery is okay now!", Toast.LENGTH_SHORT).show();
        }

        else if(intent.getAction() == MainActivity.ACTION_SMS_STATUS) {
            if (getResultCode() == Activity.RESULT_OK) {
                Toast.makeText(context, "Message sent!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "Error sending message", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
