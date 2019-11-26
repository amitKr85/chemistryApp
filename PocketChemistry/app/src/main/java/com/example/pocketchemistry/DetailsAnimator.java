package com.example.pocketchemistry;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;

public class DetailsAnimator {
    private Context mContext;
    private View mView;

    DetailsAnimator(Context context, View view){
        mContext = context;
        mView = view;
    }

    public void expand(){
        WindowManager mgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = mgr.getDefaultDisplay();
        mView.measure(display.getWidth(), display.getHeight());

        int w = mView.getMeasuredWidth(); // view width
        int h = mView.getMeasuredHeight(); //view height
        Log.i("Animator collapse", w+":"+h);
        slideView(mView, 0, h);
    }

    public void collapse(){
        slideView(mView, mView.getHeight(), 0);
    }

    public static void slideView(final View view,
                                 int currentHeight,
                                 int newHeight) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(500);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }
}
