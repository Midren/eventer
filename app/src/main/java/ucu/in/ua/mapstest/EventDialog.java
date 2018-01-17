package ucu.in.ua.mapstest;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class EventDialog extends DialogFragment {
    TextView name;
    TextView desc;
    TextView ticket_uri;
    TextView start_time;
    TextView place_name;
    ImageView picture;

    public static final String TAG = "MAPTESTS";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_dialog,null);
        Integer event_id = getArguments().getInt("Id");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Event event = getEvent(event_id, v, getContext());
        return v;
    }

    public Event getEvent(final Integer id, final View v, final Context context) {
        String url = "http://b8b446f8.ngrok.io/get_event_full/" + id.toString();
        Log.v(TAG,"try to connect");
        final Event[] event = new Event[1];
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject event_json = response.getJSONObject("event");

                    event[0] = new Event(id,event_json.getString("name"),
                            event_json.getString("description"),
                            event_json.getInt("attending_count"),
                            event_json.getString("start_time"),
                            event_json.getString("ticket_uri"),
                            event_json.getString("place"),
                            event_json.getString("picture"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                name = (TextView)v.findViewById(R.id.e_name);
                name.setText(event[0].getName());
                desc = (TextView)v.findViewById(R.id.e_desc);
                desc.setText(event[0].getDescription());
                ticket_uri = (TextView)v.findViewById(R.id.e_ticket_uri);
                if(event[0].getTicket_uri().length() > 0) {
                    ticket_uri.setText("Квитки:\n" + event[0].getTicket_uri());
                }
                place_name = (TextView)v.findViewById(R.id.e_place_name);
                place_name.setText("Місце проведення:\n" + event[0].getPlace_name());

                start_time = (TextView)v.findViewById(R.id.e_start_time);
                start_time.setText("Час проведення:\n" + event[0].getStart_time());

                picture = (ImageView)v.findViewById(R.id.e_picture);
                Picasso.with(context).load(event[0].getPicture_uri()).into(picture);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO Auto-generated method
            }
        });
        Volley.newRequestQueue(getActivity().getApplicationContext()).add(jsonRequest);
        return event[0];
    }
}
