package com.dolphin.thegigisup.models;

import com.google.gson.annotations.Expose;

/**
 * A sitting object used to store selected seat information
 *
 * @author Team Dolphin 25/04/2015.
 */
public class Sitting {

    /**
     * Default sitting constructor
     */
    public Sitting() {}

    /**
     * @return Current sitting ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id New sitting ID to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return Current user ID booking this sitting
     */
    public Integer getUserId() {
        return userID;
    }

    /**
     * @param userID New user ID to set for the sitting
     */
    public void setUserId(Integer userID) {
        this.userID = userID;
    }

    /**
     * @return Current seat ID
     */
    public Integer getSeatId() {
        return seatID;
    }

    /**
     * @param seatID New seat ID to set for the sitting
     */
    public void setSeatId(Integer seatID) {
        this.seatID = seatID;
    }

    /**
     * @return Current booking ID linked to the sitting
     */
    public Integer getBookingId() {
        return bookingID;
    }

    /**
     * @param bookingID New booking ID to be linked to the sitting
     */
    public void setBookingId(Integer bookingID) {
        this.bookingID = bookingID;
    }

    /**
     * @return Current event ID linked to the sitting
     */
    public Integer getEventId() {
        return eventID;
    }

    /**
     * @param eventID New event ID linked to be set for the sitting
     */
    public void setEventId(Integer eventID) {
        this.eventID = eventID;
    }

    @Expose
    private Integer id;
    @Expose
    private Integer userID;
    @Expose
    private Integer seatID;
    @Expose
    private Integer bookingID;
    @Expose
    private Integer eventID;
}
