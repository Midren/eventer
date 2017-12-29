package ucu.in.ua.mapstest;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

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
        getLocation mGetLocation = new getLocation();
        mGetLocation.execute("Львів");
        try {
            position = mGetLocation.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,13));
        addEventMarker(new Event(1,"Виставкa \"Скарби династії Цін\"",new Date(151449840L*10000),"Палаццо Бандінеллі, пл. Ринок 2","Some description ---"),map);
        addEventMarker(new Event(2,"Виставка \"Сучасне польське мистецтво\"",new Date(151449840L*10000),"Музей скульптури І. Г. Пінзеля, пл. Митна, 2","Some description ---"),map);
        addEventMarker(new Event(3,"Репертуар Театру Лесі на грудень",new Date(151449840L*10000),"Театр імені Лесі Українки, вул. Городоцька, 36","Some description ---"),map);
        addEventMarker(new Event(4,"Репертуар Львівського театру ляльок на грудень",new Date(151449840L*10000),"Львівський театр ляльок, площа Данила Галицького, 1","Some description ---"),map);
        addEventMarker(new Event(6,"Фестиваль \"Святкуємо особливе Різдво у Львові\"",new Date(151449840L*10000),"Львів, площа Ринок, площа перед Львівською оперою","Some description ---"),map);
        addEventMarker(new Event(7,"Виставка \"Народний дереворит у художніх збірках Львова\"",new Date(0),"Музей етнографії та художнього промислу, пр. Свободи,15","Some description ---"),map);
        addEventMarker(new Event(8,"Виставка «Археологія автострад. Розкопки під час масштабного будівництва доріг біля Кракова»",new Date(151449840L*10000),"Музей етнографії та художнього промислу, пр. Свободи,15","Some description ---"),map);
        addEventMarker(new Event(9,"Палац Потоцьких — Зимова резиденція Львова",new Date(151449840L*10000),"Палац Потоцьких, вул. Коперника, 15","Some description ---"),map);

    }

    public void addEventMarker(Event event,GoogleMap map) {
        Log.v(TAG,"I created marker");
        LatLng position = Lviv;
        getLocation mGetLocation = new getLocation();
        mGetLocation.execute(event.getPlace());
        try {
            position = mGetLocation.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        map.addMarker(new MarkerOptions().position(position).title(event.getName()).snippet(event.getDate().toString()));
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

    class getLocation extends AsyncTask<String,Void,LatLng> {

        @Override
        protected LatLng doInBackground(String... sites) {
            double lng = 0, lat = 0;
            for (String site: sites) {
                Log.v(TAG,"Start async");
                String json = "";
                for (int i = 0; i < site.length(); i++) {
                    if(site.charAt(i) == ' '){
                        site = site.replace(' ','+');
                    }
                }
                try {
                    URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=");
                    String key = "&key=AIzaSyAuurSdVgrVBkMnDG7Z4Dur0TT5ZRY44-k";
                    url = new URL(url.toString()+site+key);
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
                JSONObject place = null;
                try {
                    place = new JSONObject(json);
                    lng = place.getJSONArray("results").optJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    lat = place.getJSONArray("results").optJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return new LatLng(lat,lng);
        }
    }
}

