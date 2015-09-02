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
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class MyPagerAdapter extends FragmentPagerAdapter implements
        CustomViewPager2.OnPageChangeListener, CustomOntouchListener {

    public MyLinearLayout cur = null;
    private MyLinearLayout previous = null;
    private MyLinearLayout next = null;
    private MainActivity context;
    private FragmentManager fm;
    private float scale;
    CustomViewPager customViewPager;
    private static final float SCALE = (80 / 200f) * 1.25f;
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
    int scroll_state = -1;
    boolean first_time = true;
    boolean directionRight = false;
    boolean left = false, right = false, firstAttemp = true;
    ImageView iv = null;
    float currentX = 0;
    float currentY = 0;
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

        Log.e("", "Onscroll   " + positionOffset + " // " + scroll_state + " // " + pageSelected + " // " + cur);


        isTouchedLong = false;
        this.positionOffset = positionOffset;
        if (scroll_state != 2) {
            cur = getRootView(pageSelected);
            iv = (ImageView) cur.getChildAt(0);
            next = getRootView(pageSelected + 1);
            previous = getRootView(pageSelected - 1);
            if (first_time) {
                first_time = !first_time;
                first_offset = positionOffset;
            }
        }

        setLongListener();

        if (positionOffset < 1.0f && positionOffset > 0.0f) {
            if (scroll_state == 1) {
                if (first_offset < positionOffset) {   //Left scoll

                    if (!right) { //  right boolean declared because pagescroll may return any value
                        left = true;
                        if (MainActivity.SMALL_SCALE + positionOffset <= MainActivity.BIG_SCALE) {
                            next.setScaleBoth(MainActivity.SMALL_SCALE + positionOffset);
                            cur.setScaleBoth(MainActivity.BIG_SCALE - positionOffset);
                        }

                    }
                } else if (first_offset > positionOffset) { // right scroll
                    if (!left) {
                        right = true;
                        if (MainActivity.BIG_SCALE - positionOffset <= MainActivity.BIG_SCALE) {
                            if ((MainActivity.SMALL_SCALE + (MainActivity.BIG_SCALE - positionOffset)) <= MainActivity.BIG_SCALE)
                                previous.setScaleBoth(MainActivity.SMALL_SCALE + (MainActivity.BIG_SCALE - positionOffset));
                            if (positionOffset >= MainActivity.SMALL_SCALE)
                                cur.setScaleBoth(positionOffset);
                        }
                    }
                }


            }
            if (scroll_state == 2) {

                if (pageSelected == pageSelectedOld) {

                    next.setScaleBoth(MainActivity.SMALL_SCALE);
                    cur.setScaleBoth(MainActivity.BIG_SCALE);
                    previous.setScaleBoth(MainActivity.SMALL_SCALE);

                } else {
                    if (pageSelected == pageSelectedOld + 2 || pageSelected == pageSelectedOld - 2) {
                        Log.e(TAG, "Position jumped to 2nd one");

                        cur = getRootView(pageSelected);
                        iv = (ImageView) cur.getChildAt(0);
                        next = getRootView(pageSelected + 1);
                        previous = getRootView(pageSelected - 1);
                        setLongListener();
                        next.setScaleBoth(MainActivity.SMALL_SCALE);
                        cur.setScaleBoth(MainActivity.BIG_SCALE);
                        previous.setScaleBoth(MainActivity.SMALL_SCALE);
                    } else {
                        if (left) {
                            next.setScaleBoth(MainActivity.BIG_SCALE);
                            cur.setScaleBoth(MainActivity.SMALL_SCALE);
                        }
                        if (right) {
                            previous.setScaleBoth(MainActivity.BIG_SCALE);
                            cur.setScaleBoth(MainActivity.SMALL_SCALE);
                        }
                    }

                }

            }
        }

    }


    private void setLongListener() {
        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                currentX = cur.getX();
                currentY = cur.getY();
                customViewPager.setPagingEnabled(false);

//                snapToThumb(cur,cur.getX(),cur.getY());

                cur.animate().scaleX(SCALE).scaleY(SCALE).setDuration(duration);

                previous.animate().alpha(0).translationX(-58).setDuration(duration);
                next.animate().alpha(0).translationX(58).setDuration(duration);
                isTouchedLong = true;
                action.requestTouch(true);
                view.setOnTouchListener(listener);

                return true;
            }

        });

