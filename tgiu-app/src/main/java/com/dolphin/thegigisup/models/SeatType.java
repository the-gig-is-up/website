package com.dolphin.thegigisup.models;

import com.google.gson.annotations.Expose;

/**
 * SeatType object to hold seat type information
 *
 * @author Team Dolphin 27/04/15.
 */
public class SeatType {
    @Expose
    private int id;
    @Expose
    private String type;
    @Expose
    private double priceModifier;

    /**
     * @return Current seat type ID
     */
    public int getId() {
        return id;
    }

    /**
     * @param id New seat type ID to be set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Current price modifier for the seat type
     */
    public double getPriceModifier() {
        return this.priceModifier;
    }

    /**
     * @param priceModifier New price modifier to set for the seat type
     */
    public void setPriceModifier(double priceModifier) {
        this.priceModifier = priceModifier;
    }
}
