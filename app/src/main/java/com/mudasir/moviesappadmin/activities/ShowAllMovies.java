package com.mudasir.moviesappadmin.activities;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.mudasir.moviesappadmin.R;
import com.mudasir.moviesappadmin.adapters.ViewPagerAdapter;
import com.mudasir.moviesappadmin.fragments.ActionMoviesFragment;
import com.mudasir.moviesappadmin.fragments.AdventureFragment;
import com.mudasir.moviesappadmin.fragments.ComedyFragment;
import com.mudasir.moviesappadmin.fragments.LoveMoviesFragment;

public class ShowAllMovies extends AppCompatActivity {

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_movies);


        mToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Movies App Admin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        ViewPagerAdapter sectionsPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.addFragment("Love",new LoveMoviesFragment());
        sectionsPagerAdapter.addFragment("Action",new ActionMoviesFragment());
        sectionsPagerAdapter.addFragment("Adventure",new AdventureFragment());
        sectionsPagerAdapter.addFragment("Comedy",new ComedyFragment());

        ViewPager viewPager = findViewById(R.id.view_pager);

        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


    }
}