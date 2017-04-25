package edu.uw.notsetdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "**Main**";

    public static final String EXTRA_MESSAGE = "edu.uw.intentdemo.message";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SMS_SENT_CODE = 2;
    private static final int NOTIFY_CODE = 3;

    private static final int TEST_NOTIFICATION_ID = 1;

    public static final String ACTION_SMS_STATUS = "edu.uw.intentdemo.ACTION_SMS_STATUS";
    private int notificationCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View launchButton = findViewById(R.id.btnLaunch);
        launchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "Launch button pressed");

                //                         context,           target
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Greetings from sunny MainActivity!");
                startActivity(intent);
            }
        });

        SharedPreferences prefs = getSharedPreferences("TestPrefs", MODE_PRIVATE);
        this.notificationCount = prefs.getInt("notifications", 0);

        //dynamic BroadcastReceiver registry
//        IntentFilter batteryFilter = new IntentFilter();
//        batteryFilter.addAction(Intent.ACTION_BATTERY_LOW);
//        batteryFilter.addAction(Intent.ACTION_BATTERY_OKAY);
//        batteryFilter.addAction(Intent.ACTION_POWER_CONNECTED);
//        batteryFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
//        this.registerReceiver(new MyReceiver(), batteryFilter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("TestPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("notifications", notificationCount);
        editor.commit();
    }

    public void callNumber(View v) {
        Log.v(TAG, "Call button pressed");

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:206-685-1622"));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        
    }

    public void takePicture(View v) {
        Log.v(TAG, "Camera button pressed");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extras.get("data");
            ImageView imageView = (ImageView)findViewById(R.id.imgThumbnail);
            imageView.setImageBitmap(imageBitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void sendMessage(View v) {
        Log.v(TAG, "Message button pressed");

        //demo
        SmsManager smsManager = SmsManager.getDefault();

        Intent intent = new Intent(ACTION_SMS_STATUS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, SMS_SENT_CODE, intent, 0);

        smsManager.sendTextMessage("5554", null, "This is a test message!", pendingIntent, null);

    }


    public void notify(View v){
        Log.v(TAG, "Notify button pressed");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("shouldNotify", false) == true) {

            notificationCount++;
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("My notification")
                    .setContentText("Hello world!");
            // Make the intent
            Intent intent = new Intent(this, SecondActivity.class);

            // for building the artificial history
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(SecondActivity.class);
            stackBuilder.addNextIntent(intent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(NOTIFY_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
            // Done making intent

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("Clicky Clicky")
                    .setContentText("You clicked this " + notificationCount + " times");
            builder.setContentIntent(pendingIntent);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setVibrate(new long[]{0, 100, 200, 300, 400, 500});
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

            NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = builder.build();
            notifyManager.notify(TEST_NOTIFICATION_ID, notification);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_notify:
                notify(null);
                return true;
            case R.id.menu_item_prefs:
                Log.v(TAG, "Settings button pressed");
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_click:
                Log.v(TAG, "Extra button pressed");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
