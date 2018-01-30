package ucu.in.ua.mapstest;

/**
 * Created by ahdrew on 25.01.18.
 */

public class Event {
    private int id;
    private String name;
    private String description;
    private int attending_count;
    private int maybe_count;
    private String ticket_uri;
    private String start_time;
    private String picture_uri;
    private String place_name;
    private double place_lat;
    private double place_lng;

    public double getPlace_lat() {
        return place_lat;
    }

    public String getDescription() {
        return description;
    }

    public int getAttending_count() {
        return attending_count;
    }

    public String getTicket_uri() {
        return ticket_uri;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getPlace_name() {
        return place_name;
    }

    public double getPlace_lng() {
        return place_lng;
    }

    public int getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public String getPicture_uri() {
        return picture_uri;
    }

    public Event(int id, String name, double place_lat, double place_lng) {
        super();
        this.id = id;
        this.name = name;

        this.place_lat = place_lat;
        this.place_lng = place_lng;
    }

    public Event(int id, String name, String description, int attending_count, String start_time, String ticket_uri, String place_name, String picture) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.attending_count = attending_count;
        this.start_time = start_time;
        this.ticket_uri = ticket_uri;
        this.place_name = place_name;
        this.picture_uri = picture;
    }

    public Event(int id, String name, String start_time, String picture) {
        this.id = id;
        this.name = name;
        this.start_time = start_time;
        this.picture_uri = picture;
    }
}
