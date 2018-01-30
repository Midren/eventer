package ucu.in.ua.mapstest;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FragmentEvent extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_event, container, false);
        //завантаження подій
        Log.v("MAPTEST", "go1");

        String url = "http://56ab65a1.ngrok.io/get_events_menu";
        final List<Event> temp = new ArrayList<Event>();

        Log.v("MAPTEST", "go2");
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json_events = null;
                try {
                    Log.v("MAPTEST", "go3");
                    json_events = response.getJSONArray("events");
                    for (int i = 0; i < json_events.length(); i++) {
                        JSONObject event = json_events.optJSONObject(i);
                        temp.add(new Event(
                                event.getInt("id"),
                                event.getString("name"),
                                event.getString("start_time"),
                                event.getString("picture"))
                        );
                    }
                    ListAdapter adapter = new EventsArrayAdapter(view.getContext(), temp);
                    ListView listView = (ListView) view.findViewById(R.id.lv_event);
                    listView.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(view.getContext()).add(jsonRequest);
        Log.v("MAPTEST", "go4");


        // Inflate the layout for this fragment
        return view;
    }


}
