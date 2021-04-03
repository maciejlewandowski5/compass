package com.example.ngcompass.animations;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.example.ngcompass.mainactivity.presenter.CompassPresenter;

public class CompassRotationAnimation {

    private RotateAnimation rotateAnimation;
    private onAnimationStart onAnimationStart;
    private onAnimationEnd onAnimationEnd;
    private ImageView compassFace;

    public CompassRotationAnimation(CompassPresenter compassPresenter, ImageView compassFace) {

        this.compassFace = compassFace;

        rotateAnimation = new RotateAnimation(
                compassPresenter.getLastAzimuth(),
                compassPresenter.getCurrentAzimuth(),
                compassFace.getWidth() / 2f,
                compassFace.getHeight() / 2f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(215);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (onAnimationStart != null) {
                    onAnimationStart.onAnimationStart();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (onAnimationEnd != null) {
                    onAnimationEnd.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void startAnimation() {
        if (compassFace != null) {
            compassFace.startAnimation(rotateAnimation);
        }
    }

    public void setOnAnimationStart(CompassRotationAnimation.onAnimationStart onAnimationStart) {
        this.onAnimationStart = onAnimationStart;
    }

    public void setOnAnimationEnd(CompassRotationAnimation.onAnimationEnd onAnimationEnd) {
        this.onAnimationEnd = onAnimationEnd;
    }

    public interface onAnimationStart {
        void onAnimationStart();
    }

    public interface onAnimationEnd {
        void onAnimationEnd();
    }
}
