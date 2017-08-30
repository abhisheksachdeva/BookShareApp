package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.ui.fragments.TutorialFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WelcomeActivity extends FragmentActivity {

    private ViewPager pager;
    private TextView skipTextView, nextTextView;
    private ImageView dot1, dot2, dot3, dot4;
    private ImageView[] dots;
    private TutorialPagerAdapter tutorialPagerAdapter;
    private List<String> titleList, descriptionList;
    private List<Integer> logoIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making display screen full window
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);
        changeStatusBarColor();
        initViews();
        regListeners();
        formLists();
        setBottomDots(0);
        pager.setAdapter(tutorialPagerAdapter);
        pager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    /**
     * to change the color of dots appropriately
     * @param i the current position of the viewpager
     */
    private void setBottomDots(int i) {
        for (int j = 0; j < 4; j++){
            dots[j].setImageResource(R.drawable.normal_dot);
        }
        dots[i].setImageResource(R.drawable.current_page_dot);
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
        dot1 = (ImageView) findViewById(R.id.image_dot_1);
        dot2 = (ImageView) findViewById(R.id.image_dot_2);
        dot3 = (ImageView) findViewById(R.id.image_dot_3);
        dot4 = (ImageView) findViewById(R.id.image_dot_4);
        dots = new ImageView[4];
        dots[0] = dot1;
        dots[1] = dot2;
        dots[2] = dot3;
        dots[3] = dot4;
        tutorialPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());
        titleList = new ArrayList<>();
        descriptionList = new ArrayList<>();
        logoIdList = new ArrayList<>();
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
        for (int i = 0; i < 4; i++) {
            final int finalI = i;
            dots[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pager.setCurrentItem(finalI);
                }
            });
        }
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
    }

    /**
     * viewpager change listener
     */
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            setBottomDots(position);
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
        if (getIntent().getExtras() != null) {
            String intentData = (String) getIntent().getExtras().get("data_splash");
            if (intentData != null){
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("data_splash", intentData);
                startActivity(intent);
                finish();
            }else {
                intentData = (String) getIntent().getExtras().get("toast_message");
                if (intentData != null) {
                    Toast.makeText(WelcomeActivity.this, intentData, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }else {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    /**
     * return the current item of the pager adapter
     * @return position of the item
     */
    private int getItem() {
        return pager.getCurrentItem();
    }

    private class TutorialPagerAdapter extends FragmentPagerAdapter{

        private TutorialPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TutorialFragment.newInstance(titleList.get(position), descriptionList.get(position),
                    logoIdList.get(position));
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
