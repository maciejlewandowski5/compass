package com.example.ngcompass.mainactivity.mvp.presenter.dependency;

public interface SensorEvent {
    public enum SensorType{
        TYPE_ACCELEROMETER,TYPE_MAGNETIC_FIELD
    }

    public SensorEvent.SensorType getSensorType();
    float[] getValues();
}
