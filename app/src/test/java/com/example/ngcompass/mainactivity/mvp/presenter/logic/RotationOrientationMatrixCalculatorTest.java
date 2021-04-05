package com.example.ngcompass.mainactivity.mvp.presenter.logic;

import com.example.ngcompass.mainactivity.mvp.presenter.dependency.SensorManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RotationOrientationMatrixCalculatorTest {


    @Mock
    SensorManager mockSensorManager;

    @Test
    public void applyLowPassFilter() {

    RotationOrientationMatrixCalculator r =
            new RotationOrientationMatrixCalculator(mockSensorManager);

    float[] filtered = r.applyLowPassFilter(new float[]{1,2,3},new float[]{2,2,2});

    assertEquals(1.5,filtered[0],0);
    assertEquals(2,filtered[1],0);
    assertEquals(2.5,filtered[2],0);
    }

}