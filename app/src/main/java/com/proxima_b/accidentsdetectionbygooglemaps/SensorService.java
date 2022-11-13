package com.proxima_b.accidentsdetectionbygooglemaps;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class SensorService extends Service implements SensorEventListener {

    // TAG to identify notification
    private static final int NOTIFICATION = 007;

    // IBinder object to allow Activity to connect
    private final IBinder mBinder = new LocalBinder();

    // Sensor Objects
    private Sensor accelerometer;
    private SensorManager mSensorManager;

    private double accelerationX, accelerationY, accelerationZ;

    private int threshold = 15;

    // Notification Manager
    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public SensorService getService() {
            return SensorService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSensorManager.unregisterListener(this);                            // Unregister sensor when not in use

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        accelerationX = (Math.round(sensorEvent.values[0]*1000)/1000.0);
        accelerationY = (Math.round(sensorEvent.values[1]*1000)/1000.0);
        accelerationZ = (Math.round(sensorEvent.values[2]*1000)/1000.0);

        /*** Detect Accident ***/
        if (accelerationX > threshold || accelerationY > threshold || accelerationZ > threshold) {
            System.out.println("Accident detected");
            Toast.makeText(this,"Accident detected",Toast.LENGTH_LONG).show();
            stopService(new Intent(this, SensorService.class));
            /*
            1) Here we take user co-ordinates and send it google api.
            2) Google will verify the data and push a notification to the nearest user
            (with in 100 meters range) asking if there is any accident. Based on crowd souring.
            we alert the first responders and recommend the users to take the different route if the road
            is blocked.

             */
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
