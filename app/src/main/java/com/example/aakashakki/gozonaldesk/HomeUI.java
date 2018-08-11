package com.example.aakashakki.gozonaldesk;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.webianks.easy_feedback.EasyFeedback;

import java.io.IOException;
import java.util.List;

public class HomeUI extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private long exitTime = 0;
    ImageView profilepicture ;
    TextView displayname;
    TextView mail;
    private DatabaseReference mDatabase;
    LocationManager locationManager;
    public  static  final  int REQUEST_CALL=1;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Home");
        setContentView(R.layout.activity_home_ui);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(HomeUI.this, PhoneUI.class));
                    finish();
                }
            }
        };
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        profilepicture = (ImageView) header.findViewById(R.id.profile);
        displayname = (TextView) header.findViewById(R.id.nameUSER);
        mail = (TextView)header.findViewById(R.id.PhoneorMail);

        String mail_id = mAuth.getCurrentUser().getPhoneNumber();
       // String user_id = mAuth.getCurrentUser().getDisplayName();

        profilepicture.setImageResource(R.drawable.biztimelogo);
        displayname.setText("ZonalDesk");
        mail.setText("PhoneNo: "+mail_id);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                     else{
            //            Snackbar.make(findViewById(android.R.id.content), "permission denied",
            //                    Snackbar.LENGTH_SHORT).show();
            //        }                     int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        //   ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);

        }
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                  //  Bundle bundle = getIntent().getExtras();
                    //final    String stuff = bundle.getString("stuff");
                    String uid = mAuth.getCurrentUser().getUid();
                    try {
                        mDatabase.child("users").child(uid).child("latitude").setValue(lat);
                        mDatabase.child("users").child(uid).child("longitude").setValue(lon);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }



                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    String uid = mAuth.getCurrentUser().getUid();
                    try {
                        mDatabase.child("users").child(uid).child("latitude").setValue(lat);
                       mDatabase.child("users").child(uid).child("longitude").setValue(lon);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        else{
            showSettingsAlert();

        }



        Fragment fragment = null;


        fragment = new Home();
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.title_text, fragment);
            ft.commit();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

                doExitApp();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_ui, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//noinspection SimplifiableIfStatement
        if (id == R.id.call_action) {
          //  Toast.makeText(HomeUI.this,"Call will be connected",Toast.LENGTH_SHORT).show();
            makePhoneCall();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mAuth.signOut();
            Toast.makeText(HomeUI.this,"signout was done",Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }

    private void makePhoneCall() {
        String AdminNumber ="+918074878950";
        if(ContextCompat.checkSelfPermission(HomeUI.this,android.Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
    ActivityCompat.requestPermissions(HomeUI.this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
        }
        else{
            String dial ="tel:"+AdminNumber;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode ==REQUEST_CALL){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                makePhoneCall();

            }
            else{
                Toast.makeText(HomeUI.this,"Permission Denied",Toast.LENGTH_SHORT).show();

            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
       int id = item.getItemId();
       /* Intent intent = null;
        switch (item.getItemId()) {
            case R.id.service_estimate:
          //      intent = new Intent(this, DefaultActivity.class);
                break;
            case R.id.service_request:
           //     intent = new Intent(this, ColoredActivity.class);
                break;
            case R.id.call_history:
            //    intent = new Intent(this, VoiceActivity.class);
                break;
            case R.id.service_status:
            //    intent = new Intent(this, StickyActivity.class);
                break;
            case R.id.chat_zonaldesk:
            //    intent = new Intent(this, TabActivity.class);
                break;
            case R.id.home:
            //    intent = new Intent(this, InputTypeActivity.class);
                break;
            case R.id.nav_share:
                try {
                    ShareCompat.IntentBuilder.from(HomeUI.this)
                            .setType("text/plain")
                            .setChooserTitle("Chooser title")
                            .setText("http://play.google.com/store/apps/details?id=" + getApplication().getPackageName())
                            .startChooser();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.feedback:
                intent = new Intent(this, Feedback.class);

                break;
        }
        if (intent != null) {
            startActivity(intent);
            finish();
        }*/

        Fragment fragment = null;

        Bundle bundle = new Bundle();
        if (id == R.id.service_estimate) {
//            Toast.makeText(HomeUI.this,"Service Estimate",Toast.LENGTH_SHORT).show();

            fragment = new EstimateService();
        } else if (id == R.id.service_request) {
//            Toast.makeText(HomeUI.this,"Service request",Toast.LENGTH_SHORT).show();
            fragment = new RequestService();

        }  else if (id == R.id.call_history) {
//            Toast.makeText(HomeUI.this,"Call History",Toast.LENGTH_SHORT).show();
            fragment = new CallHistory();


        } else if (id == R.id.service_status) {
//            Toast.makeText(HomeUI.this,"Service Status",Toast.LENGTH_SHORT).show();
            fragment = new ServiceStatus();


        } else if (id == R.id.chat_zonaldesk) {
//            Toast.makeText(HomeUI.this,"chat with admin",Toast.LENGTH_SHORT).show();
            fragment = new ChatZonaldesk();

        }
        else if (id == R.id.home) {
            fragment = new Home();

        }
        else if (id == R.id.nav_share) {
            try {
                ShareCompat.IntentBuilder.from(HomeUI.this)
                        .setType("text/plain")
                        .setChooserTitle("Chooser title")
                        .setText("http://play.google.com/store/apps/details?id=" + getApplication().getPackageName())
                        .startChooser();
            } catch(Exception e) {
                e.printStackTrace();
            }


        }
        else if (id == R.id.feedback) {
            new EasyFeedback.Builder(this)
                    .withEmail("aakashakki47@gmail.com")
                    .withSystemInfo()
                    .build()
                    .start();

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.title_text, fragment);
            ft.commit();
        }
/*        Fragment fragment = null;
        Class fragmentClass = null;
        switch(item.getItemId()) {
            case R.id.call_history:
                fragmentClass = CallHistory.class;
                break;
            case R.id.service_request:
               // fragmentClass = SecondFragment.class;
                break;
            case R.id.service_estimate:
               // fragmentClass = ThirdFragment.class;
                break;
            case R.id.home:
                // fragmentClass = ThirdFragment.class;
                break;
            case R.id.chat_zonaldesk:
                // fragmentClass = ThirdFragment.class;
                break;
            case R.id.service_status:
                // fragmentClass = ThirdFragment.class;
                break;

            case R.id.feedback:
                new EasyFeedback.Builder(this)
                        .withEmail("aakashakki47@gmail.com")
                        .withSystemInfo()
                        .build()
                        .start();                break;
            case R.id.nav_share:
                try {
                    ShareCompat.IntentBuilder.from(HomeUI.this)
                            .setType("text/plain")
                            .setChooserTitle("Chooser title")
                            .setText("http://play.google.com/store/apps/details?id=" + getApplication().getPackageName())
                            .startChooser();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                fragmentClass = CallHistory.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.title_text, fragment).commit();
*/
        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);

        // Set action bar title
       // if(item.getItemId()!=R.id.feedback&&item.getItemId()!=R.id.nav_share){        setTitle(item.getTitle());
      //  }
        // Close the navigation drawer



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);


        alertDialog.setTitle("GPS is not Enabled!");

        alertDialog.setMessage("Agree to our terms and conditions for Better Performance  Turn on GPS");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();


            }
        });


        alertDialog.show();
    }
    /*
   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 &&grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            }
            }    }*/


    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
           finish();
        }
    }

}

