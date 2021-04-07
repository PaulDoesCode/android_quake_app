// Name:    Paul Harbison-Smith
// No:      S1712745

// Package
package org.me.gcu.equakestartercode;

// Imports
import java.io.Serializable;

// Quake class
public class Quake implements Serializable, Comparable {

    // Declare variables
    private String title;
    private String description;
    private String link;
    private String pubDate;
    private String category;
    private String geoLat;
    private String geoLong;
    private String location;
    private String depth;
    private String magnitude;

    // Empty Quake constructor used to create an empty object
    // Object has attributes added individually rather than all being made at the start
    public Quake() {

    }

    // Getters and setters for Quake variables

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    // For description, we delimit the Strings and retrieve each unique entry in the description
    // Use description.split() to do so
    // FOR loop indexes the result array to get the relevant data (location, depth, magnitude)
    public void setDescription(String description) {
        this.description = description;
        String[] result = description.split(";");
        for(int i = 0; i < result.length; i++)  {
            if(i==1)    {
                setLocation((result[1]));
            }
            else if(i==3)   {
                setDepth((result[3]));
            }
            else if(i==4)   {
                setMagnitude((result[4]));
            }
        }
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGeoLat() {
        return geoLat;
    }

    public void setGeoLat(String geoLat) {
        this.geoLat = geoLat;
    }

    public String getGeoLong() {
        return geoLong;
    }

    public void setGeoLong(String geoLong) {
        this.geoLong = geoLong;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(String magnitude) {
        this.magnitude = magnitude;
    }

    // compareTo used to compare the magnitude of each earthquake object so that we can sort the data by magnitude
    @Override
    public int compareTo(Object o) {
        Quake quake = (Quake)o;
        String quakeMagnitudeA = quake.getMagnitude().substring(11);
        String quakeMagnitudeB = this.getMagnitude().substring(11);
        Float floatQuakeMagnitudeA = Float.parseFloat(quakeMagnitudeA);
        Float floatQuakeMagnitudeB = Float.parseFloat(quakeMagnitudeB);
        return floatQuakeMagnitudeA.compareTo(floatQuakeMagnitudeB);
    }
}
