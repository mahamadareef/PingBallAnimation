package net.leolink.android.simpleinfinitecarousel;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyPagerAdapter extends FragmentPagerAdapter implements
        CustomViewPager2.OnPageChangeListener, CustomOntouchListener {

    public MyLinearLayout cur = null;
    private MyLinearLayout previous = null;
    private MyLinearLayout next = null, next2next = null, previous2previous = null;
    private MainActivity context;
    private FragmentManager fm;
    LinearLayout.LayoutParams layoutParams = null;
    private float scale;
    CustomViewPager customViewPager;

    // Long press in swipe ball
    boolean isTouchedLong = false;
    private int duration = 500;
    TouchAction action;
    int curPageNum;
    float X1, Y1, X2, Y2;
    public static final int DURATION_ANIMATION = 500;
    View rootView;
    private float _xDelta;
    private float _yDelta;
    private String TAG = "FragmentPagerAdapter";
    private int pageSelected = 0, pageSelectedOld;
    private float positionOffset = 0.0f, first_offset = 0.0f;
    private boolean scrollStarted, checkDirection;
    private static final float thresholdOffset = 0.8f;
    float tempfloat = 0.1f;
    int scroll_state = -1;
    boolean first_time = true;
    boolean directionRight = false;
    boolean left = false, right = false, firstAttemp = true;
    ImageView iv = null;
    float currentX = 0;// cur.getX();
    float currentY = 0;// cur.getY();
    int windowwidth = 0;
    int windowheight = 0;



    public MyPagerAdapter(MainActivity context, FragmentManager fm, TouchAction action, View rootView) {
        super(fm);
        this.context = context;
        this.fm = fm;
        this.action = action;
        this.rootView = rootView;
        windowwidth = context.getWindowManager().getDefaultDisplay().getWidth();
        windowheight = context.getWindowManager().getDefaultDisplay().getHeight();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.e(TAG, "Intantiate Item");
        if (customViewPager == null) {
            customViewPager = (CustomViewPager) container;
            customViewPager.setCustomTouchListener(this);

        }
        return super.instantiateItem(container, position);
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        if (position == MainActivity.FIRST_PAGE)
            scale = MainActivity.BIG_SCALE;
        else
            scale = MainActivity.SMALL_SCALE;
        position = position % MainActivity.PAGES;
        return MyFragment.newInstance(context, position, scale);
    }

    @Override
    public int getCount() {

        return MainActivity.PAGES * MainActivity.LOOPS;
    }


    @Override
    public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {

        ImageView iv = null;
        isTouchedLong = false;
        tempfloat += 0.005;
        this.positionOffset = positionOffset;
//        Log.e(TAG, "OnPageScrolled " + positionOffsetPixels+" //  "+windowwidth);

        cur = getRootView(pageSelected);
        iv = (ImageView) cur.getChildAt(0);
        next = getRootView(pageSelected + 1);
        previous = getRootView(pageSelected - 1);
//        firstAttemp = false;

//        setLayoutParams(null);


        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                currentX = cur.getX();
                currentY = cur.getY();
                Log.e(TAG, "Long Press " + position + " // " + currentX + " // " + currentY);
                customViewPager.setPagingEnabled(false);
                cur.animate().scaleX(0.8f).scaleY(0.8f).setDuration(duration);
                previous.animate().alpha(0).translationX(-58).setDuration(duration);
                next.animate().alpha(0).translationX(58).setDuration(duration);

                isTouchedLong = true;
                action.requestTouch(true);
                view.setOnTouchListener(listener);

                return true;
            }

        });


        if (pageSelectedOld == pageSelected) {

            if (positionOffset < 1.0f && positionOffset > 0.0f) {


                if (first_time) {
                    first_time = !first_time;
                    first_offset = positionOffset;
                }

                if (first_offset < positionOffset) {   //Left scoll
//                    Log.e(TAG, "Page state going left " + (MainActivity.SMALL_SCALE + positionOffset + " // " + positionOffset) + " // " + scroll_state + " // " + pageSelected);
//                    if (MainActivity.SMALL_SCALE + positionOffset <= MainActivity.BIG_SCALE) {
//                        next.setScaleBoth(MainActivity.SMALL_SCALE + positionOffset);
//                        cur.setScaleBoth(MainActivity.BIG_SCALE - positionOffset);
//                    }
//
                    if (!right) { //  right boolean declared because pagescroll may return any value
                        left = true;
//                        Log.e(TAG, "Page state going left " + (MainActivity.SMALL_SCALE + tempfloat));
//                        Log.e(TAG, "Page state going left " + (MainActivity.SMALL_SCALE + tempfloat + " // "+positionOffset));
                        if ((MainActivity.SMALL_SCALE + tempfloat) <= MainActivity.BIG_SCALE) {
//                            Log.e(TAG, "Next scaling");
//                            next.setScaleBoth(MainActivity.SMALL_SCALE + tempfloat);
//                            cur.setScaleBoth(MainActivity.BIG_SCALE - tempfloat);

                        }
                        if (MainActivity.SMALL_SCALE + positionOffset <= MainActivity.BIG_SCALE) {
                            next.setScaleBoth(MainActivity.SMALL_SCALE + positionOffset);
                            cur.setScaleBoth(MainActivity.BIG_SCALE - positionOffset);
                        }

                    }
                } else if (first_offset > positionOffset) { // right scroll
//                    Log.e(TAG, "Page state going right  " + (MainActivity.BIG_SCALE - positionOffset) + " // " + positionOffset + " // " + scroll_state + " // " + pageSelected);

//                    if (MainActivity.BIG_SCALE - positionOffset <= MainActivity.BIG_SCALE) {
//                        Log.e(TAG, " ........................ " + (MainActivity.BIG_SCALE - positionOffset));
//                        previous.setScaleBoth(MainActivity.BIG_SCALE - positionOffset);
////                        cur.setScaleBoth(positionOffset);
//                        cur.setScaleBoth(positionOffset-MainActivity.BIG_SCALE);
//                    }


                    if (!left) {
                        right = true;
//                        Log.e(TAG, "Page state going right  " + (MainActivity.SMALL_SCALE + tempfloat));
//                        Log.e(TAG, "Page state going right  " + (MainActivity.BIG_SCALE - positionOffset) + " // "+positionOffset+" // "+scroll_state);
                        if ((MainActivity.BIG_SCALE - tempfloat) >= MainActivity.SMALL_SCALE) {
//                            Log.e(TAG, "Scale right");
//                            previous.setScaleBoth(MainActivity.SMALL_SCALE + tempfloat);
//                            cur.setScaleBoth(MainActivity.BIG_SCALE - tempfloat);
                        }

                        if (MainActivity.BIG_SCALE - positionOffset <= MainActivity.BIG_SCALE) {
//                            Log.e(TAG, " ........................ " + (MainActivity.BIG_SCALE - positionOffset));
                            if ((MainActivity.SMALL_SCALE + (MainActivity.BIG_SCALE - positionOffset)) <= MainActivity.BIG_SCALE)
                                previous.setScaleBoth(MainActivity.SMALL_SCALE + (MainActivity.BIG_SCALE - positionOffset));
//                        cur.setScaleBoth(positionOffset);
                            if (positionOffset >= MainActivity.SMALL_SCALE)
                                cur.setScaleBoth(positionOffset);
                        }


//
//
                    }
                }


            }
        }




    }

    private View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.e(TAG, "Ontouch Event");

            if (isTouchedLong) {
                Log.e(TAG, "TouchListener " + isTouchedLong);


                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();
                LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) cur.getLayoutParams();


                float rawX = event.getX();
                float rawY = event.getY();
                float dX = 0, dY = 0;
                switch (event.getAction()) {

                    case MotionEvent.ACTION_UP:
                        Log.e(TAG, "Action Up " + currentX + " // " + currentY);

                        next.animate().alpha(1).translationX(0).setDuration(duration);
                        previous.animate().alpha(1).translationX(0).setDuration(duration);
                        cur.animate().scaleX(1).scaleY(1).setDuration(duration);

                        cur.animate().x(currentX).y(currentY).setDuration(500).start(); // revert to original position 
                        // param.gravity = Gravity.
                        action.requestTouch(true);
                        isTouchedLong = !isTouchedLong;
                        customViewPager.setPagingEnabled(true);


                        break;

                    case MotionEvent.ACTION_MOVE:
                        Log.e(TAG, "Action Move 111 " + event.getRawX() + " // " + event.getRawY());
                        int x_cord = (int) event.getRawX();
                        int y_cord = (int) event.getRawY();

                        if (x_cord > windowwidth) {
                            x_cord = windowwidth;
                        }
                        if (y_cord > windowheight) {
                            y_cord = windowheight;
                        }

                        float diffX = x_cord - currentX;
                        float diffY = y_cord - currentY;

                        Log.e(TAG, "Difference  " + diffX + " // " + diffY);

                        cur.animate()
                                .x(currentX + diffX - 100)
                                .y(currentY + diffY - 200)
                                .setDuration(0)
                                .start();

                        break;

                    case MotionEvent.ACTION_DOWN:
                        Log.e(TAG, "Action Down");
//                        _xDelta = cur.getX() - event.getRawX();
//                        _yDelta = cur.getY() - event.getRawY();
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        break;

                }
