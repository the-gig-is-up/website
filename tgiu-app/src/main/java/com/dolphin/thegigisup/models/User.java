package com.dolphin.thegigisup.models;

import com.google.gson.annotations.Expose;

/**
 * A user object to store user information
 *
 * @author Team Dolphin 21/03/15.
 */
public class User {

    @Expose
    private int id;
    @Expose
    private String firstName;
    @Expose
    private String lastName;
    @Expose
    private String email;
    @Expose
    private int isAdmin;
    @Expose
    private int isArtist;
    @Expose
    private String realm;
    @Expose
    private String username;
    @Expose
    private String password;
    @Expose
    private Object challenges;
    @Expose
    private Object credentials;
    @Expose
    private Boolean emailVerified;
    @Expose
    private String verificationToken;
    @Expose
    private String status;
    @Expose
    private String created;
    @Expose
    private String lastUpdated;

    /**
     * @param id New id to set for the user
     */
    public void setId(int id) {this.id = id;}

    /**
     * @return Current user ID
     */
    public int getId() {
        return id;
    }

    /**
     * @param email New email to set for the user
     */
    public void setEmail(String email) {this.email = email;}

    /**
     * @return Current email address for the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param username New username to set for the user
     */
    public void setUsername(String username) {this.username = username;}

    /**
     * @return Current username for the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param password New password to set for the user
     */
    public void setPassword(String password) {this.password = password;}

    /**
     * @return Current user password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param isArtist New value to change whether the user is an artist or not
     */
    public void setIsArtist(int isArtist) { this.isArtist = isArtist;}

    /**
     * @return Current integer to show if the user is an artist or not
     */
    public int getIsArtist() {
        return isArtist;
    }

    /**
     * @param isAdmin New value to change whether the user is an admin or not
     */
    public void setIsAdmin(int isAdmin) { this.isAdmin = isAdmin;}

    /**
     * @return Current integer to show if the user is an admin or not
     */
    public int getIsAdmin() {
        return isAdmin;
    }
}


