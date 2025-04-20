package com.main.lutemon.utils;

import com.badlogic.gdx.Gdx;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.model.profile.Profile;
import com.main.lutemon.model.storage.Storage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages game statistics.
 * Tracks general statistics like total Lutemons created, total battles, and total training sessions.
 * Also provides methods to get performance statistics for individual Lutemons.
 */
public class StatisticsManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static volatile StatisticsManager instance;

    // General statistics
    private int totalLutemonsCreated;
    private int totalBattles;
    private int totalTrainingSessions;

    private StatisticsManager() {
        totalLutemonsCreated = 0;
        totalBattles = 0;
        totalTrainingSessions = 0;
    }

    // Constructor initializes statistics to zero

    public static StatisticsManager getInstance() {
        if (instance == null) {
            synchronized (StatisticsManager.class) {
                if (instance == null) {
                    instance = new StatisticsManager();
                }
            }
        }
        return instance;
    }

    /**
     * Increments the total number of Lutemons created.
     */
    public synchronized void incrementLutemonsCreated() {
        totalLutemonsCreated++;
        Gdx.app.log("StatisticsManager", "Total Lutemons created: " + totalLutemonsCreated);

        // Update profile if available
        Profile currentProfile = ProfileManager.getInstance().getCurrentProfile();
        if (currentProfile != null) {
            currentProfile.incrementTotalLutemonsCreated();
        }
    }

    /**
     * Increments the total number of battles.
     */
    public synchronized void incrementTotalBattles() {
        totalBattles++;
        Gdx.app.log("StatisticsManager", "Total battles: " + totalBattles);

        // Update profile if available
        Profile currentProfile = ProfileManager.getInstance().getCurrentProfile();
        if (currentProfile != null) {
            currentProfile.incrementTotalBattles();
        }
    }

    /**
     * Increments the total number of training sessions.
     */
    public synchronized void incrementTotalTrainingSessions() {
        totalTrainingSessions++;
        Gdx.app.log("StatisticsManager", "Total training sessions: " + totalTrainingSessions);

        // Update profile if available
        Profile currentProfile = ProfileManager.getInstance().getCurrentProfile();
        if (currentProfile != null) {
            currentProfile.incrementTotalTrainingSessions();
        }
    }

    /**
     * Gets the total number of Lutemons created.
     * @return The total number of Lutemons created
     */
    public int getTotalLutemonsCreated() {
        return totalLutemonsCreated;
    }

    // Getter for totalLutemonsCreated

    /**
     * Gets the total number of battles.
     * @return The total number of battles
     */
    public int getTotalBattles() {
        return totalBattles;
    }

    // Getter for totalBattles

    /**
     * Gets the total number of training sessions.
     * @return The total number of training sessions
     */
    public int getTotalTrainingSessions() {
        return totalTrainingSessions;
    }

    // Getter for totalTrainingSessions

    /**
     * Gets a map of Lutemon IDs to their performance statistics.
     * @return A map of Lutemon IDs to their performance statistics
     */
    public Map<Integer, LutemonPerformance> getLutemonPerformanceStats() {
        Map<Integer, LutemonPerformance> performanceMap = new HashMap<>();

        for (Lutemon lutemon : Storage.getInstance().getAllLutemons()) {
            int id = lutemon.getId();
            int battles = lutemon.getStats().getBattles();
            int wins = lutemon.getStats().getWins();
            int trainingDays = lutemon.getStats().getTrainingDays();

            performanceMap.put(id, new LutemonPerformance(battles, wins, trainingDays));
        }

        return performanceMap;
    }

    /**
     * Resets all statistics.
     * This is called when loading a profile to ensure statistics are properly isolated between profiles.
     */
    public synchronized void reset() {
        Gdx.app.log("StatisticsManager", "Resetting statistics - Old values: " +
                  "Lutemons created: " + totalLutemonsCreated +
                  ", Battles: " + totalBattles +
                  ", Training sessions: " + totalTrainingSessions);

        // Reset all statistics to zero
        totalLutemonsCreated = 0;
        totalBattles = 0;
        totalTrainingSessions = 0;

        Gdx.app.log("StatisticsManager", "Statistics reset complete");
    }

    /**
     * Sets the total number of Lutemons created.
     * Used when loading from save file.
     *
     * @param totalLutemonsCreated The total number of Lutemons created
     */
    public synchronized void setTotalLutemonsCreated(int totalLutemonsCreated) {
        Gdx.app.log("StatisticsManager", "Setting total Lutemons created: " + totalLutemonsCreated);
        this.totalLutemonsCreated = totalLutemonsCreated;
    }

    /**
     * Sets the total number of battles.
     * Used when loading from save file.
     *
     * @param totalBattles The total number of battles
     */
    public synchronized void setTotalBattles(int totalBattles) {
        Gdx.app.log("StatisticsManager", "Setting total battles: " + totalBattles);
        this.totalBattles = totalBattles;
    }

    /**
     * Sets the total number of training sessions.
     * Used when loading from save file.
     *
     * @param totalTrainingSessions The total number of training sessions
     */
    public synchronized void setTotalTrainingSessions(int totalTrainingSessions) {
        Gdx.app.log("StatisticsManager", "Setting total training sessions: " + totalTrainingSessions);
        this.totalTrainingSessions = totalTrainingSessions;
    }

    // For serialization purposes
    protected Object readResolve() {
        instance = this;
        return instance;
    }

    /**
     * Class to hold performance statistics for a single Lutemon.
     */
    public static class LutemonPerformance implements Serializable {
        private static final long serialVersionUID = 1L;
        private int battles;
        private int wins;
        private int trainingDays;

        // No-arg constructor for serialization
        public LutemonPerformance() {
            this.battles = 0;
            this.wins = 0;
            this.trainingDays = 0;
        }

        public LutemonPerformance(int battles, int wins, int trainingDays) {
            this.battles = battles;
            this.wins = wins;
            this.trainingDays = trainingDays;
        }

        public int getBattles() {
            return battles;
        }

        public int getWins() {
            return wins;
        }

        public int getTrainingDays() {
            return trainingDays;
        }

        public int getLosses() {
            return battles - wins;
        }

        public float getWinRate() {
            return battles > 0 ? (float) wins / battles : 0;
        }
    }
}
