package ucu.in.ua.mapstest;

public class Event {
    private int id;
    private String name;
    private java.sql.Date date;
    private String place;
    private String description;

    public Event(int id, String name, java.sql.Date date, String place, String description){
        super();
        this.id = id;
        this.name = name;
        this.date = date;
        this.place = place;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String toString() {
        return "Event : " + name + "\n" + "Date: " + date + "\n" + "Place: " + place + "\n" + "Description: " + description + "\n";

    }
}
