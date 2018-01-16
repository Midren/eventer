package ucu.in.ua.mapstest;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    MapFragment fragMap;
    Fragment mFragment;
    Button mButtonMap;
    FragmentTransaction mFragmentTransaction;
    Boolean MapBtn = true;

    public static final LatLng Lviv = new LatLng(49.85,24.0166666667);
    public static final String TAG = "MAPTESTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragment = new Fragment1();
        mFragmentTransaction.add(R.id.map_fragment, mFragment);
        mFragmentTransaction.commit();

        fragMap = MapFragment.newInstance();
        Log.v(TAG,"I created map");

        mButtonMap = (Button) findViewById(R.id.btn_map);
        mButtonMap.setOnClickListener(this);

    }

    public void onClick(View v) {
        mFragmentTransaction = getFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.btn_map:
               if(MapBtn) {
                   mFragmentTransaction.remove(mFragment);
                   mFragmentTransaction.add(R.id.map_fragment, fragMap);
                   fragMap.getMapAsync(this);
                   mButtonMap = (Button) findViewById(R.id.btn_map);
                   mButtonMap.setText(R.string.remove_map);
                   Log.v(TAG, "I added map");
                   MapBtn = !MapBtn;
                   break;
               } else {
                   mFragmentTransaction.remove(fragMap);
                   mFragmentTransaction.add(R.id.map_fragment, mFragment);
                   mButtonMap = (Button) findViewById(R.id.btn_map);
                   mButtonMap.setText(R.string.add_map);
                   MapBtn = !MapBtn;
                   Log.v(TAG,"I removed map");
                   break;
               }
            default:
                break;
        }
        mFragmentTransaction.commit();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng position = Lviv;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,13));
        GetMarkers getMarkers = new GetMarkers();
        getMarkers.execute();
        ArrayList<Event> events = new ArrayList<Event>();
        try {
            events = getMarkers.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        for (Event event: events) {
            addEventMarker(event, map);
        }
    }

    public void addEventMarker(Event event,GoogleMap map) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(event.getPlace_lat(),event.getPlace_lng()))
                .title(event.getName()));
        marker.setTag(event.getId());
        map.setOnMarkerClickListener(this);
    }

    @Override
    protected void onPause() {
        mFragmentTransaction = getFragmentManager().beginTransaction();
        if(!MapBtn) {
            mFragmentTransaction.remove(fragMap);
        }
        Log.v(TAG,"Paused");
        mFragmentTransaction.commit();
        super.onPause();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer event_id = (Integer) marker.getTag();
        Bundle bundle = new Bundle();
        bundle.putInt("Id",event_id);
        DialogFragment eventDialog = new EventDialog();
        eventDialog.setArguments(bundle);
        eventDialog.show(getFragmentManager(), "EventDialog");
        return true;
    }

    class GetMarkers extends AsyncTask<Void,Void,ArrayList<Event>>{

        @Override
        protected ArrayList<Event> doInBackground(Void... voids) {
            Log.v(TAG,"Start async");
            String json = "";
            try {
                URL url = new URL("http://382809ce.ngrok.io/get_events_short");
                Log.v(TAG,"try to connect");
                Scanner sc = new Scanner(url.openConnection().getInputStream());
                Log.v(TAG,"connected");
                while(sc.hasNextLine()) {
                    json += sc.nextLine() + '\n';
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<Event> events = new ArrayList<Event>();
            try {
                JSONArray json_events = new JSONObject(json).getJSONArray("events");
                for(int i = 0; i < json_events.length(); i++) {
                    JSONObject event = json_events.optJSONObject(i);
                    events.add(new Event(event.getInt("id"),event.getString("name"),event.getDouble("latitude"),event.getDouble("longitude")));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return events;
        }

    }
}

