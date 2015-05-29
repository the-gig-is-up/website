package com.dolphin.thegigisup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.models.Seat;
import com.dolphin.thegigisup.models.Sitting;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An adapter to hold seat objects
 *
 * @author Team Dolphin
 */
public class SeatingAdapter extends BaseAdapter {
    private Context context;
    private int layoutResourceId;
    private HashMap<Integer, Seat> data;
    Integer[] keys;

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Seat getItem(int position) {
        return data.get(keys[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public SeatingAdapter(Context context,
                          int layoutResourceId,
                          LinkedHashMap<Integer, Seat> data) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    /**
     * Find seats in this adapter's data source
     * then highlight them red
     *
     * @param taken Seats that have been taken
     */
    public void addTakenSeats(ArrayList<Sitting> taken) {
        for (Sitting takenSeat : taken) {
            int id = takenSeat.getSeatId();
            if (data.get(id) != null) {
                Seat seat = data.get(id);
                seat.setTaken(true);
                data.put(id, seat);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        makeKeySet();
        super.notifyDataSetChanged();
    }

    private void makeKeySet() {
        keys = data.keySet().toArray(new Integer[data.size()]);
    }

    private void makeKeySet(int size) {
        keys = data.keySet().toArray(new Integer[size]);
    }

    /**
     * Swap the contents of the data source with this
     * new data source
     *
     * @param seats List of seats that needs swapping
     */
    public void addSeats(ArrayList<Seat> seats) {
        data = new LinkedHashMap<>();
        for (Seat seat: seats) {
            data.put(seat.getId(), seat);
        }
        notifyDataSetChanged();
    }

    /**
     * If there are any selected seats in the adapter, set them to be
     * unselected
     */
    public void clearSelectedSeats() {
        for (Map.Entry pair: data.entrySet()) {
            Seat seat = (Seat) pair.getValue();
            if (seat.isSelected()) {
                seat.setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.name = (TextView) row.findViewById(R.id.sea_tv_seat_name);
            holder.bg = (RoundedImageView) row.findViewById(
                                                      R.id.sea_riv_seat_bg);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        Integer key = keys[position];
        Seat seat = data.get(key);
        holder.name.setText(seat.toString());

        if (seat.isTaken()) {
            holder.bg.setImageResource(R.color.color_red);
        } else if (seat.isSelected()) {
            holder.bg.setImageResource(R.color.color_darker_gray);
        } else {
            holder.bg.setImageResource(R.color.color_primary);
        }


        return row;

    }



    static class RecordHolder {
        TextView name;
        RoundedImageView bg;

    }
}