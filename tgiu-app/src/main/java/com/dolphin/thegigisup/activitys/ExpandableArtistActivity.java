package com.dolphin.thegigisup.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.adapters.ExpandableArtistAdapter;
import com.dolphin.thegigisup.models.Artist;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity to display the artists playing at a particular gig
 *
 * @author Team Dolphin
 */
public class ExpandableArtistActivity extends ActionBarActivity{

    private ExpandableListView artistList;
    private List<Artist> artists = new ArrayList<>();
    private ExpandableArtistAdapter artistAdapter;

    /**
     * On create, set the xml view and initialise the appropriate variables
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_artist);

        this.artistList =
                (ExpandableListView) findViewById(R.id.elv_artist_frag);

        Intent receivedIntent = getIntent();
        Bundle b = receivedIntent.getExtras();
        artists = b.getParcelableArrayList("artists");

        artistAdapter = new ExpandableArtistAdapter(this,artists);

        artistList.setFocusable(false);
        artistList.setAdapter(this.artistAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}