//        iv.setOnTouchListener(new View.OnTouchListener() {
//            GestureDetector mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//                @Override
//                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
////                    onPan(Utility.convertPixelsToDp((e2.getX() - e1.getX()), getActivity()), Utility.convertPixelsToDp((e2.getY() - e1.getY()), getActivity()));
//                    return true;
//                }
//
//                @Override
//                public void onLongPress(MotionEvent e) {
//                    Log.e("","OnLOngPress New ");
//                    super.onLongPress(e);
//                    if ((e.getX() > cur.getX() && e.getX() < (cur.getX() + cur.getWidth()))
//                            && (e.getY() > cur.getY() && e.getY() < (cur.getY() + cur.getHeight()))) {
//
//                        float x = e.getX() / X2;
//                        float y = e.getY() / Y2;
//
//
//                        snapToThumb(cur, e.getX(), e.getY());
//                    }
//                }
//            });
//
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.e("","Single");
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//
//                }
//
//                return true;
//            }
//        });

    }

    private void snapToThumb(final View view, final float X, final float Y) {
        ScaleAnimation anim = new ScaleAnimation(1f, SCALE, 1f, SCALE, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);

        TranslateAnimation anim2 = new TranslateAnimation(0, X - view.getX() - view.getWidth() * SCALE / 2, 0, Y - view.getY() - view.getHeight() * SCALE / 2);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(anim);
        animation.addAnimation(anim2);
        animation.setDuration(DURATION_ANIMATION);
        animation.setInterpolator(context, android.R.interpolator.linear);
        animation.setFillAfter(false);

        animation.setAnimationListener(
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Instantiates the drag shadow builder.
//                        startStopDrag(X, Y, pingBallStarted);
//                        View.DragShadowBuilder myShadow = new MyDragShadowBuilder(view, PingBallFragment.this.getActivity());
//                        view.startDrag(null,  // the data to be dragged
//                                myShadow,  // the drag shadow builder
//                                null,      // no need to use local data
//                                0          // flags (not currently used, set to 0)
//                        );
//                        if (!pingBallStarted)
//                            showWobble();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                }
        );
        view.startAnimation(animation);
    }

    private View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.e(TAG, "Ontouch Event");

            if (isTouchedLong) {
                Log.e(TAG, "TouchListener " + isTouchedLong);

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

                        Log.e(TAG, "Difference  " + cur.getHeight() + " // " + cur.getHeight() / 2);

                        cur.animate()
                                .x((event.getRawX() - (cur.getWidth() / 2)))
                                .y((event.getRawY() - (cur.getHeight()/2)))
                                .setDuration(0)
                                .start();

//                        cur.animate()
//                                .x(currentX + diffX - 100)
//                                .y(currentY + diffY - 200)
//                                .setDuration(0)
//                                .start();
//                        cur.animate().scaleX(SCALE).x((e.getX() - (cur.getWidth() / 2))).scaleY(SCALE).
//                                y((e.getY() - (cur.getHeight() / 2))).setDuration(duration);

                        break;

                    case MotionEvent.ACTION_DOWN:
                        Log.e(TAG, "Action Down");
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        break;

                }
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
        this.pageSelected = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

        this.scroll_state = state;
        if (!scrollStarted && state == ViewPager.SCROLL_STATE_DRAGGING) {
            scrollStarted = true;
            checkDirection = true;
        } else {
            scrollStarted = false;
        }
//        Log.e(TAG, "Page state changed  " + state + " // " + pageSelected);
        switch (state) {
            case 0:
                first_time = true;
                left = false;
                right = false;
                first_offset = 0.0f;
                firstAttemp = true;


                float temp = 0.05f;

                if (pageSelected != pageSelectedOld) {
                    cur = getRootView(pageSelected);
                    iv = (ImageView) cur.getChildAt(0);
                    next = getRootView(pageSelected + 1);
                    previous = getRootView(pageSelected - 1);
                    setLongListener();
                }

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

}
