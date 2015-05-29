package com.dolphin.thegigisup.models;

import com.google.gson.annotations.Expose;

/**
 * Login details object to store user login details
 *
 * @author Team Dolphin 20/03/15.
 */
public class LoginDetails {

    @Expose
    private String email;
    @Expose
    private String username;
    @Expose
    private String password;

    /**
     * @return Current email in the login details
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email New email for login details to be set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Current username for login details
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username New username for login details to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return Current password for the login details
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password New password to set in the login details
     */
    public void setPassword(String password) {
        this.password = password;
    }

}