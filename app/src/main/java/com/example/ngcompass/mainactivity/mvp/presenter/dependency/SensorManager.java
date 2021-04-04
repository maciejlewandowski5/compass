package com.example.ngcompass.mainactivity.mvp.presenter.dependency;

public interface SensorManager {


    public float[] getRotationMatrix(float[] R,
                                     float[] I,
                                     float[] gravity,
                                     float[] geomagnetic);

    public float[] getOrientationMatrix(float[] R, float[] values);
}
