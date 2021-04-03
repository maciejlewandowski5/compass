package com.example.ngcompass.mainactivity.model;

public class Compass {

    private float currentAzimuth;
    private float lastAzimuth;
    private boolean locked;



    public Compass() {
        this.currentAzimuth = 0;
        this.lastAzimuth = 0;
        this.locked = false;
    }

    public float getCurrentAzimuth() {
        return currentAzimuth;
    }

    public float getLastAzimuth() {
        return lastAzimuth;
    }

    public void setCurrentAzimuth(float currentAzimuth) {
        this.lastAzimuth = this.currentAzimuth;
        this.currentAzimuth = currentAzimuth;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
