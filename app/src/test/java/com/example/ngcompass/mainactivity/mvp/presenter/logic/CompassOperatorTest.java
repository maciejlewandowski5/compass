package com.example.ngcompass.mainactivity.mvp.presenter.logic;

import com.example.ngcompass.mainactivity.mvp.model.CompassModel;
import com.example.ngcompass.mainactivity.mvp.presenter.dependency.SensorManager;
import com.example.ngcompass.mainactivity.mvp.presenter.dependency.SurfaceRotation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CompassOperatorTest {


    @Mock
    RotationOrientationMatrixCalculator mockRotationCalculator;
    @Mock
    CompassModel compassModel;



    @Test(expected = IllegalStateException.class)
    public void updateLockedCompass() {

        compassModel = mock(CompassModel.class);
        when(compassModel.isLocked()).thenReturn(true);

        CompassOperator co = new CompassOperator(mockRotationCalculator,compassModel);

        co.updateCompass();
    }

    @Test
    public void updateUnlockedCompass() {

        CompassModel compassModel1 = new CompassModel();
        mockRotationCalculator = mock(RotationOrientationMatrixCalculator.class);

        float angle = 30;

        when(mockRotationCalculator.calculateAzimuth()).thenReturn(angle).thenReturn(angle*2);
        CompassOperator co = new CompassOperator(mockRotationCalculator,compassModel1);

        co.unlockCompass();
        co.updateCompass();
        co.updateCompass();

        assertEquals(angle*2,compassModel1.getCurrentAzimuth(),0);
        assertEquals(angle,compassModel1.getLastAzimuth(),0);
    }

    @Test
    public void updateWithScreenRotated() {

        mockRotationCalculator = mock(RotationOrientationMatrixCalculator.class);

        float angle = 30;
        int rotatedScreen = -90;

        when(mockRotationCalculator.calculateAzimuth()).thenReturn(angle).thenReturn(angle*2);
        CompassOperator co = new CompassOperator(mockRotationCalculator);

        co.setScreenOrientationCorrection(rotatedScreen);

        co.unlockCompass();
        co.updateCompass();
        co.updateCompass();


        assertEquals(angle*2+rotatedScreen,co.getCurrentAzimuth(),0);
        assertEquals(angle+rotatedScreen,co.getLastAzimuth(),0);

    }



    @Test
    public void updateAccelerometerReadings() {

        mockRotationCalculator = mock(RotationOrientationMatrixCalculator.class);

        CompassOperator co = new CompassOperator(mockRotationCalculator);

        float[] reading = new float[]{1,2,3};

        co.updateAccelerometerReadings(reading);
        verify(mockRotationCalculator,times(1)).
                updateAccelerometerReadings(reading);
    }

    @Test
    public void updateMagnetometerReadings() {
        mockRotationCalculator = mock(RotationOrientationMatrixCalculator.class);

        CompassOperator co = new CompassOperator(mockRotationCalculator);

        float[] reading = new float[]{1,2,3};

        co.updateMagnetometerReadings(reading);
        verify(mockRotationCalculator,times(1)).
                updateMagnetometerReadings(reading);
    }

    @Test
    public void updateRotationModel() {
        mockRotationCalculator = mock(RotationOrientationMatrixCalculator.class);

        CompassOperator co = new CompassOperator(mockRotationCalculator);

        co.updateRotationModel();
        verify(mockRotationCalculator,times(1)).
                updateRotationModel();

    }
}