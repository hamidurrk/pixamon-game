package com.main.lutemon.utils;

import com.main.lutemon.model.lutemon.Lutemon;

import java.io.Serializable;
import java.util.List;

/**
 * Class to hold all game save data.
 * This includes Lutemons and statistics.
 */
public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Lutemon> lutemons;
    private int totalLutemonsCreated;
    private int totalBattles;
    private int totalTrainingSessions;
    
    public GameSaveData() {
    }
    
    public GameSaveData(List<Lutemon> lutemons, int totalLutemonsCreated, int totalBattles, int totalTrainingSessions) {
        this.lutemons = lutemons;
        this.totalLutemonsCreated = totalLutemonsCreated;
        this.totalBattles = totalBattles;
        this.totalTrainingSessions = totalTrainingSessions;
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
    
    public int getTotalBattles() {
        return totalBattles;
    }
    
    public void setTotalBattles(int totalBattles) {
        this.totalBattles = totalBattles;
    }
    
    public int getTotalTrainingSessions() {
        return totalTrainingSessions;
    }
    
    public void setTotalTrainingSessions(int totalTrainingSessions) {
        this.totalTrainingSessions = totalTrainingSessions;
    }
}
