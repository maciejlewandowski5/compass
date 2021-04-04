package com.example.ngcompass.mainactivity.androidimp;

import android.view.Surface;

import com.example.ngcompass.mainactivity.mvp.presenter.dependency.SurfaceRotation;

public class AndroidSurfaceRotation implements SurfaceRotation {
    @Override
    public  ScreenRotation getRotation(int surfaceRotation) {
        if(surfaceRotation== Surface.ROTATION_0){
            return ScreenRotation.ROTATION_0;
        }else if(surfaceRotation==Surface.ROTATION_90){
            return ScreenRotation.ROTATION_90;
        }else if(surfaceRotation==Surface.ROTATION_180){
            return ScreenRotation.ROTATION_180;
        }else if(surfaceRotation==Surface.ROTATION_270){
            return ScreenRotation.ROTATION_270;
        }
        return ScreenRotation.ROTATION_0;
    }
}
