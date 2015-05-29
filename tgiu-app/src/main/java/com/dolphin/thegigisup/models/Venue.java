package com.dolphin.thegigisup.models;

/**
 * Venue object to hold venue information
 *
 * @author Team Dolphin 11/03/15.
 */

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Venue implements Parcelable {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String location;
    @Expose
    private Integer capacity;
    @Expose
    private String address;
    @Expose
    private String postcode;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @Expose
    private List<Seat> seats;

    /**
     * @return Current venue ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id New venue ID to be set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return Current venue name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name New name to set for the venue
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Current venue location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location New location to be set for the venue
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return Current venue capacity
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * @param capacity New venue capacity to be set
     */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * @return Current venue address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address New venue address to be set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return Current postcode for the venue
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * @param postcode New venue postcode to be set
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * @return Current phoneNumber for the venue
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber New phone number to set to the venue
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Method to write venue to a parcelable object
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.location);
        dest.writeValue(this.capacity);
        dest.writeString(this.address);
        dest.writeString(this.postcode);
        dest.writeString(this.phoneNumber);
    }

    public Venue() {
    }

    /**
     * Parcelable constructor to read in the parcelable information
     * @param in Parcel input
     */
    private Venue(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.location = in.readString();
        this.capacity = (Integer) in.readValue(Integer.class.getClassLoader());
        this.address = in.readString();
        this.postcode = in.readString();
        this.phoneNumber = in.readString();
    }

    public static final Parcelable.Creator<Venue> CREATOR =
                                            new Parcelable.Creator<Venue>() {
        public Venue createFromParcel(Parcel source) {
            return new Venue(source);
        }

        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };

    /**
     * @return A current list of seat objects for the venue
     */
    public List<Seat> getSeats() {
        return seats;
    }

    /**
     * @param seats A new list of seat objects to set to the venue
     */
    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
