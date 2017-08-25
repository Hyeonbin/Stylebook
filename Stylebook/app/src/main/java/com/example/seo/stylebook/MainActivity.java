package com.example.seo.stylebook;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    int max = 4;
    Fragment cur_fragment = new Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.Viewpager_Main);
        viewPager.setAdapter(new adapter(getSupportFragmentManager()));

        LinearLayout main_tablist = (LinearLayout)findViewById(R.id.Sb_Tab_List);
        LinearLayout main_tablike = (LinearLayout)findViewById(R.id.Sb_Tab_Like);
        LinearLayout main_tabsearch = (LinearLayout)findViewById(R.id.Sb_Tab_Search);
        LinearLayout main_tabprofile = (LinearLayout)findViewById(R.id.Sb_Tab_Profile);

        main_tablist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });

        main_tablike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });

        main_tabsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(2);
            }
        });

        main_tabprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(3);
            }
        });

        // 뷰 페이져에 대한 페이지 체인지 리스너너
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            ImageView main_tablistimage = (ImageView)findViewById(R.id.Sb_Tab_Listimage);
            ImageView main_tablikeimage = (ImageView)findViewById(R.id.Sb_Tab_Likeimage);
            ImageView main_tabsearchimage = (ImageView)findViewById(R.id.Sb_Tab_Searchimage);
            ImageView main_tabprofileimage = (ImageView)findViewById(R.id.Sb_Tab_Profileimage);

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    main_tablistimage.setImageResource(R.drawable.newsfeed);
                    main_tablikeimage.setImageResource(R.drawable.like_emp);
                    main_tabsearchimage.setImageResource(R.drawable.tab_search3_emp);
                    main_tabprofileimage.setImageResource(R.drawable.icon_person_emp);
                } else if(position == 1) {
                    main_tablistimage.setImageResource(R.drawable.newsfeed_emp);
                    main_tablikeimage.setImageResource(R.drawable.like);
                    main_tabsearchimage.setImageResource(R.drawable.tab_search3_emp);
                    main_tabprofileimage.setImageResource(R.drawable.icon_person_emp);
                } else if(position == 2) {
                    main_tablistimage.setImageResource(R.drawable.newsfeed_emp);
                    main_tablikeimage.setImageResource(R.drawable.like_emp);
                    main_tabsearchimage.setImageResource(R.drawable.tab_search3);
                    main_tabprofileimage.setImageResource(R.drawable.icon_person_emp);
                } else {
                    main_tablistimage.setImageResource(R.drawable.newsfeed_emp);
                    main_tablikeimage.setImageResource(R.drawable.like_emp);
                    main_tabsearchimage.setImageResource(R.drawable.tab_search3_emp);
                    main_tabprofileimage.setImageResource(R.drawable.icon_person);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class adapter extends FragmentStatePagerAdapter {
        public adapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position){
            if(position < 0 || max<=position)
                return null;

            switch (position){
                case 0:
                    cur_fragment = new StyleListActivity();
                    break;
                case 1:
                    cur_fragment = new LikeActivity();
                    break;
                case 2:
                    cur_fragment = new SearchActivity();
                    break;
                case 3:
                    cur_fragment = new ProfileActivity();
                    break;
            }

            return cur_fragment;
        }

        @Override
        public int getCount() {
            return max;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
