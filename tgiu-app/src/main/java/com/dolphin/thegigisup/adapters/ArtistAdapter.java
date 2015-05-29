package com.dolphin.thegigisup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.dolphin.thegigisup.models.Artist;
import com.dolphin.thegigisup.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Adapter to store artist information in
 *
 * @author Team Dolphin
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {

    public ArtistAdapter(Context context, ArrayList<Artist> artists) {
        super(context, 0, artists);
    }

    /**
     * Get view initialises the artist information views based on the position
     */
    @Override
    public View getView(int position, View v, ViewGroup parent) {

        Artist current = getItem(position);

        // Inflate the artist item layout
        if (v == null) {
            v = LayoutInflater.from(getContext()).
                    inflate(R.layout.artist_item, parent, false);
        }

        // Find and set the relevant text information
        TextView artName = (TextView) v.findViewById(R.id.lv_item_name);
        TextView artDesc = (TextView) v.findViewById(R.id.lv_item_gig_desc);
        RoundedImageView artIm = (RoundedImageView)
                v.findViewById(R.id.lv_item_artist_image);

        artName.setText(current.getName());
        artDesc.setText(current.getDescription());

        // Load the artist image and set the default image if an error occurs
        try {
            if (!current.getImageURL().isEmpty()) {
                Picasso.with(artIm.getContext())
                        .load(current.getImageURL())
                        .into(artIm);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            artIm.setBackgroundResource(R.drawable.event);
        }

        return v;
    }

}
