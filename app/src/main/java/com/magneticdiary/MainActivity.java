package com.magneticdiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String PATTERN = "#.000";
    static final int LOCATION_SETTINGS_REQUEST = 1;
    static final int PERMISSION_ID = 44;
    public static DecimalFormat DECIMAL_FORMATTER;
    public static final String PREFERENCES = "preferences";
    public static final String VIBRATOR = "vibrator";
    private TextView valueTextView, mediumValueTextView, timerTextView;
    ImageButton startMeasurmentButton, addPlaceButton, archiveButton, vibratorButton;


    private SensorManager sensorManager;
    SharedPreferences sharedPreferences;
    Dialog_add_place dialog_add_place;
    GeoLocationFinder geoLocationFinder;


    List<Double> magneticObservation = new ArrayList<>();
    double magnitude;
    boolean isTimerRunning;

    String mediumValue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();


        startMeasurmentButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                magneticObservation.clear();
                sensorManager.registerListener(this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                        SensorManager.SENSOR_DELAY_NORMAL);
                startMeasurmentButton.setImageResource(R.drawable.ic_switchoff);
                mediumValueTextView.setText(R.string.measuring);
                isTimerRunning = true;
                timer.start();
            }
        });

        addPlaceButton.setOnClickListener((v -> {
            dialog_add_place = new Dialog_add_place(MainActivity.this);
            getGeoPosition();
            dialog_add_place.showMenuDialog();
            dialog_add_place.setMagneticFieldValue(mediumValue);
        }));

        archiveButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlaceLibraryActivity.class);
            startActivity(intent);
        });

        vibratorButton.setOnClickListener(v -> {
            putVibrator(!getVibrator());
            vibratorButton.setImageResource(getVibrator() ? R.drawable.ic_vibration_is_on : R.drawable.ic_vibration_is_off);
        });

    }

    private void init() {
        setContentView(R.layout.activity_main);
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        valueTextView = (TextView) findViewById(R.id.value);
        mediumValueTextView = findViewById(R.id.medium_value);
        timerTextView = findViewById(R.id.timer_text_view);
        startMeasurmentButton = findViewById(R.id.button_start_measurement);
        addPlaceButton = findViewById(R.id.addPlaceButton);
        archiveButton = findViewById(R.id.archiveButton);
        vibratorButton = findViewById(R.id.vibratorButton);

        try {
            if (getVibrator()) vibratorButton.setImageResource(R.drawable.ic_vibration_is_on);
        } catch (NullPointerException npe) {
            putVibrator(false);
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DECIMAL_FORMATTER = new DecimalFormat(PATTERN, symbols);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float magX = event.values[0];
            float magY = event.values[1];
            float magZ = event.values[2];
            magnitude = Math.sqrt((magX * magX) + (magY * magY) + (magZ * magZ));
            String currentValue = DECIMAL_FORMATTER.format(magnitude) + " \u00B5Tesla";
            vibrate((int) magnitude);
            valueTextView.setText(currentValue);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    CountDownTimer timer = new CountDownTimer(30000, 1000) {
        public void onTick(long millisUntilFinished) {
            if (millisUntilFinished < 4000) {
                timerTextView.setTextColor(getResources().getColor(R.color.colorFire));
            }
            magneticObservation.add(Double.parseDouble(DECIMAL_FORMATTER.format(magnitude)));
            timerTextView.setText(timeLeftFormatted(millisUntilFinished));
        }


        public void onFinish() {
            sensorManager.unregisterListener(MainActivity.this);
            Double mediumMagneticValue = null;
            double sum = 0;
            long count = 0;
            for (Double s : magneticObservation) {
                double v = (s);
                sum += v;
                count++;
            }
            mediumMagneticValue = count > 0 ? (sum / count) : null;
            mediumValue = DECIMAL_FORMATTER.format(mediumMagneticValue) + " \u00B5Tesla";
            startMeasurmentButton.setImageResource(R.drawable.ic_switchon);
            timer.cancel();
            isTimerRunning = false;
            timerTextView.setTextColor(getResources().getColor(R.color.colorToxic));
            timerTextView.setText("00:00");
            Toast.makeText(MainActivity.this, R.string.completed, Toast.LENGTH_LONG).show();
            mediumValueTextView.setText(mediumValue);
        }

    };

    public String timeLeftFormatted(long mtimeleftminutes) {
        int minutes = (int) mtimeleftminutes / 1000 / 60;
        int seconds = (int) mtimeleftminutes / 1000 % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

    }

    protected void startActivityTurnOnLocation() {
        Toast.makeText(this, R.string.location_permision, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, LOCATION_SETTINGS_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTINGS_REQUEST) {
            getGeoPosition();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getGeoPosition();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    void getGeoPosition() {
        geoLocationFinder = new GeoLocationFinder(MainActivity.this);
        geoLocationFinder.getLastLocation();
    }

    public void vibrate(int magnitude) {
        if (getVibrator()) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(magnitude);
        }

    }

    boolean getVibrator() {
        return sharedPreferences.getBoolean(VIBRATOR, true);
    }

    void putVibrator(Boolean vibrator) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(VIBRATOR, vibrator).apply();

    }


}



