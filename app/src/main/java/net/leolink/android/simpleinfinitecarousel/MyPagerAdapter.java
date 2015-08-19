package net.leolink.android.simpleinfinitecarousel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MyPagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    private MyLinearLayout cur = null;
    private MyLinearLayout previous = null;
    private MyLinearLayout next = null;
    private MainActivity context;
    private FragmentManager fm;
    private float scale;
    boolean isTouchedLong = false;
    private int duration = 500;
    TouchAction action;
    int curPageNum;
    float X1, Y1, X2, Y2;
    public static final int DURATION_ANIMATION = 500;
    View rootView;
    private int _xDelta;
    private int _yDelta;

    public MyPagerAdapter(MainActivity context, FragmentManager fm, TouchAction action, View rootView) {
        super(fm);
        this.context = context;
        this.fm = fm;
        this.action = action;
        this.rootView = rootView;
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
        cur = getRootView(position);
        ImageView iv = (ImageView) cur.getChildAt(0);
        previous = getRootView(position - 1);
        next = getRootView(position + 1);

        X1 = cur.getX();
        Y1 = cur.getY();
        X2 = cur.getX() + cur.getWidth();
        Y2 = cur.getY() + cur.getHeight();

        if (positionOffset >= 0f && positionOffset <= 1f) {
            cur.setScaleBoth(MainActivity.BIG_SCALE - MainActivity.DIFF_SCALE * positionOffset);
            next.setScaleBoth(MainActivity.SMALL_SCALE + MainActivity.DIFF_SCALE * positionOffset);
        }

        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cur.animate().scaleX(0.8f).scaleY(0.8f).setDuration(duration);
                previous.animate().alpha(0).translationX(-58).setDuration(duration);
                next.animate().alpha(0).translationX(58).setDuration(duration);
                isTouchedLong = true;
                action.requestTouch(false);
                return false;
            }
        });
        iv.setOnTouchListener(listener);

        if (curPageNum != position) {
            curPageNum = position;
            Toast.makeText(context, "Current is " + curPageNum, Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (isTouchedLong) {
                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();
                LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) cur.getLayoutParams();
                float currentX = cur.getX();
                float currentY = cur.getY();
                float rawX = event.getX();
                float rawY = event.getY();
                switch (event.getAction()) {

                    case MotionEvent.ACTION_UP:
                        cur.animate().scaleX(1).scaleY(1).setDuration(duration);
                        previous.animate().alpha(1).translationX(0).setDuration(duration);
                        next.animate().alpha(1).translationX(0).setDuration(duration);
                        action.requestTouch(true);
//                        moveViewToScreenCenter(cur, event.getX(), event.getY());
                        isTouchedLong = !isTouchedLong;
                        break;

                    case MotionEvent.ACTION_SCROLL:
                        break;

                    case MotionEvent.ACTION_HOVER_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cur.getLayoutParams();
                        layoutParams.leftMargin = X - _xDelta;
                        layoutParams.topMargin = Y - _yDelta;
                        layoutParams.rightMargin = -250;
                        layoutParams.bottomMargin = -250;
                        cur.setLayoutParams(layoutParams);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) cur.getLayoutParams();
                        _xDelta = X - lParams.leftMargin;
                        _yDelta = Y - lParams.topMargin;
                        break;

                }
                cur.invalidate();
                return true;
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
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private MyLinearLayout getRootView(int position) {
        return (MyLinearLayout) fm.findFragmentByTag(this.getFragmentTag(position)).getView().findViewById(R.id.root);
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + context.pager.getId() + ":" + position;
    }


    public interface TouchAction {
        public void requestTouch(boolean action);
    }
}
