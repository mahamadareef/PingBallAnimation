package net.leolink.android.simpleinfinitecarousel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author Navin Prasad <navin.prasad@fastacash.com>
 * @since 19/Aug/2015
 */


public class CustomViewPager extends CustomViewPager2 {
    private boolean enabled;
    CustomOntouchListener listener;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);


        this.enabled = true;
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        super.setOnPageChangeListener(listener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {

            if (listener != null)
                listener.ontouch(event);

            return super.onTouchEvent(event);
        }

        return true;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCustomTouchListener(CustomOntouchListener listener) {
        this.listener = listener;
    }
}
