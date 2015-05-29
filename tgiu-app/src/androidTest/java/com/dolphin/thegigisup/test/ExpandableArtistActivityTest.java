package com.dolphin.thegigisup.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ExpandableListView;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.activitys.ExpandableArtistActivity;
import com.dolphin.thegigisup.models.Artist;
import java.util.ArrayList;

public class ExpandableArtistActivityTest extends
        ActivityInstrumentationTestCase2<ExpandableArtistActivity> {

    ExpandableArtistActivity expArtistActivity;
    private ExpandableListView artists;
    private ArrayList<Artist> artistArrayList;

    public ExpandableArtistActivityTest() {
        super(ExpandableArtistActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Create a mock event object for Intent.
        createArtists();

        // Setup mock intent for List so it has Event Object to get
        // Artists from.
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("artists", artistArrayList);
        setActivityIntent(intent);

        expArtistActivity = getActivity();

        artists = (ExpandableListView) expArtistActivity.
                findViewById(R.id.elv_artist_frag);

    }

    public void testPreconditions(){
        assertNotNull("Activity is null", expArtistActivity);
        assertNotNull("Username null", artists);
    }

    public void testGroupSize() {
        assertSame(2, artists.getExpandableListAdapter().getGroupCount());
    }

    public void testChildSize() {
        assertSame(1, artists.getExpandableListAdapter().getChildrenCount(0));
        assertSame(1, artists.getExpandableListAdapter().getChildrenCount(1));
    }

    public void testDescription() {
        assertNotNull("Null Description", artists.
                getExpandableListAdapter().getChild(0,0).toString());
        assertNotNull("Null Description", artists.
                getExpandableListAdapter().getChild(1,0).toString());
    }

    private void createArtists() {

        Artist artist1 = new Artist();
        artist1.setName("Test Artist 1");
        artist1.setDescription("Description");
        artist1.setImageURL("URL 1");
        Artist artist2 = new Artist();
        artist2.setName("Test Artist 2");
        artist2.setDescription("Description");
        artist2.setImageURL("URL 2");

        artistArrayList = new ArrayList<>();

        artistArrayList.add(artist1);
        artistArrayList.add(artist2);


    }
}

