package com.dolphin.thegigisup.models;

import com.google.gson.annotations.Expose;

/**
 * A booking object used to store booking information
 *
 * @author Team Dolphin 18/04/2015.
 */
public class Booking {

    @Expose
    private Integer id;
    @Expose
    private Integer userID;
    @Expose
    private Integer eventID;
    @Expose
    private String ticketRef;
    @Expose
    private Boolean isVIP;
    @Expose
    private String cardID;
    @Expose
    private Event event;

    public Booking() {}

    /**
     * @return Booking ID
     */
    public Integer getId() {return id;}

    /**
     * @param id Set the booking ID
     */
    public void setId(Integer id) {this.id = id;}

    /**
     * @return The User ID
     */
    public Integer getUserID() {return userID;}

    /**
     * @param userID Set the user ID
     */
    public void setUserID(Integer userID) {this.userID = userID;}

    /**
     * @return The event ID
     */
    public Integer getEventID() {return eventID;}

    /**
     * @param eventID Set the event ID
     */
    public void setEventID(Integer eventID) {this.eventID = eventID;}

    /**
     * @return The ticket reference string for the booking
     */
    public String getTicketRef() {return ticketRef;}

    /**
     * @param ticketRef Set the ticket reference string for the booking
     */
    public void setTicketRef(String ticketRef) {this.ticketRef = ticketRef;}

    /**
     * @return Boolean to check if the booking is VIP or not
     */
    public Boolean getVIP() {return isVIP;}

    /**
     * @param isVIP Set whether the booking is VIP
     */
    public void setVIP(Boolean isVIP) {this.isVIP = isVIP;}

    /**
     * @return The card ID for the booking
     */
    public String getCardID() {return cardID;}

    /**
     * @param cardID Set the card ID for the booking
     */
    public void setCardID(String cardID) {this.cardID = cardID;}

    /**
     * @return The event that the user booked
     */
    public Event getEvent() {return event;}

    /**
     * @param event Set the event the booking is for
     */
    public void setEvent(Event event) {this.event = event;}
}
