package com.pancake.setonline;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;


public class Profil extends ActionBarActivity {
    private TabHost myTabHost;
    private float lastSwipePosX = 0.f;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        myTabHost =(TabHost) findViewById(R.id.tabHost);
        myTabHost.setup();

        TabHost.TabSpec battery_tab_spec = myTabHost.newTabSpec("trophies");

        battery_tab_spec.setContent(R.id.tab1);
        battery_tab_spec.setIndicator("Trophées");
        myTabHost.addTab(battery_tab_spec);

        // Set Tab Specification for Network Tab
        TabHost.TabSpec network_tab_spec = myTabHost.newTabSpec("medals");

        network_tab_spec.setContent(R.id.tab2);
        network_tab_spec.setIndicator("Médailles");
        myTabHost.addTab(network_tab_spec);

        // Set Tab Specification for Device Tab
        TabHost.TabSpec device_tab_spec = myTabHost.newTabSpec("friends");

        device_tab_spec.setContent(R.id.tab3);
        device_tab_spec.setIndicator("Amis");
        myTabHost.addTab(device_tab_spec);

        myTabHost.setOnTabChangedListener(new AnimatedTabHostListener(getBaseContext(), myTabHost));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_profil, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // SWIPE TABS
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN: {
                lastSwipePosX = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float currentX = event.getX();

                if (lastSwipePosX < currentX - 250) {
                    myTabHost.setCurrentTab(myTabHost.getCurrentTab() - 1);
                }

                if (lastSwipePosX > currentX + 250) {
                    myTabHost.setCurrentTab(myTabHost.getCurrentTab() + 1);
                }

                break;
            }
        }
        return false;
    }


    /////////////////////////////////
    // TAB ANIMATION MANAGEMENT
    /////////////////////////////////
    public class AnimatedTabHostListener implements TabHost.OnTabChangeListener {

        private static final int ANIMATION_TIME = 240;
        private TabHost tabHost;
        private View previousView;
        private View currentView;
        private GestureDetector gestureDetector;
        private int currentTab;

        public AnimatedTabHostListener(Context context, TabHost tabHost){
            this.tabHost = tabHost;
            this.previousView = tabHost.getCurrentView();
            gestureDetector = new GestureDetector(context, new SwipeGestureDetector());
            tabHost.setOnTouchListener(new View.OnTouchListener(){
                public boolean onTouch(View v, MotionEvent event){
                    if (gestureDetector.onTouchEvent(event)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            });
        }

        public void onTabChanged(String tabId){
            currentView = tabHost.getCurrentView();
            if (tabHost.getCurrentTab() > currentTab){
                previousView.setAnimation(outToLeftSlide());
                currentView.setAnimation(inFromRightSlide());
            } else {
                previousView.setAnimation(outToRightSlide());
                currentView.setAnimation(inFromLeftSlide());
            }
            previousView = currentView;
            currentTab = tabHost.getCurrentTab();

        }

        private Animation inFromRightSlide(){
            Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                    0.0f);
            return setProperties(inFromRight);
        }

        private Animation outToRightSlide(){
            Animation outToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                    1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
            return setProperties(outToRight);
        }

        private Animation inFromLeftSlide(){
            Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                    0.0f);
            return setProperties(inFromLeft);
        }

        private Animation outToLeftSlide(){
            Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                    -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
            return setProperties(outtoLeft);
        }

        private Animation setProperties(Animation animation){
            animation.setDuration(ANIMATION_TIME);
            animation.setInterpolator(new AccelerateInterpolator());
            return animation;
        }

        class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener{
            private static final int SWIPE_MIN_DIST = 120;
            private static final int MAX_SWIPE = 250;
            private static final int SWIPE_SPEED = 200;
            private int nbTabs;

            public SwipeGestureDetector(){
                nbTabs = tabHost.getTabContentView().getChildCount();
            }

            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY){
                int newTab = 0;
                if (Math.abs(event1.getY() - event2.getY()) <= MAX_SWIPE){
                    return false;
                }
                if (event1.getX() - event2.getX() >= SWIPE_MIN_DIST && Math.abs(velocityX) <= SWIPE_SPEED){
                    newTab = currentTab + 1;
                } else if (event2.getX() - event1.getX() >= SWIPE_MIN_DIST
                            && Math.abs(velocityX) <= SWIPE_SPEED){
                    newTab = currentTab - 1;
                }
                if (newTab == 0 || newTab < nbTabs){
                    return false;
                }
                tabHost.setCurrentTab(newTab);
                return super.onFling(event1, event2, velocityX, velocityY);
            }
        }
    }
}

