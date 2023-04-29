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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;




import android.content.BroadcastReceiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private static final int BATTERY_SAMPLE_INTERVAL_MS = 1000 * 60 * 60; // 1 hour

    private final Handler  mHandler = new Handler(Looper.getMainLooper());
    private TextView mBatteryStatusTextView;
    private TextView mBatteryInfoTextView;
    private boolean isThreadRunning = false;

    private  ProgressBar progressBar;
    String countsString;
    String batteryStatus;

    String batteryConsumption, batteryInfo,runDuration;
    private  TextView mRunDuration;

    private TextView mcount_of_iterations;

//    private ListView mTaskList;
    private TextView mBatteryConsumptionTextView;
    private int mStartBatteryLevel;
    private int mEndBatteryLevel;
    private long mStartTime;
    private long mEndTime;

//    Button startButton;

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_ACCESS_NETWORK_STATE = 2;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mcount_of_iterations = findViewById(R.id.count_of_iterations);
        mRunDuration = findViewById(R.id.run_duration_text_view);
        mBatteryStatusTextView = findViewById(R.id.battery_status_text_view);
        mBatteryInfoTextView = findViewById(R.id.battery_info_text_view);
        mBatteryConsumptionTextView = findViewById(R.id.battery_consumption_text_view);
        progressBar = findViewById(R.id.progress_bar);


        // Register battery level receiver
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryLevelReceiver, batteryLevelFilter);

        // Start battery sample timer
//        mHandler.postDelayed(mBatterySampleRunnable, BATTERY_SAMPLE_INTERVAL_MS);
    }



    private Runnable mBatterySampleRunnable = new Runnable() {
        @Override
        public void run() {
            loadFunction();
            isThreadRunning = false;
        };

    };


private void loadFunction(){int currentBatteryLevel = getBatteryLevel();
    mStartBatteryLevel = currentBatteryLevel;
    mStartTime = System.currentTimeMillis();

    // Show progress bar


    double x = 1.0;

    for (int i = 0; i < Integer.MAX_VALUE; i++) {
        x = Math.tan(Math.atan(x)); // applying the load function



        // Show count_of_iterations to the screen
         countsString = "Load function ran for : " + Integer.MAX_VALUE +" number of times\n\n";

        // Get current battery level
        currentBatteryLevel = getBatteryLevel();

        // Update end battery level and end time
        mEndBatteryLevel = currentBatteryLevel;
        mEndTime = System.currentTimeMillis();

        // Update battery status text view
        batteryStatus = "Battery status: " + getBatteryStatus() + "\t(" + currentBatteryLevel + "%)\n\n";

        // Calculate battery consumption per hour
        long timeDifferenceMs = mEndTime - mStartTime;
        int batteryDifference = mStartBatteryLevel - mEndBatteryLevel;
        double batteryConsumptionPerHour = (double) batteryDifference / (timeDifferenceMs / (1000 * 60 * 60.0));
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        batteryConsumption = "Battery Difference: "+ batteryDifference + "% \nTime Duration: " +timeDifferenceMs + "ms \nBattery consumption per hour: " + decimalFormat.format(batteryConsumptionPerHour) + "%\n\n";


        // Update battery info text view
         batteryInfo = "current Battery Level: "+currentBatteryLevel + "%\n" + "Battery information: " + getBatteryInfo() + "\n\n";

        // update run duration text view
         runDuration = "Run Duration: " + timeDifferenceMs / (1000.0) + " seconds\n\n";
    }}

    private BroadcastReceiver mBatteryLevelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


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
        if (!isThreadRunning){
            Thread thread = new Thread(mBatterySampleRunnable);
            thread.start();
            isThreadRunning = true;
            progressBar.setVisibility(View.VISIBLE);
        }
        mcount_of_iterations.setText(countsString);
        mBatteryStatusTextView.setText(batteryStatus);
        mRunDuration.setText(runDuration);
        mBatteryInfoTextView.setText(batteryInfo);
        mBatteryConsumptionTextView.setText(batteryConsumption);

        if (isThreadRunning == false){
            progressBar.setVisibility(View.GONE);
        }

//        mHandler.postDelayed(mBatterySampleRunnable, 0);
    }
    }





