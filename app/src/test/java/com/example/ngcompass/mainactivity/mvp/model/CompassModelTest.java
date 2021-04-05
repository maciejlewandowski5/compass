package com.example.ngcompass.mainactivity.mvp.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class CompassModelTest {


    @Test
    public void setCurrentAzimuth() {

        CompassModel compassModel = new CompassModel();

        compassModel.setCurrentAzimuth(1);
        compassModel.setCurrentAzimuth(2);
        assertEquals(1,compassModel.getLastAzimuth(),0);
        assertEquals(2,compassModel.getCurrentAzimuth(),0);

    }


}