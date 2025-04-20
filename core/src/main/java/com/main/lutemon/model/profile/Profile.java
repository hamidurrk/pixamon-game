package com.main.lutemon.model.profile;

import com.main.lutemon.model.lutemon.Lutemon;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a player profile that stores all game data for a specific player.
 */
public class Profile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Date creationDate;
    private Date lastPlayedDate;
    private List<Lutemon> lutemons;
    private int totalLutemonsCreated;
    private int totalBattles;
    private int totalTrainingSessions;

    /**
     * Default constructor for serialization.
     * Required by LibGDX's Json serializer.
     */
    public Profile() {
        this.creationDate = new Date();
        this.lastPlayedDate = new Date();
        this.lutemons = new ArrayList<>();
        this.totalLutemonsCreated = 0;
        this.totalBattles = 0;
        this.totalTrainingSessions = 0;
    }

    /**
     * Creates a new profile with the given name.
     *
     * @param name The name of the profile
     */
    public Profile(String name) {
        this();
        this.name = name;
    }

    /**
     * Updates the last played date to the current date.
     */
    public void updateLastPlayedDate() {
        this.lastPlayedDate = new Date();
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastPlayedDate() {
        return lastPlayedDate;
    }

    public void setLastPlayedDate(Date lastPlayedDate) {
        this.lastPlayedDate = lastPlayedDate;
    }

    public List<Lutemon> getLutemons() {
        return lutemons;
    }

    public void setLutemons(List<Lutemon> lutemons) {
        this.lutemons = lutemons;
    }

    public int getTotalLutemonsCreated() {
        return totalLutemonsCreated;
    }

    public void setTotalLutemonsCreated(int totalLutemonsCreated) {
        this.totalLutemonsCreated = totalLutemonsCreated;
    }

    public void incrementTotalLutemonsCreated() {
        this.totalLutemonsCreated++;
    }

    public int getTotalBattles() {
        return totalBattles;
    }

    public void setTotalBattles(int totalBattles) {
        this.totalBattles = totalBattles;
    }

    public void incrementTotalBattles() {
        this.totalBattles++;
    }

    public int getTotalTrainingSessions() {
        return totalTrainingSessions;
    }

    public void setTotalTrainingSessions(int totalTrainingSessions) {
        this.totalTrainingSessions = totalTrainingSessions;
    }

    public void incrementTotalTrainingSessions() {
        this.totalTrainingSessions++;
    }

    @Override
    public String toString() {
        return name + " (Created: " + creationDate + ", Last played: " + lastPlayedDate + ")";
    }
}