//                cur.invalidate();
                return false;
            }

            return false;
        }
    };


    private void moveViewToScreenCenter(final View view, float X, float Y) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int statusBarOffset = dm.heightPixels - rootView.getMeasuredHeight();

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);

        final int xDest = dm.widthPixels / 2 - (view.getMeasuredWidth() / 2);
        final int yDest = dm.heightPixels / 2 - (view.getMeasuredHeight() / 2) - statusBarOffset;

        TranslateAnimation anim = new TranslateAnimation(-(dm.widthPixels / 2 - X), 0, -(dm.heightPixels / 2 - Y), 0);
        anim.setDuration(DURATION_ANIMATION);
        anim.setFillAfter(false);
        anim.setAnimationListener(
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        view.layout((int) X1, (int) Y1, (int) X2, (int) Y2);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                }
        );
        view.startAnimation(anim);
    }


    @Override
    public void onPageSelected(int position) {
        tempfloat = 0.1f;
        this.pageSelected = position;

        if (next != null) {
//            setLayoutParams(null);

        }

//        Log.e(TAG, "PageSelected " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        Log.e(TAG, "11111 Scroll State changed " + state);
        this.scroll_state = state;
        if (pageSelectedOld != pageSelected) {
//            this.scroll_state = state;
        }

        if (!scrollStarted && state == ViewPager.SCROLL_STATE_DRAGGING) {
            scrollStarted = true;
            checkDirection = true;
        } else {
            scrollStarted = false;
        }
        tempfloat = 0.1f;

//        Log.e(TAG, "Page state changed  " + state + " // " + pageSelected);
        switch (state) {
            case 0:
                first_time = true;
                left = false;
                right = false;
                first_offset = 0.0f;
                firstAttemp = true;
//                Log.e(TAG,"Scroll state in state changed "+scroll_state);
//                if (pageSelectedOld == pageSelected) {
                cur.setScaleBoth(MainActivity.BIG_SCALE);//SMALL_SCALE + tempfloat);
                // previous.setScaleBoth(MainActivity.SMALL_SCALE - tempfloat);
                next.setScaleBoth(MainActivity.SMALL_SCALE);//BIG_SCALE - tempfloat);
                previous.setScaleBoth(MainActivity.SMALL_SCALE);
//                }

//                setLayoutParams(null);
                break;
            case 1:
                pageSelectedOld = pageSelected;
                break;
            case 2:
                break;

        }
    }

    private MyLinearLayout getRootView(int position) {
        return (MyLinearLayout) fm.findFragmentByTag(this.getFragmentTag(position)).getView().findViewById(R.id.root);
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + context.pager.getId() + ":" + position;
    }

    @Override
    public void ontouch(MotionEvent event) {
        Log.e(TAG, "Custom View Pager in Adapter " + event.getRawX() + " // " + event.getRawY());
//        setLayoutParams(event);
    }


    public interface TouchAction {
        public void requestTouch(boolean action);
    }

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {

        // The drag shadow image, defined as a drawable thing
        private static Drawable shadow;

        // Defines the constructor for myDragShadowBuilder
        public MyDragShadowBuilder(View v) {

            // Stores the View parameter passed to myDragShadowBuilder.
            super(v);

            // Creates a draggable image that will fill the Canvas provided by the system.
            shadow = new ColorDrawable(Color.LTGRAY);
        }

        // Defines a callback that sends the drag shadow dimensions and touch point back to the
        // system.
        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {

            // Defines local variables
            int width, height;

            // Sets the width of the shadow to half the width of the original View
            width = getView().getWidth() / 2;

            // Sets the height of the shadow to half the height of the original View
            height =
                    getView().getHeight() / 2;

            // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
            // Canvas that the system will provide. As a result, the drag shadow will fill the
            // Canvas.
            shadow.setBounds(0, 0, width, height);

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);
        }

        // Defines a callback that draws the drag shadow in a Canvas that the system constructs
        // from the dimensions passed in onProvideShadowMetrics().
        @Override
        public void onDrawShadow(Canvas canvas) {

            // Draws the ColorDrawable in the Canvas passed in from the system.
            shadow.draw(canvas);
        }
    }

    float getFirst_offsetX = -1;

    private void setLayoutParams(MotionEvent event) {

        float center_gravity = (windowwidth / 2) - (cur.getWidth() / 2);
        float right_gravity = windowwidth - cur.getWidth();
        float left_gravity = 0;

        float curLeftMargin = center_gravity;
        float nextLeftMargin = left_gravity;
        float previousleftMargin = right_gravity;
        if (event != null) {
            if (firstAttemp) {
                if (event.getRawX() > 0.0 && event.getRawX() < windowwidth) {
                    getFirst_offsetX = event.getRawX();
                    firstAttemp = !firstAttemp;
                }
            }
            Log.e(TAG, "Next left Margin  " + (getFirst_offsetX-event.getRawX()) + " // " + center_gravity);
            if ((getFirst_offsetX-event.getRawX()) <= center_gravity) {

                nextLeftMargin = (getFirst_offsetX - event.getRawX());
                curLeftMargin= center_gravity - (getFirst_offsetX - event.getRawX());
            }else{
                curLeftMargin = left_gravity;
                nextLeftMargin = center_gravity;

            }
            Log.e(TAG, "Next left Margin  22222 "+nextLeftMargin);
//            if(event.getRawX()<=center_gravity) {
//                previousleftMargin = event.getRawX();
//            }else{
//                previousleftMargin = center_gravity;
//            }
//            curLeftMargin = event.getRawX();
        }


        LinearLayout.LayoutParams nextLayoutParams = (LinearLayout.LayoutParams) next.getLayoutParams();
        nextLayoutParams.leftMargin = 235;//(int) nextLeftMargin;
//        nextLayoutParams.gravity = Gravity.LEFT;
        next.setLayoutParams(nextLayoutParams);
        LinearLayout.LayoutParams curLayoutParams = (LinearLayout.LayoutParams) cur.getLayoutParams();
        curLayoutParams.leftMargin = (int)curLeftMargin;//(int) center_gravity;
//        curLayoutParams.gravity = Gravity.CENTER;
        cur.setLayoutParams(curLayoutParams);
        LinearLayout.LayoutParams previousLayoutParams = (LinearLayout.LayoutParams) previous.getLayoutParams();
        previousLayoutParams.leftMargin = (int) previousleftMargin;//(int)windowwidth-cur.getWidth();
//        previousLayoutParams.gravity = Gravity.RIGHT;
        previous.setLayoutParams(previousLayoutParams);
    }

//    class Task implements Runnable {
//        @Override
//
//        public void run() {
//            while (threadStarter) {
////                Log.e(TAG, "Thread is Running " + positionOffset);
//
//            }
////            for (int i = 0; i <= 10; i++) {
////                final int value = i;
////                try {
////                    Thread.sleep(100);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////
////            }
//        }
//
//
//    }

}
