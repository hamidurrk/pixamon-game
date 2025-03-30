package com.main.lutemon.model.lutemon;

public enum LutemonType {
    WHITE(5, 4, 20),
    GREEN(6, 3, 19),
    PINK(7, 2, 18),
    ORANGE(8, 1, 17),
    BLACK(9, 0, 16);

    private final int attack;
    private final int defense;
    private final int maxHealth;

    LutemonType(int attack, int defense, int maxHealth) {
        this.attack = attack;
        this.defense = defense;
        this.maxHealth = maxHealth;
    }

    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getMaxHealth() { return maxHealth; }
} 