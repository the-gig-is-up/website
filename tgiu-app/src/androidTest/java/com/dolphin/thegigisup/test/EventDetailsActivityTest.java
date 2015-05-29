package com.dolphin.thegigisup.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.activitys.EventDetailsActivity;
import com.dolphin.thegigisup.models.Artist;
import com.dolphin.thegigisup.models.Event;
import com.dolphin.thegigisup.models.Venue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gurpreet Paul - 24/02/2015.
 */
public class EventDetailsActivityTest extends
        ActivityInstrumentationTestCase2<EventDetailsActivity> {

    EventDetailsActivity eventDetailsActivity;

    private Event event;

    public EventDetailsActivityTest() {super(EventDetailsActivity.class);}

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        event = createEvent();
        Intent intent = new Intent();
        intent.putExtra("com.dolphin.thegigisup.Event", event);
        setActivityIntent(intent);
        eventDetailsActivity = getActivity();
    }

    public void testPreconditions(){
        assertNotNull("eventDetailsActivity is null", eventDetailsActivity);
    }

    public void testEventTitle(){
        TextView eventName = (TextView) eventDetailsActivity.
                findViewById(R.id.fea_tv_artistName);
        final String expectedEvent = "Event Details Event".toLowerCase();
        final String actualEvent = eventName.getText().toString().
                toLowerCase();
        assertEquals(expectedEvent, actualEvent);
    }

    public void testTicketsTitle(){
        TextView ticketsLeft = (TextView) eventDetailsActivity.
                findViewById(R.id.fea_tv_ticketNum);

        final String expectedTickets = "10 tickets left".toLowerCase();
        final String actualTickets = ticketsLeft.getText().toString().
                toLowerCase();

        assertEquals(expectedTickets, actualTickets);
    }

    public void testArtistsAttending(){
        TextView artistsAttending = (TextView) eventDetailsActivity.
                findViewById(R.id.fea_tv_artists);

        final String expectedArtists = "2 acts attending".toLowerCase();
        final String actualArtists = artistsAttending.getText().toString().
                toLowerCase();

        assertEquals(expectedArtists, actualArtists);
    }

    public void testEventVenueLocation(){
        TextView eventVenueLocation = (TextView) eventDetailsActivity.
                findViewById(R.id.fea_tv_location);

        final String expectedLocation = "Event Details Venue".toLowerCase()
                + "\nEvent Details Location\nEvent Details Address"
                .toLowerCase();
        final String actualLocation = eventVenueLocation.getText().toString().
                toLowerCase();

        assertEquals(expectedLocation, actualLocation);

    }

    public void testEventTimeDate(){
        TextView eventTimeDate = (TextView) eventDetailsActivity.
                findViewById(R.id.fea_tv_timedate);

        final String expectedTimeDate = "19:00 on 01 Jul 15".
                toLowerCase();
        final String actualTimeDate = eventTimeDate.getText().toString().
                toLowerCase();

        assertEquals(expectedTimeDate, actualTimeDate);
    }

    private Event createEvent() {

        Venue venue = new Venue();
        venue.setName("Event Details Venue");
        venue.setLocation("Event Details Location");
        venue.setAddress("Event Details Address");
        venue.setId(1002);
        venue.setCapacity(5000);

        Artist artist = new Artist();
        artist.setName("Artist");
        Artist artist2 = new Artist();
        artist.setImageURL("http://placehold.it/350x150");
        List artists = new ArrayList<>();
        artists.add(artist);
        artists.add(artist2);

        com.dolphin.thegigisup.models.Event event = new Event();

        event.setArtists(artists);
        event.setVenue(venue);
        event.setId(1);
        event.setPrice("10");
        event.setEventName("Event Details Event");
        event.setDate("2015-07-01T00:00:00.000Z");
        event.setStartTime("19:00:00");
        event.setEndTime("00:00:00");
        event.setTicketsLeft(10);

        return event;
    }
}
