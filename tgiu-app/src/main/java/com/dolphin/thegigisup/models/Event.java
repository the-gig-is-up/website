package com.dolphin.thegigisup.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * An event object used to store event information. It is parcelable for use in
 * passing across pages in the app
 *
 * @author Team Dolphin 10/03/15.
 */
public class Event implements Parcelable {

    @Expose
    private Integer id;
    @Expose
    private Integer venueID;
    @Expose
    private String eventName;
    @Expose
    private String price;
    @Expose
    private String date;
    @Expose
    private String startTime;
    @Expose
    private String endTime;
    @Expose
    private String originalImage;
    @Expose
    private String savedImage;
    @Expose
    private int ticketsLeft;
    @Expose
    private String description;
    @Expose
    private Venue venue;
    @Expose
    private List<Artist> artists;

    /**
     * @return The Event ID
     */
    public Integer getId() {return id;}

    /**
     * @param id Set the Event ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The venue ID
     */
    public Integer getVenueID() {
        return venueID;
    }

    /**
     * @param venue Set the venue for the event
     */
    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    /**
     * @return The venue associated with the event
     */
    public Venue getVenue() {
        return venue;
    }

    /**
     * @param venueID Set the venue ID
     */
    public void setVenueID(Integer venueID) {
        this.venueID = venueID;
    }

    /**
     * @return The name string of the event
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @param eventName Set the name of the event
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * @return The date of the event in dateTime format
     */
    public DateTime getDate() {
        DateTime theDate = DateTime.parse(date,
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        return theDate;
    }

    /**
     * @param date Set the date using a string in the correct format
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return The price of the event
     */
    public String getPrice() {
        return price;
    }

    /**
     * @param price Set the price of the event
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * @return The event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description Set the event description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The start time of the event in hours and minutes
     */
    public String getStartTime() {
        return hoursAndMinutesOnly(startTime);
    }

    /**
     * @param startTime Set the start time for the event
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return The end time in hours and minutes
     */
    public String getEndTime() {return hoursAndMinutesOnly(endTime);}

    /**
     * @param endTime Set the end time of the event
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return The original start time string
     */
    public String getOriginalStartTime() {
        return startTime;
    }

    /**
     * @return The original end time string
     */
    public String getOriginalEndTime() {
        return endTime;
    }

    /**
     * @return The original image of the event
     */
    public String getOriginalImage() {
        return originalImage;
    }

    /**
     * @param originalImage Set the original image of the event
     */
    public void setOriginalImage(String originalImage) {
        this.originalImage = originalImage;
    }

    /**
     * @return The event's saved image
     */
    public String getSavedImage() {
        return savedImage;
    }

    /**
     * @param savedImage Set the event's saved image
     */
    public void setSavedImage(String savedImage) {
        this.savedImage = savedImage;
    }

    /**
     * @return The number of tickets left at the event
     */
    public int getTicketsLeft() {return ticketsLeft;}

    /**
     * @param ticketsLeft Set the tickets left for the event
     */
    public void setTicketsLeft(int ticketsLeft) {
        this.ticketsLeft = ticketsLeft;
    }

    /**
     * @return A list of artists playing at the event
     */
    public List<Artist> getArtists() {return artists;}

    /**
     * @param artists Set the list of artists playing at the event
     */
    public void setArtists(List<Artist> artists)  {this.artists =  artists;}

    /**
     * A method changing a time string into hours and minutes representation
     *
     * @param time A time string
     * @return A time string in hours and minutes format
     */
    private String hoursAndMinutesOnly(String time) {
        String first5CharsOfTime =
                time.substring(0, Math.min(time.length(), 5));
        return first5CharsOfTime;
    }

    /**
     * A method used to check if the event has a low ticket number
     *
     * @return A boolean to tell if the event has a low ticket number or not
     */
    public boolean isLowTickets() {
        float capacity = (float) (getVenue().getCapacity()/20);
        return (float) getTicketsLeft() < capacity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Parcelable method to write the variables to the parcel destination
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.venueID);
        dest.writeString(this.eventName);
        dest.writeString(this.price);
        dest.writeString(this.date);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.originalImage);
        dest.writeString(this.savedImage);
        dest.writeInt(this.ticketsLeft);
        dest.writeString(this.description);
        dest.writeParcelable(this.venue, 0);
        dest.writeTypedList(artists);
    }

    public Event() {
    }

    // Implement other parcelable methods to make event parcelable
    private Event(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.venueID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.eventName = in.readString();
        this.price = in.readString();
        this.date = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.originalImage = in.readString();
        this.savedImage = in.readString();
        this.ticketsLeft = in.readInt();
        this.description = in.readString();
        this.venue = in.readParcelable(Venue.class.getClassLoader());
        List<Artist> temp = new ArrayList<>();
        in.readTypedList(temp, Artist.CREATOR);
        setArtists(temp);
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}