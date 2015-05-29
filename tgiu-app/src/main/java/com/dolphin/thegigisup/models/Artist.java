package com.dolphin.thegigisup.models;


import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;

/**
 * A class to hold artist information. It is parcelable to pass between pages
 * in the app
 *
 * @author Team Dolphin
 */
public class Artist implements Parcelable {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private String imageURL;

    /**
     * @return The artist id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id Set the artist id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The artist's name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Set the artist's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The artist's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param desc Set the artist's description
     */
    public void setDescription(String desc) {
        this.description = desc;
    }

    /**
     * @return The URL of the artist's image
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * @param URL Set the URL of the artist's image
     */
    public void setImageURL(String URL) {
        this.imageURL = URL;
    }

    // Some methods to implement parcelable in artist
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.imageURL);
    }

    public Artist() {
    }

    private Artist(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.description = in.readString();
        this.imageURL = in.readString();
    }

    public static final Parcelable.Creator<Artist> CREATOR =
                                            new Parcelable.Creator<Artist>() {

        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}