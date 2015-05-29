package com.dolphin.thegigisup.api;

import com.dolphin.thegigisup.fragments.FeaturedFragment;
import com.dolphin.thegigisup.models.*;
import retrofit.client.Response;
import retrofit.http.*;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * REST API definition
 */
public interface ServiceInterface {

    /**
     * Gets one event
     * @param id The id of the event to get
     * @return An Event
     */
    @GET("/events/{id}")
    public Event getEvent (@Path("id") int id);

    /**
     * Gets a list of Events
     * @return A list of Events
     */
    @GET("/events?filter[include]=artists" +
            "&filter[include]=venue")
    public List<Event> getEvents();


    /**
     * Get upcoming Events.
     * @param startDate The start date
     * @param numEvents The number of events to initially get
     * @return List of upcoming Events
     */
    @GET("/events?filter[include]=artists" +
            "&filter[include]=venue" +
            "&filter[order]=date%20ASC" +
            "&filter[where][ticketsLeft][gt]=0")
    public Observable<ArrayList<Event>> getUpcomingEvents(
            @Query("filter[where][date][gt]") String startDate,
            @Query("filter[limit]") int numEvents
    );


    /**
     * Get a list of Events starting with an offset
     * @param startDate The starting date
     * @param numEvents Number of Events to get
     * @param offset The starting point
     * @return List of upcoming Events
     */
    @GET("/events?filter[include]=artists" +
            "&filter[include]=venue" +
            "&filter[order]=date%20ASC" +
            "&filter[where][ticketsLeft][gt]=0")
    public List<Event> getUpcomingEventsWithOffset(
            @Query("filter[where][date][gt]") String startDate,
            @Query("filter[limit]") int numEvents,
            @Query("filter[skip]") int offset
    );

    /**
     * Gets a list of events which are close to running out
     * @return A list of hot Events which are currently
     *          coupled with FeaturedFragment.java
     */
    @GET("/events/hotevents")
    public Observable<FeaturedFragment.Hot> getHotEvents();


    /**
     * Allows for searching of Events
     * @param startDate Starting date
     * @param searchQuery The search query to perform
     * @param numEvents The number of Events to get
     * @return A list of searched Events
     */
    @GET("/events?filter[include]=venue" +
            "&filter[order]=date%20ASC" +
            "&filter[include]=artists")
    public List<Event> searchForEvents(
            @Query("filter[where][date][gt]") String startDate,
            @Query("filter[where][eventName][like]") String searchQuery,
            @Query("filter[limit]") int numEvents
    );


    /**
     * Login a user
     * @param loginDetails Details of user as body
     * @return A login confirmation
     */
    @POST("/users/login?[include]=user")
    public LoginConfirm loginUser(@Body LoginDetails loginDetails);

    /**
     * Logs a user out
     * @param accessToken The access token
     * @return Response of the logout (success or failure)
     */
    @POST("/users/logout")
    public Response logoutUser(@Query("access_token") String accessToken);


    /**
     * Signs a user to the system
     * @param user The details of the User
     * @return Response of the signup
     */
    @POST("/users")
    public Response signUpUser(@Body User user);


    /**
     * Allows for updating of user info
     * @param userID The user Id of the user to update
     * @param accessToken The access token
     * @param update What part of the JSON to update
     * @return Response of the updating
     */
    @PUT("/users/{id}")
    public Response updateUserInfo(
            @Path("id") int userID,
            @Query("access_token") String accessToken,
            @Body String update
    );

    /**
     * Get a list of cards
     * @param userID The user ID
     * @return List of cards
     */
    @GET("/cards")
    public List<Card> getCardsForUser(
            @Query("filter[where][userID]") int userID
    );

    /**
     * Add a card to the database
     * @param cardDetails A user's card details
     * @return Success or failure
     */
    @POST("/cards")
    public Response addUserCard(@Body Card cardDetails);


    /**
     * Remove a user card from the database
     * @param cardID The card ID to be removed
     * @param userID The user ID for the card
     * @return Success or failure
     */
    @DELETE("/cards/{id}")
    public Response removeUserCard(
           @Path("id") String cardID,
           @Query("filter[where][userID]") int userID
    );

    /**
     * Get a list of all the people sitting at an Event
     * @param eventID The Event's ID
     * @return List of people Sitting on which Seat at
     * an Event
     */
    @GET("/sittings")
    public Observable<ArrayList<Sitting>> getTakenSeats(
            @Query("filter[where][eventID]") int eventID

    );

    /**
     * Get a list of the Seats at a Venue
     * @param venueID Venue ID
     * @param typeID The type of Seat (1, 2, 3, 4, 5)
     * @return A list of the Seats
     */
    @GET("/venues/{id}/seats")
    public Observable<ArrayList<Seat>> getSeatsAtVenue (
            @Path("id") int venueID,
            @Query("&filter[where][typeID]") int typeID
    );

    /**
     * Update or add a booking to the database
     * @param booking The booking that needs adding
     * @return Success or failure codes
     */
    @PUT("/bookings")
    public Response postBooking(@Body Booking booking);

    /**
     * Add where a user is sat
     * @param sitting The details of the Sitting
     * @return Success or failure
     */
    @PUT("/sittings")
    public Response postSitting(@Body Sitting sitting);

    /**
     * Get a list of the seat types available
     * @return List of seat types
     */
    @GET("/seattypes")
    public List<SeatType> getPrices();

    /**
     * Gets a list of artists
     * @return An Observable on a list of artists
     */
    @GET("/artists?filter[order]=name%20ASC")
    public Observable<List<Artist>> getArtists();

    /**
     * Get a list of Bookings
     * @param userID The user id of the user that made the booking
     * @return List of bookings
     */
    @GET("/users/{id}/bookings?filter[include][event]=venue" +
            "&filter[order]=dateTime%20DESC")
    public Observable<ArrayList<Booking>> getBookings(@Path("id") int userID);

}
