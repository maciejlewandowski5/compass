package com.example.ngcompass.mainactivity.mvp.model;

import com.example.ngcompass.mainactivity.mvp.presenter.dependency.Location;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PointerCompassModelTest {

    @Mock
    Location mockCurrentLocation;

    @Mock
    Location mockDestination;

    @Test
    public void setCurrentAzimuth() {
        when(mockCurrentLocation.bearingTo(mockDestination)).thenReturn(50f);

        PointerCompassModel pointerCompassModel = new PointerCompassModel(
                mockDestination,
                mockCurrentLocation);

        pointerCompassModel.setCurrentAzimuth(50);
        pointerCompassModel.setCurrentAzimuth(100);

        assertEquals(-50f,pointerCompassModel.getCurrentAzimuth(),0f);
        assertEquals(0f,pointerCompassModel.getLastAzimuth(),0f);

    }
}