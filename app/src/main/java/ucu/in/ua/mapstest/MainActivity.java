package ucu.in.ua.mapstest;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.util.Log;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
        //ТУТ мій пробний коментар
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
        addMarkers(map);
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

    public void addMarkers(final GoogleMap map) {
        String url = "http://36e25c92.ngrok.io/get_events_short";
        Log.v(TAG,"try to connect");
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(TAG,"connected");
                JSONArray json_events = null;
                try {
                    json_events = response.getJSONArray("events");
                    for(int i = 0; i < json_events.length(); i++) {
                        JSONObject event = json_events.optJSONObject(i);
                        addEventMarker(new Event(event.getInt("id"),event.getString("name"),event.getDouble("latitude"),event.getDouble("longitude")),map);
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


}

