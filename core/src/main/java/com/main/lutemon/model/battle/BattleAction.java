package com.main.lutemon.model.battle;

/**
 * Represents possible actions that can be taken during a battle.
 */
public enum BattleAction {
    /** Basic attack action */
    ATTACK("Attack"),
    
    /** Defensive action that reduces incoming damage */
    DEFEND("Defend"),
    
    /** Special attack with higher damage but lower accuracy */
    SPECIAL("Special");
    
    private final String displayName;
    
    BattleAction(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Get the display name of the action.
     * @return The readable name of this action
     */
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
} 