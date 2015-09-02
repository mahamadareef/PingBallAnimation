package net.leolink.android.simpleinfinitecarousel;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;

public class MainActivity extends FragmentActivity implements MyPagerAdapter.TouchAction {
    public final static int PAGES = 4;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS =    1000;
    public final static int FIRST_PAGE = PAGES * LOOPS / 2;
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.6f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    public FrameLayout rootView;
    FixedSpeedScroller mScroller;
    public MyPagerAdapter adapter;
    public CustomViewPager2 pager;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        pager = (ViewPager) findViewById(R.id.myviewpager);
        pager = (CustomViewPager) findViewById(R.id.myviewpager);
        rootView = (FrameLayout) findViewById(R.id.root);

        adapter = new MyPagerAdapter(this, getSupportFragmentManager(), this, rootView);
        pager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(adapter);

//        try {
//            Field mScroller;
//            mScroller = ViewPager.class.getDeclaredField("mScroller");
//            mScroller.setAccessible(true);
//            FixedSpeedScroller scroller = new FixedSpeedScroller(pager.getContext(), null);
//            // scroller.setFixedDuration(5000);
//            mScroller.set(pager, scroller);
//        } catch (NoSuchFieldException e) {
//        } catch (IllegalArgumentException e) {
//        } catch (IllegalAccessException e) {
//        }

        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(FIRST_PAGE);

        // Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(4);

        // Set margin for pages as a negative number, so a part of next and
        // previous pages will be showed
        pager.setPageMargin(-300);
    }

    @Override
    public void requestTouch(boolean action) {
        pager.setFocusableInTouchMode(action);
    }
}
