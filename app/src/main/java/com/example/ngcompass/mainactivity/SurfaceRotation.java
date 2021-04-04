package com.example.ngcompass.mainactivity;

public interface SurfaceRotation {
    public enum ScreenRotation{
        ROTATION_90,
        ROTATION_180,
        ROTATION_270,
        ROTATION_0;
    }

    ScreenRotation getRotation(int surfaceRotation);
}
