package ucu.in.ua.mapstest;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahdrew on 25.01.18.
 */

public class EventsArrayAdapter extends ArrayAdapter<Event> {

    List<Event> events;

    public EventsArrayAdapter(Context context, List<Event> events) {
        super(context, R.layout.event_badge, events);
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        View view = layoutInflater.inflate(R.layout.event_badge, parent, false);

        Event event = events.get(position);

        TextView name = (TextView) view.findViewById(R.id.eb_name);
        TextView date = (TextView) view.findViewById(R.id.eb_date);
        TextView place = (TextView) view.findViewById(R.id.eb_place);
        ImageView picture = (ImageView) view.findViewById(R.id.eb_picture);


        name.setText(event.getName());
        date.setText(event.getStart_time());
        place.setText(event.getPlace_name());

        Picasso.with(view.getContext()).load(event.getPicture_uri()).into(picture);
        picture.setVisibility(View.VISIBLE);


        return view;


    }
}

