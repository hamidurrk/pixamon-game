package com.main.lutemon.model.lutemon;

import com.badlogic.gdx.math.Vector2;
import com.main.lutemon.model.lutemon.stats.LutemonStats;

public abstract class Lutemon {
    private int id;
    private String name;
    private final LutemonType type;
    private final Vector2 position;
    private final Vector2 velocity;
    private final LutemonStats stats;
    private boolean isAlive;
    private float stateTime;
    private float animationSpeed;

    protected Lutemon(int id, String name, LutemonType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.stats = new LutemonStats();
        this.isAlive = true;
        this.stateTime = 0;
        this.animationSpeed = 0.1f;

        initializeStats();
    }

    protected abstract void initializeStats();

    public void update(float delta) {
        stateTime += delta;
        position.add(velocity.x * delta, velocity.y * delta);
    }

    /**
     * Makes the Lutemon take damage.
     * Damage is reduced by defense, capped at 20% of max health, and health is reduced accordingly.
     * Ensures health cannot go below 0.
     *
     * @param damage The amount of damage to take
     */
    public void takeDamage(int damage) {
        if (!isAlive) return;

        int defense = stats.getDefense();
        int actualDamage = Math.max(1, damage - defense);

        int maxHealth = stats.getMaxHealth();
        int damageLimit = (int) Math.ceil(maxHealth * 0.2);
        actualDamage = Math.min(actualDamage, damageLimit);

        int currentHealth = stats.getCurrentHealth();
        int newHealth = Math.max(0, currentHealth - actualDamage);

        stats.setCurrentHealth(newHealth);
        System.out.println("Health reduced: " + currentHealth + " -> " + newHealth +
                         " (Damage taken: " + (currentHealth - newHealth) + ")");
        isAlive = newHealth > 0;
    }

    /**
     * Heals the Lutemon to full health.
     */
    public void heal() {
        stats.setCurrentHealth(stats.getMaxHealth());
        isAlive = true;
    }

    public void train() {
        stats.incrementExperience();
        stats.incrementTrainingDays();
    }

    /**
     * Adds experience points to the Lutemon.
     * This directly increases the Lutemon's stats through the LutemonStats class.
     *
     * @param amount The amount of experience points to add
     */
    public void addExperience(int amount) {
        for (int i = 0; i < amount; i++) {
            stats.incrementExperience();
        }
    }

    public void recordBattle(boolean won) {
        stats.incrementBattles();
        if (won) {
            stats.incrementWins();
            stats.incrementExperience(); // Bonus experience for winning
            System.out.println(name + " gained 1 experience point for winning");
        }
    }

    public int getAttackDamage() {
        return stats.getAttack();
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LutemonType getType() { return type; }
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public LutemonStats getStats() { return stats; }
    public boolean isAlive() { return isAlive; }
    public float getStateTime() { return stateTime; }
    public float getAnimationSpeed() { return animationSpeed; }
    public void setAnimationSpeed(float speed) { this.animationSpeed = speed; }

    // Additional getters for stats
    public int getLevel() { return stats.getLevel(); }
    public int getExperience() { return stats.getExperience(); }
    public int getWins() { return stats.getWins(); }
    public int getLosses() { return stats.getLosses(); }
    public int getAttack() { return stats.getAttack(); }
}
