package com.geisonquevedo.moscadasfrutas;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geisonquevedo.moscadasfrutas.utils.SaveBitmap;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imgHelp;
    TextView textHelp;
    Button btnIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        imgHelp = (ImageView) findViewById(R.id.imgHelp);
        textHelp = (TextView) findViewById(R.id.textHelp);

        btnIniciar = (Button) findViewById(R.id.btnIniciar);
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideButtons();
                /*Fragment objFragment = new CameraFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.relativeLayoutMainContent, objFragment).commit();*/

                Intent myIntent = new Intent(MainActivity.this, CameraActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("startIntend");
            if(value.equals("CameraFragnent")){
                hideButtons();
                Fragment objFragment = new CameraFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.relativeLayoutMainContent, objFragment).commit();
            }
        }

        SharedPreferences pref = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        if(pref.getInt("FirstTime", 1) == 1){
            SharedPreferences.Editor edit = pref.edit();

            edit.putInt("FirstTime", 2);
            edit.commit();

            File dir = new SaveBitmap().getDir();
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }
        }
    }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            hideButtons();

            Fragment objFragment = null;
            objFragment = new SettingsFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.relativeLayoutMainContent, objFragment).commit();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment objFragment = null;

        int id = item.getItemId();

        btnIniciar = (Button) findViewById(R.id.btnIniciar);

        hideButtons();

       /* if (id == R.id.nav_camara) {
            objFragment = new CameraFragment();
        } else*/ if (id == R.id.nav_camara_live) {
            Intent myIntent = new Intent(MainActivity.this, CameraActivity.class);
            MainActivity.this.startActivity(myIntent);
            return true;
        } else if (id == R.id.nav_gallery) {
            objFragment = new CartilhaFragment();
        } else if (id == R.id.nav_slideshow) {
            objFragment = new ClassifierFragment();
        } else if (id == R.id.nav_send) {
            objFragment = new CameraNativaFragment();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.relativeLayoutMainContent, objFragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void hideButtons() {
        imgHelp.setVisibility(View.INVISIBLE);
        btnIniciar.setVisibility(View.INVISIBLE);
        textHelp.setVisibility(View.INVISIBLE);
    }
}
