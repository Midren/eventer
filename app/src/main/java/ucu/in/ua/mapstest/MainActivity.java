package ucu.in.ua.mapstest;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback{
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
                   mFragmentTransaction.add(R.id.map_fragment, fragMap);
                   fragMap.getMapAsync(this);
                   mButtonMap = (Button) findViewById(R.id.btn_map);
                   mButtonMap.setText(R.string.remove_map);
                   Log.v(TAG, "I added map");
                   MapBtn = !MapBtn;
                   break;
               } else {
                   mFragmentTransaction.remove(fragMap);
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

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Lviv,10));
        Log.v(TAG,"I created marker");
        map.addMarker(new MarkerOptions().position(Lviv).title("Lviv"));

    }
}
