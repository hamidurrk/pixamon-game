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

    /**
     * Gets the effective attack value including experience bonuses.
     * For every 10 experience points, attack increases by 1.
     *
     * @return The effective attack value
     */
    public int getEffectiveAttack() {
        return attack + (experience / 10);
    }

    /**
     * Gets the effective defense value including experience bonuses.
     * For every 15 experience points, defense increases by 1.
     *
     * @return The effective defense value
     */
    public int getEffectiveDefense() {
        return defense + (experience / 15);
    }

    /**
     * Gets the effective max health including experience bonuses.
     * For every 5 experience points, max health increases by 1.
     *
     * @return The effective max health value
     */
    public int getEffectiveMaxHealth() {
        return maxHealth + (experience / 5);
    }

    /**
     * Increments the experience points by 10.
     * Experience directly affects stats through the getEffective* methods.
     */
    public void incrementExperience() {
        experience += 1;
        // Update current health if it was at max before
        if (currentHealth == maxHealth || currentHealth == getEffectiveMaxHealth()) {
            currentHealth = getEffectiveMaxHealth();
        }
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
    public int getMaxHealth() { return getEffectiveMaxHealth(); }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public int getCurrentHealth() { return currentHealth; }
    public void setCurrentHealth(int currentHealth) { this.currentHealth = currentHealth; }
    public int getAttack() { return getEffectiveAttack(); }
    public void setAttack(int attack) { this.attack = attack; }
    public int getDefense() { return getEffectiveDefense(); }
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
