package com.dolphin.thegigisup.models;

import com.google.gson.annotations.Expose;

/**
 * An object to store seat information
 *
 * @author Team Dolphin 16/04/2015.
 */
public class Seat {

    @Expose
    private int id;
    @Expose
    private String row;
    @Expose
    private Integer column;
    @Expose
    private Integer typeID;
    @Expose
    private Integer venueID;
    private String name;
    private boolean isTaken;
    private boolean isSelected;

    Seat() {}

    Seat(String name) {
        setName(name);
    }

    /**
     * @param newName New seat name to set
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * @return Current seat name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return Current seat ID
     */
    public int getId() {
        return id;
    }

    /**
     * @param id New seat ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Current row the seat is in
     */
    public String getRow() {
        return row;
    }

    /**
     * @param row New row of the seat to set
     */
    public void setRow(String row) {
        this.row = row;
    }

    /**
     * @return Current column the seat is in
     */
    public Integer getColumn() {
        return column;
    }

    /**
     * @param column New column to set the seat to be in
     */
    public void setColumn(Integer column) {
        this.column = column;
    }

    /**
     * @return Current type ID of the seat
     */
    public Integer getTypeID() {
        return typeID;
    }

    /**
     * @param typeID New type ID to assign to the seat
     */
    public void setTypeID(Integer typeID) {
        this.typeID = typeID;
    }

    /**
     * @return Current venueID for the seat
     */
    public Integer getVenueID() {
        return venueID;
    }

    /**
     * @param venueID New venue ID to set for the seat
     */
    public void setVenueID(Integer venueID) {
        this.venueID = venueID;
    }

    /**
     * @return True if the seat is taken, false if not
     */
    public boolean isTaken() {
        return isTaken;
    }

    /**
     * @param isTaken New value for whether the seat is taken or not
     */
    public void setTaken(boolean isTaken) {
        this.isTaken = isTaken;
    }

    /**
     * @return Custom row + column string to represent the seat
     */
    @Override
    public String toString() {
        return getRow() + getColumn();
    }

    /**
     * @return True/false in regards to whether the user has selected the seat
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @param isSelected A boolean set to true if the user selects the seat
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
