package com.main.lutemon.model.lutemon.stats;

public class LutemonStats {
    private int maxHealth;
    private int currentHealth;
    private int attack;
    private int defense;
    private int experience;
    private int level;
    private int trainingDays;
    private int battles;
    private int wins;
    private int losses;

    public LutemonStats() {
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.attack = 10;
        this.defense = 5;
        this.experience = 0;
        this.level = 1;
        this.trainingDays = 0;
        this.battles = 0;
        this.wins = 0;
        this.losses = 0;
    }

    public void setBaseStats(int attack, int defense, int maxHealth) {
        this.attack = attack;
        this.defense = defense;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public int getEffectiveAttack() {
        return attack + (experience / 10);
    }

    public void incrementExperience() {
        experience += 10;
        if (experience >= level * 100) {
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        maxHealth += 10;
        currentHealth = maxHealth;
        attack += 2;
        defense += 1;
        experience = 0;
    }

    public void incrementTrainingDays() {
        trainingDays++;
    }

    public void incrementBattles() {
        battles++;
    }

    public void incrementWins() {
        wins++;
    }

    public void incrementLosses() {
        losses++;
    }

    // Getters and setters
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public int getCurrentHealth() { return currentHealth; }
    public void setCurrentHealth(int currentHealth) { this.currentHealth = currentHealth; }
    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }
    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getTrainingDays() { return trainingDays; }
    public void setTrainingDays(int trainingDays) { this.trainingDays = trainingDays; }
    public int getBattles() { return battles; }
    public void setBattles(int battles) { this.battles = battles; }
    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }
    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }
} 