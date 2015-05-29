package com.dolphin.thegigisup.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dolphin.thegigisup.models.Booking;
import com.dolphin.thegigisup.R;

import java.util.ArrayList;

/**
 * Adapter to store the user bookings
 *
 * @author Team Dolphin 25/04/2015.
 */
public class BookingsAdapter
            extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {

    private ArrayList<Booking> bookings;
    private int layoutResource;

    /**
     * Constructor that requires a list of bookings and a layout resource
     */
    public BookingsAdapter(ArrayList<Booking> bookings, int layoutResource) {
        this.bookings = bookings;
        this.layoutResource = layoutResource;
    }

    /**
     * Set bookings sets a list of bookings and updates the data set
     *
     * @param bookings A list of new bookings
     */
    public void setBookings(ArrayList<Booking> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
        notifyItemRangeInserted(0, bookings.size());
    }

    /**
     * Add one booking to the end of the list and notify the change
     *
     * @param booking A booking object
     */
    public void addBooking(Booking booking) {
        this.bookings.add(booking);
        notifyItemInserted(bookings.size() + 1);
    }

    /**
     * @return The amount of bookings in the adapter
     */
    @Override
    public int getItemCount() {
        if (bookings == null) return 0;
        else return bookings.size();
    }

    /**
     * Inflate the view v and return a viewholder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(layoutResource, viewGroup, false);

        return new ViewHolder(v);
    }

    /**
     * Bind the viewholder to each booking
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int index) {
        Booking booking = bookings.get(index);
        viewHolder.setBooking(booking);
    }

    /**
     * Extend the viewholder class to update the booking information
     */
    public class ViewHolder
            extends RecyclerView.ViewHolder{

        private TextView ticketRef;
        private TextView eventName;
        private TextView location;

        /**
         * In the constructor, find the appropriate textviews
         *
         * @param itemView The given item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            ticketRef = (TextView) itemView
                    .findViewById(R.id.boo_tv_ticket_ref);
            eventName = (TextView) itemView
                    .findViewById(R.id.boo_tv_event_name);
            location = (TextView) itemView
                    .findViewById(R.id.boo_tv_location);
        }

        /**
         * Set booking method is called in onBindViewHolder to set the correct
         * booking information
         */
        public void setBooking(Booking b) {
            ticketRef.setText("Reference: " + b.getTicketRef());
            eventName.setText(b.getEvent().getEventName());
            location.setText(b.getEvent().getVenue().getName());

            itemView.setTag(b);
        }

    }
}
