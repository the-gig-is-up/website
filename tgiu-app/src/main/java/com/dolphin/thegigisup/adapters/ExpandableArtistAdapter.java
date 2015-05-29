package com.dolphin.thegigisup.adapters;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.dolphin.thegigisup.models.Artist;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.activitys.SearchResultsActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to store the expandable artist information for each event
 *
 * @author Team Dolphin
 */
public class ExpandableArtistAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Artist> artistList;
    private List<String> childList;

    /**
     * Initialise variables using the custom constructor
     */
    public ExpandableArtistAdapter(Context context,
                                   List<Artist> artistList) {
        this.artistList = artistList;
        this.context = context;

        this.childList = new ArrayList<>();

        for(Artist name : artistList) {
            childList.add(name.getDescription());
        }

    }

    @Override
    public String getChild(int groupPos, int childPos) {
        return this.childList.get(groupPos);
    }

    @Override
    public long getChildId(int groupPos, int childPos){
        return childPos;
    }

    /**
     * Get child view inflates the expandable item view and sets a search
     * intent to find more events by the current
     * artist
     * @return The expanded child view
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView,
                             ViewGroup parent) {

        final String childText = getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expand_item, null);
        }
        convertView.setClickable(false);
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        Button search = (Button) convertView.findViewById(R.id.item_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),
                        SearchResultsActivity.class);
                Artist current = getGroup(groupPosition);
                i.setAction(Intent.ACTION_SEARCH);
                i.putExtra(SearchManager.QUERY,current.getName());
                v.getContext().startActivity(i);
            }
        });

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Artist getGroup(int groupPosition) {
        return this.artistList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.artistList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * The group view will display the non expanded artist views, with only
     * the artist name and image visible
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View v, ViewGroup parent) {

        Artist current = getGroup(groupPosition);
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.expand_header, null);
        }

        TextView artName = (TextView) v.findViewById(R.id.lv_item_name);
        RoundedImageView artIm = (RoundedImageView)
                v.findViewById(R.id.lv_item_artist_image);

        artName.setText(current.getName());

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

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

