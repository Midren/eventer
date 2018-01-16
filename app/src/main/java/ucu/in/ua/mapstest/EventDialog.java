package ucu.in.ua.mapstest;

import android.app.DialogFragment;
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
        GetEvent getEvent = new GetEvent();
        getEvent.execute(event_id);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Event event = null;
        try {
            event = getEvent.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        name = (TextView)v.findViewById(R.id.e_name);
        name.setText(event.getName());
        desc = (TextView)v.findViewById(R.id.e_desc);
        desc.setText(event.getDescription());
        ticket_uri = (TextView)v.findViewById(R.id.e_ticket_uri);
        if(event.getTicket_uri().length() > 0) {
            ticket_uri.setText("Квитки:\n" + event.getTicket_uri());
        }
        place_name = (TextView)v.findViewById(R.id.e_place_name);
        place_name.setText("Місце проведення:\n" + event.getPlace_name());

        start_time = (TextView)v.findViewById(R.id.e_start_time);
        start_time.setText("Час проведення:\n" + event.getStart_time());

        picture = (ImageView)v.findViewById(R.id.e_picture);
        Picasso.with(getContext()).load(event.getPicture_uri()).into(picture);

        return v;
    }

    class GetEvent extends AsyncTask<Integer,Void,Event> {

        @Override
        protected Event doInBackground(Integer... integers) {
            String json = "";
            Event event = null;
            try {
                URL url = new URL("http://382809ce.ngrok.io/get_event_full/"+integers[0].toString());
                Scanner sc = new Scanner(url.openConnection().getInputStream());
                while(sc.hasNextLine()) {
                    json += sc.nextLine() + '\n';
                }
                JSONObject event_json = new JSONObject(json).getJSONObject("event");

                event = new Event(integers[0],event_json.getString("name"),
                        event_json.getString("description"),
                        event_json.getInt("attending_count"),
                        event_json.getString("start_time"),
                        event_json.getString("ticket_uri"),
                        event_json.getString("place"),
                        event_json.getString("picture"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return event;
        }

    }
}
