package com.dolphin.thegigisup.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dolphin.thegigisup.models.Event;
import com.dolphin.thegigisup.activitys.EventDetailsActivity;
import com.dolphin.thegigisup.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import org.joda.time.DateTime;
import java.util.List;

/**
 * Adapter to populate information for a list of event objects
 *
 * @author Team Dolphin
 */
public class EventsAdapter
    extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private final int layoutResource;
    private List<Event> events;
    private int userID;

    /**
     * Constructor using a list of event objects and an xml layout resource
     */
    public EventsAdapter(List<Event> events, int layoutResource) {
        this.events = events;
        this.layoutResource = layoutResource;
    }

    /**
     * Method to set a new list of events in the data set
     *
     * @param events A list of event objects
     */
    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
        notifyItemRangeInserted(0, events.size());
    }

    /**
     * Add an extra event to the list of events in the adapter
     * @param event An event object
     */
    public void addEvent(Event event) {
        this.events.add(event);
        notifyItemInserted(events.size() + 1);
    }

    /**
     * Add multiple extra events to the current adapter events list
     *
     * @param events A list of event objects
     */
    public void addEvents(List<Event> events) {
        for (Event event : events) {
            this.events.add(event);
            notifyItemInserted(events.size());
        }
        notifyDataSetChanged();
    }

    /**
     * @return The given user ID
     */
    public int getUserID() {
        return this.userID;
    }

    /**
     * @return The number of events in the adapter
     */
    @Override
    public int getItemCount() {
        if (events == null) return 0;
        else return events.size ();
    }

    /**
     * On create view holder inflate the respective xml layout
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(layoutResource, viewGroup, false);

        return new ViewHolder(v);
    }

    /**
     * Set the current event on bind to the ViewHolder and use Picasso to
     * load the event image
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int index) {
        Event event = events.get(index);
        if (!event.getArtists().get(0).getImageURL().isEmpty()) {
            Picasso.with(viewHolder.imageView.getContext()).load(
                    event.getArtists().get(0).getImageURL()).
                    into(viewHolder.imageView);
        }
        viewHolder.setEvent(event);
    }

    /**
     * ViewHolder class to hold the event information
     */
    public class ViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        private TextView artistName;
        private TextView venueName;
        private TextView location;
        private TextView date_time;
        private TextView price;
        private TextView ticketsLeft;
        private String imageURL;
        private RoundedImageView imageView;
        private Event e;

        /**
         * The constructor initialises the views needed to represent the event
         * information
         *
         * @param itemView The current item view
         */
        public ViewHolder(View itemView) {
            super(itemView);

            artistName = (TextView) itemView
                                    .findViewById(R.id.fea_tv_event_artist);
            venueName = (TextView) itemView
                                    .findViewById(R.id.fea_tv_event_venue);
            location = (TextView) itemView
                                    .findViewById(R.id.fea_tv_event_location);
            date_time = (TextView) itemView.findViewById(
                                            R.id.fea_tv_event_date_time);
            price = (TextView) itemView.findViewById(R.id.fea_tv_event_price);
            ticketsLeft = (TextView) itemView
                                      .findViewById(
                                                R.id.fea_tv_event_tickets_left);
            imageView = (RoundedImageView) itemView.findViewById(
                                                        R.id.featured_image);


            itemView.setOnClickListener(this);
        }

        /**
         * Set the event information using the values from the event object
         *
         * @param e The given event object
         */
        public void setEvent(Event e) {
            this.e = e;
            artistName.setText(e.getEventName());
            venueName.setText(e.getVenue().getName());
            location.setText(e.getVenue().getAddress());
            price.setText("{faw-money} £" + e.getPrice());
            ticketsLeft.setText("{faw-ticket} "+String.valueOf(
                                                e.getTicketsLeft()));

            String time = e.getStartTime();
            DateTime date = e.getDate();

            String clockIcon = "{faw-clock-o} "
                                + date.toString("dd MMM")
                                + " at "
                                + time;
            date_time.setText(clockIcon);
            imageURL = e.getArtists().get(0).getImageURL();

            if (e.isLowTickets()) {
                ticketsLeft.setTextColor(itemView
                        .getResources()
                        .getColor(R.color.color_red));
            } else {
                ticketsLeft.setTextColor(itemView
                        .getResources()
                        .getColor(R.color.color_dark_gray));
            }

            itemView.setTag(e);
        }

        /**
         * OnClick method starts an intent to open up an EventDetailsActivity
         * using a chosen event
         *
         * @param v The clicked event view
         */
        @Override
        public void onClick(View v){

            // On click can be implemented here
            // getPosition() for integer value
            // Or just get the value from the TextViews.

            Intent intent = new Intent(v.getContext(),
                                       EventDetailsActivity.class);

            intent.putExtra("com.dolphin.thegigisup.Event", e);
            v.getContext().startActivity(intent);
        }

    }
}
