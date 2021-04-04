package com.example.ngcompass.mainactivity;

import android.hardware.Sensor;


public class AndroidSensorEvent implements SensorEvent {

    SensorType mSensorType;
    float [] values;

    public AndroidSensorEvent(android.hardware.SensorEvent event) {
        int sensorType = event.sensor.getType();
        if(sensorType== Sensor.TYPE_ACCELEROMETER){
            this.mSensorType =  SensorEvent.SensorType.TYPE_ACCELEROMETER;
        }else if(sensorType==Sensor.TYPE_MAGNETIC_FIELD) {
            this.mSensorType =  SensorEvent.SensorType.TYPE_MAGNETIC_FIELD;
        }else{
            this.mSensorType = null;
        }

        this.values = event.values.clone();
    }

    @Override
    public SensorEvent.SensorType getSensorType() {
        return mSensorType;
    }

    @Override
    public float[] getValues() {
        return  values;
    }
}
