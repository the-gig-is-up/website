package com.dolphin.thegigisup.models;

import com.google.gson.annotations.Expose;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Login confirmation object to be received when the user has logged in
 *
 * @author Team Dolphin 20/03/15.
 */
public class LoginConfirm {

    @Expose
    private String id;
    @Expose
    private String ttl;
    @Expose
    private String created;
    @Expose
    private int userId;
    @Expose
    private User user;

    /**
     * @return Current ID from the login confirmation
     */
    public String getId() {
        return id;
    }

    /**
     * @param id Set the ID for login confirmation
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Current time to live from the login
     */
    public String getTtl() {
        return ttl;
    }

    /**
     * @param ttl New time to live to set for the login
     */
    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    /**
     * @return Current created date for the login
     */
    public DateTime getCreated() {
        DateTime theDate = DateTime.parse(created,
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        return theDate;
    }

    /**
     * @return Current user ID from the login confirmation
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return Current user object from the login confirmation
     */
    public User getUser() { return user; }

}
