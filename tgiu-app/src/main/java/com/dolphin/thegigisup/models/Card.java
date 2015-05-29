package com.dolphin.thegigisup.models;

import com.google.gson.annotations.Expose;
import org.joda.time.DateTime;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;

/**
 * Card class to hold card information
 *
 * @author Team Dolphin 13/04/15.
 */
public class Card {
    @Expose
    private String id;
    @Expose
    private int userID;
    @Expose
    private String nameOnCard;
    @Expose
    private String expiry;

    /**
     * @return Current ID for the card
     */
    public String getId() {
        return id;
    }

    /**
     * @param id New ID for the card to be set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Current user ID on the card
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID New user ID to be set for the card
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return Current name on the card
     */
    public String getCardName() {
        return nameOnCard;
    }

    /**
     * @param cardName New name to be set on the card
     */
    public void setCardName(String cardName) {
        this.nameOnCard = cardName;
    }

    /**
     * @return Current year and month expiry date on the card
     */
    public YearMonth getCardExpiry() {
        DateTime theDate = DateTime.parse(expiry,
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        YearMonth yearMonth = YearMonth.fromDateFields(theDate.toDate());
        return yearMonth;
    }

    /**
     * @param cardExpiry New expiry date to set for the card
     */
    public void setCardExpiry(String cardExpiry) {
        this.expiry = cardExpiry;
    }
}
