package com.dtavana.foodswipe.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.dtavana.foodswipe.R;
import com.dtavana.foodswipe.fragments.CycleFragment;

/**
 * Detects left and right swipes across a view.
 * Found at: https://stackoverflow.com/a/19506010
 */
public abstract class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    private final CycleFragment fragment;
    private final RelativeLayout rvRestaurant;
    private final Context context;

    public OnSwipeTouchListener(CycleFragment fragment, RelativeLayout rvRestaurant) {
        this.fragment = fragment;
        this.rvRestaurant = rvRestaurant;
        this.context = fragment.getContext();
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public abstract void onSwipeLeft(Animation a);

    public abstract void onSwipeRight(Animation a);

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
                    Animation a = AnimationUtils.loadAnimation(context, R.anim.out_right);
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            fragment.showCurrentRestaurant();
                            rvRestaurant.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                        }
                    });
                    onSwipeRight(a);
                }
                else{
                    Animation a = AnimationUtils.loadAnimation(context, R.anim.out_left);
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            rvRestaurant.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                            fragment.showCurrentRestaurant();
                        }
                    });
                    onSwipeLeft(a);
                }
                return true;
            }
            return false;
        }
    }
}