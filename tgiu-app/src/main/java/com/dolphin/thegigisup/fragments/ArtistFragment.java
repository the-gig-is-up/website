package com.dolphin.thegigisup.fragments;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import au.com.gridstone.grex.GRexPersister;
import au.com.gridstone.grex.converters.GsonConverter;
import com.dolphin.thegigisup.models.Artist;
import com.dolphin.thegigisup.R;
import com.dolphin.thegigisup.activitys.SearchResultsActivity;
import com.dolphin.thegigisup.api.ServiceFactory;
import com.dolphin.thegigisup.adapters.ArtistAdapter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display all the artists in alphabetical order
 *
 * @author Team Dolphin
 */
public class ArtistFragment extends Fragment {

    // Initialise the appropriate variables
    private ListView artistList;
    private ArrayList<Artist> artists = new ArrayList<>();
    private Observable<List<Artist>> artistObservable;
    private Subscription artistSubscription;
    private ArtistAdapter artistAdapter;

    private static final String ARTIST_KEY = "artists";
    private GRexPersister persister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        persister = new GRexPersister(
                new GsonConverter(),
                new File(getActivity().getCacheDir().getAbsolutePath()));

        if (savedInstanceState == null) {
            artistObservable = ServiceFactory
                    .createInstance()
                    .getArtists();
        }
        artistSubscription = artistObservable
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(artists1 -> persister.putList(ARTIST_KEY,
                    artists1,
                    Artist.class))
            .onErrorResumeNext(throwable -> {
                setHeader();
                return persister.getList(ARTIST_KEY, Artist.class);
            })
            .subscribe(this::updateArtists);
    }

    /**
     * Sets the header if got cached results
     */
    private void setHeader() {
        ViewGroup header =
                (ViewGroup) getActivity()
                        .getLayoutInflater()
                        .inflate(R.layout.cached_message,
                                artistList,
                                false);
        artistList.addHeaderView(header);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.artist_frag, container, false);
        setupArtists(v);
        return v;
    }


    @Override
    public void onDestroy() {
        artistSubscription.unsubscribe();
        super.onDestroy();
    }

    /**
     * Set up artists gets the listview and populates it with the artists
     * information
     */
    private void setupArtists(View v) {
        artistList =
                (ListView) v.findViewById(R.id.fea_lv_artists);

        artistAdapter = new ArtistAdapter(v.getContext(), artists);


        // Set an on click listener to search for all events by the chosen
        // artist
        artistList.setAdapter(artistAdapter);
        artistList.setOnItemClickListener((parent, view, pos, id) -> {
            Intent i = new Intent(getActivity(),
                    SearchResultsActivity.class);
            Artist current = artistAdapter.getItem(pos);
            i.setAction(Intent.ACTION_SEARCH);
            i.putExtra(SearchManager.QUERY,current.getName());
            startActivity(i);
        });
    }


    /**
     * Helper function to add artists to adapter
     * @param newArtists List of new artists
     */
    private void updateArtists(List<Artist> newArtists) {
        if (artistAdapter != null) {
            artistAdapter.addAll(newArtists);
            artistAdapter.notifyDataSetChanged();
        } else {
            artists.addAll(newArtists);
        }
    }



}