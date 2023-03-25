package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;




import android.content.BroadcastReceiver;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 1000;

    private Timer mTimer;

    private static final int BATTERY_SAMPLE_INTERVAL_MS = 1000 * 60 * 60; // 1 hour
    private static final long TASK_DURATION_MS = 60 * 60 * 1000; // 1 hour in milliseconds

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private TextView mBatteryStatusTextView;
    private TextView mBatteryInfoTextView;

    private  TextView mRunDuration;

    private ListView mTaskList;
    private TextView mBatteryConsumptionTextView;
    private int mStartBatteryLevel;
    private int mEndBatteryLevel;
    private long mStartTime;
    private long mEndTime;

    Button startButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startButton = findViewById(R.id.start_button);
        mRunDuration = findViewById(R.id.run_duration_text_view);
        mTaskList = findViewById(R.id.task_list);
        mBatteryStatusTextView = findViewById(R.id.battery_status_text_view);
        mBatteryInfoTextView = findViewById(R.id.battery_info_text_view);
        mBatteryConsumptionTextView = findViewById(R.id.battery_consumption_text_view);

        // Check for write external storage permission
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
        }

        // Register battery level receiver
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryLevelReceiver, batteryLevelFilter);

        // Start battery sample timer
        mHandler.postDelayed(mBatterySampleRunnable, BATTERY_SAMPLE_INTERVAL_MS);
    }

    private Runnable mBatterySampleRunnable = new Runnable() {
        @Override
        public void run() {
            int currentBatteryLevel = getBatteryLevel();
            mStartBatteryLevel = currentBatteryLevel;
            mStartTime = System.currentTimeMillis();

//            ArrayList<String> resultList = new ArrayList<>();
            double x = 1.0; // initial value of x

            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                x = Math.tan(Math.atan(x)); // apply the load function
//                resultList.add(Double.toString(i+1));
            }

//            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, resultList);
//            mTaskList.setAdapter(adapter)

            // Stop battery sample timer
//            mHandler.removeCallbacks(this);

            // Get current battery level
            currentBatteryLevel = getBatteryLevel();

            // Update end battery level and end time
            mEndBatteryLevel = currentBatteryLevel;
            mEndTime = System.currentTimeMillis();

            // Update battery status text view
            String batteryStatus = "Battery status: " + getBatteryStatus() + "\t(" + currentBatteryLevel + "%)";
            mBatteryStatusTextView.setText(batteryStatus);

            // Calculate battery consumption per hour
            long timeDifferenceMs = mEndTime - mStartTime;
            int batteryDifference = mStartBatteryLevel - mEndBatteryLevel;
            double batteryConsumptionPerHour = (double) batteryDifference / (timeDifferenceMs / (1000 * 60 * 60.0));
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String batteryConsumption = "Battery Difference: "+ batteryDifference + "% \nTime Duration: " +timeDifferenceMs + "ms \nBattery consumption per hour: " + decimalFormat.format(batteryConsumptionPerHour) + "%";
            mBatteryConsumptionTextView.setText(batteryConsumption);


            // Update battery info text view
            String batteryInfo = "current Battery Level: "+currentBatteryLevel + "%\n" + "Battery information: " + getBatteryInfo();
            mBatteryInfoTextView.setText(batteryInfo);

            // update run duration text view
            String runDuration = "Run Duration: " + timeDifferenceMs / (1000.0) + " seconds";
            mRunDuration.setText(runDuration);
            // Start battery sample timer again
//            mHandler.postDelayed(this, BATTERY_SAMPLE_INTERVAL_MS);

        }
    };



    private BroadcastReceiver mBatteryLevelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            int currentBatteryLevel = getBatteryLevel();
//
//            // Update start battery level and start time
//            mStartBatteryLevel = currentBatteryLevel;
//            mStartTime = System.currentTimeMillis();

        }
    };

    private int getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent != null) {
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (level != -1 && scale != -1) {
                return (int) ((level / (float) scale) * 100);
            }
        }
        return -1;
    }

    private String getBatteryStatus() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) : -1;
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "Not charging";
            default:
                return "Unknown";
        }
    }

    private String getBatteryInfo() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int health = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) : -1;
        String technology = batteryIntent != null ? batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) : "Unknown";
        int temperature = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) : -1;
        int voltage = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) : -1;

        String batteryInfo = "";
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                batteryInfo += "Cold";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                batteryInfo += "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                batteryInfo += "Good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                batteryInfo += "Overheat";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                batteryInfo += "Over voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                batteryInfo += "Unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                batteryInfo += "Unspecified failure";
                break;
        }
        batteryInfo += " (" + technology + ")";
        if (temperature != -1) {
            batteryInfo += ", Temperature: " + temperature / 10.0 + "°C";
        }
        if (voltage != -1) {
            batteryInfo += ", Voltage: " + voltage / 1000.0 + "V";
        }
        return batteryInfo;
    }

    public void onStartButtonClick(View v)
    {
        mHandler.postDelayed(mBatterySampleRunnable, 0);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission granted, get battery info and update text view
//                    String batteryStatus = "Battery status: " + getBatteryStatus();
//                    mBatteryStatusTextView.setText(batteryStatus);
//
//                    String batteryInfo = "Battery info: " + getBatteryInfo();
//                    mBatteryInfoTextView.setText(batteryInfo);
//
//                    // Start task and register broadcast receiver
//                    registerReceiver(mBatteryLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//                    mTimer = new Timer();
//                    mTimer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            // Update end battery level and end time
//                            int currentBatteryLevel = getBatteryLevel();
//                            mEndBatteryLevel = currentBatteryLevel;
//                            mEndTime = System.currentTimeMillis();
//
//                            // Calculate battery consumption per hour and update text view
//                            float batteryConsumption = (float) (mStartBatteryLevel - mEndBatteryLevel) / (mEndTime - mStartTime) * 60 * 60;
//                            String batteryConsumptionText = "Battery consumption per hour: " + batteryConsumption + "%";
//                            mBatteryConsumptionTextView.setText(batteryConsumptionText);
//
//                            // Unregister broadcast receiver and cancel timer
//                            unregisterReceiver(mBatteryLevelReceiver);
//                            mTimer.cancel();
//                        }
//
//                        private void unregisterReceiver(BroadcastReceiver mBatteryLevelReceiver) {
//                        }
//                    }, TASK_DURATION_MS, TASK_DURATION_MS);
                } else {
                    // Permission not granted, show message and finish activity
                    Toast.makeText(this, "Permission denied, cannot run task", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
}




