package ucu.in.ua.mapstest;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //Defining Variables
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    //Defining Fragments
    private MapFragment mFragmentMap;
    private Fragment mFragmentStart;
    private Fragment mFragmentSettings;
    private Fragment mFragmentEvent;
    private Fragment mFragmentProfile;
    FragmentTransaction mFragmentTransaction;
    Boolean MapBtn = true;


    public static final LatLng Lviv = new LatLng(49.85, 24.0166666667);
    public static final String TAG = "MAPTESTS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        //проініціалізувати всі фрагменти
        mFragmentMap = MapFragment.newInstance();
        mFragmentStart = new FragmentStart();
        mFragmentEvent = new FragmentEvent();
        mFragmentSettings = new FragmentSettings();
        mFragmentProfile = new FragmentProfile();
        //ТУТ мій пробний коментар
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.container, mFragmentStart);
        mFragmentTransaction.commit();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView navUsername = (TextView) headerView.findViewById(R.id.username_text);
            navUsername.setText(account.getDisplayName());
            TextView navEmail = (TextView) headerView.findViewById(R.id.email_text);
            navEmail.setText(account.getEmail());
            ImageView navPicture = (ImageView) headerView.findViewById(R.id.nav_picture);
            Picasso.with(navPicture.getContext()).load(account.getPhotoUrl()).transform(new BorderedCircleTransformation()).into(navPicture);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (id) {
            case R.id.nav_settings:
                Log.v(TAG, "settings ");
                fragmentTransaction.replace(R.id.container, mFragmentSettings);
                break;
            case R.id.nav_map:
                Log.v(TAG, "map ");
                fragmentTransaction.replace(R.id.container, mFragmentMap);
                mFragmentMap.getMapAsync(this);
                break;
            case R.id.nav_events:
                Log.v(TAG, "events ");
                fragmentTransaction.replace(R.id.container, mFragmentEvent);
                break;
            case R.id.nav_profile:
                Log.v(TAG,"profile ");
                fragmentTransaction.replace(R.id.container, mFragmentProfile);
                break;
        }
        Log.v(TAG, "tranz done");
        fragmentTransaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer event_id = (Integer) marker.getTag();
        Bundle bundle = new Bundle();
        bundle.putInt("Id", event_id);
        DialogFragment eventDialog = new EventDialog();
        eventDialog.setArguments(bundle);
        eventDialog.show(getFragmentManager(), "EventDialog");
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng position = Lviv;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
        addMarkers(googleMap);
    }


    //Created by us
    public void addMarkers(final GoogleMap map) {
        String url = "https://dc49c5ec.ngrok.io/get_events_short";
        Log.v(TAG, "try to connect1");
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(TAG, "connected");
                JSONArray json_events = null;
                try {
                    json_events = response.getJSONArray("events");
                    for (int i = 0; i < json_events.length(); i++) {
                        JSONObject event = json_events.optJSONObject(i);
                        addEventMarker(new Event(event.getInt("id"), event.getString("name"), event.getDouble("latitude"), event.getDouble("longitude")), map);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO Auto-generated method
            }
        });
        Volley.newRequestQueue(this).add(jsonRequest);

    }

    public void addEventMarker(Event event, GoogleMap map) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(event.getPlace_lat(), event.getPlace_lng()))
                .title(event.getName()));
        marker.setTag(event.getId());

        map.setOnMarkerClickListener(this);
    }

}
