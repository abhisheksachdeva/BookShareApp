package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.ui.fragments.TutorialFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WelcomeActivity extends FragmentActivity {

    private SharedPreferences preferences;
    private static final String PREF_NAME = "is_first_launch";
    private static final String IS_FIRST_TIME_LAUNCH = "is_first_time_launch";
    private ViewPager pager;
    private TextView skipTextView, nextTextView;
    private TutorialPagerAdapter tutorialPagerAdapter;
    private List<String> titleList, descriptionList;
    private List<Integer> logoIdList, dotsIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (!isFirstTimeLaunch()){
            launchHomeScreen();
            finish();
        }
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, false);
        editor.apply();

        // Making display screen full window
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);
        changeStatusBarColor();
        initViews();
        regListeners();
        formLists();
        pager.setAdapter(tutorialPagerAdapter);
        pager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    /**
     * Making status bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * initialise the various views
     */
    private void initViews() {
        pager = (ViewPager) findViewById(R.id.tutorial_pager);
        skipTextView = (TextView) findViewById(R.id.skip_button);
        nextTextView = (TextView) findViewById(R.id.next_button);
        tutorialPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());
        titleList = new ArrayList<>();
        descriptionList = new ArrayList<>();
        logoIdList = new ArrayList<>();
        dotsIdList = new ArrayList<>();
    }

    /**
     * register listeners for the views
     */
    private void regListeners() {
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchHomeScreen();
            }
        });
        nextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = getItem();
                if (current < 3){
                    pager.setCurrentItem(current + 1);
                } else{
                    launchHomeScreen();
                }
            }
        });
    }

    /**
     * form the various lists for the fragments
     */
    private void formLists() {
        // form the list containing all the titles.
        titleList = Arrays.asList(getResources().getStringArray(R.array.title_list));
        // form the list containing all the descriptions.
        descriptionList = Arrays.asList(getResources().getStringArray(R.array.description_list));
        // form the list containing all the logos.
        logoIdList.add(R.drawable.campus_books_group);
        logoIdList.add(R.drawable.add_books_group);
        logoIdList.add(R.drawable.notification_group);
        logoIdList.add(R.drawable.user_search_group);
        // form the list containing all the dots.
        dotsIdList.add(R.drawable.loading_campus_books);
        dotsIdList.add(R.drawable.loading_add_books);
        dotsIdList.add(R.drawable.loading_notifications);
        dotsIdList.add(R.drawable.loading_user_search);
    }

    /**
     * viewpager change listener
     */
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            // changing the next button text 'NEXT' / 'LET'S BEGIN'
            if (position == 3) {
                // last page. make button text to LET'S BEGIN
                nextTextView.setText(getResources().getString(R.string.lets_begin));
                skipTextView.setVisibility(View.GONE);
            } else {
                // still pages are left
                nextTextView.setText(getResources().getString(R.string.next));
                skipTextView.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    /**
     * method to launch home screen
     */
    private void launchHomeScreen() {
        startActivity(new Intent(this, SplashScreen.class));
        finish();
    }

    /**
     * return the current item of the pager adapter
     * @return position of the item
     */
    private int getItem() {
        return pager.getCurrentItem();
    }

    /**
     * to check whether the app is opened for the first time
     * @return boolean telling whether the app is opened first
     */
    public boolean isFirstTimeLaunch() {
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    private class TutorialPagerAdapter extends FragmentPagerAdapter{

        private TutorialPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TutorialFragment.newInstance(titleList.get(position), descriptionList.get(position),
                    logoIdList.get(position), dotsIdList.get(position));
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
