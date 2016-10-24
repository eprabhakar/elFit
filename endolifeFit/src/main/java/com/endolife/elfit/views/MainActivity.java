package com.endolife.elfit.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.endolife.elfit.R;
import com.endolife.elfit.auth.GoogleAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.mContext = getApplicationContext();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        findViewById(R.id.card_csview).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.card_acview).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.card_ahview).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.card_scview).setOnClickListener(mGlobal_OnClickListener);
       // findViewById(R.id.card_shview).setOnClickListener(mGlobal_OnClickListener);
       // findViewById(R.id.card_tlview).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.card_dayview).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.card_tcview).setOnClickListener(mGlobal_OnClickListener);
    }

    //Global On click listener for all views
    final View.OnClickListener mGlobal_OnClickListener = new View.OnClickListener() {
        Intent mIntent;
        public void onClick(final View v) {
            switch(v.getId()) {
                case R.id.card_csview: {
                    mIntent = new Intent(mContext, CheckSensorsActivity.class);
                    startActivity(mIntent);
                    break;
                }
                case R.id.card_acview: {
                    mIntent = new Intent(mContext, CustomAlgoResultsActivity.class);
                    startActivity(mIntent);
                    break;
                }
                case R.id.card_ahview: {
                    mIntent = new Intent(mContext, CustomAlgoHistoryActivity.class);
                    startActivity(mIntent);
                    break;
                }
                case R.id.card_scview:{
                    mIntent = new Intent(mContext, StepsCounterActivity.class);
                    startActivity(mIntent);
                    break;
                }
                /*
                case R.id.card_shview: {
                    mIntent = new Intent(mContext, StepsHistoryActivity.class);
                    startActivity(mIntent);
                    break;
                }

                case R.id.card_tlview: {
                    mIntent = new Intent(mContext, TimeLine.class);
                    startActivity(mIntent);
                    break;
                }*/
                case R.id.card_dayview:{
                    mIntent = new Intent(mContext,DayView.class);
                    startActivity(mIntent);
                    break;
                }

                case R.id.card_tcview: {
                    mIntent = new Intent(mContext, TimeChart.class);
                    startActivity(mIntent);
                    break;
                }
                default:
                     break;

            }
        }
    };
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
/*
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else
  */
        if (id == R.id.check_sensors){
            Intent mIntent = new Intent(this, CheckSensorsActivity.class);
            startActivity(mIntent);

        } else if (id == R.id.custom_accelerometer){
            Intent mIntent = new Intent(this, CustomAlgoResultsActivity.class);
            startActivity(mIntent);

        } else if (id == R.id.history_accelerometer){
            Intent mIntent = new Intent(this, CustomAlgoHistoryActivity.class);
            startActivity(mIntent);

        }  else if (id == R.id.steps_counter){
            Intent mIntent = new Intent(this, StepsCounterActivity.class);
            startActivity(mIntent);

        } else if (id == R.id.history_steps){
            Intent mIntent = new Intent(this, StepsHistoryActivity.class);
            startActivity(mIntent);

        } else if (id == R.id.google_signin){
            Intent mIntent = new Intent(this, GoogleAuth.class);
            startActivity(mIntent);

        }

        /*
        else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
