package com.main.lutemon.model.battle;

import com.main.lutemon.model.lutemon.Lutemon;
import java.util.Random;

/**
 * Represents a battle between two Lutemons.
 */
public class Battle {
    private final Lutemon playerLutemon;
    private final Lutemon enemyLutemon;
    private final Random random;
    private BattleState state;
    private boolean isPlayerTurn;
    private float turnTimer;
    private static final float TURN_DURATION = 1.0f;

    /**
     * Creates a new battle between two Lutemons.
     *
     * @param playerLutemon The player's Lutemon
     * @param enemyLutemon The enemy Lutemon
     */
    public Battle(Lutemon playerLutemon, Lutemon enemyLutemon) {
        this.playerLutemon = playerLutemon;
        this.enemyLutemon = enemyLutemon;
        this.random = new Random();
        this.state = BattleState.STARTING;
        this.isPlayerTurn = true;
        this.turnTimer = 0;
    }

    /**
     * Updates the battle state based on elapsed time.
     *
     * @param delta Time elapsed since last update
     */
    public void update(float delta) {
        if (state != BattleState.IN_PROGRESS) return;

        turnTimer += delta;
        if (turnTimer >= TURN_DURATION) {
            turnTimer = 0;
            if (!isPlayerTurn) {
                // AI's turn
                performAITurn();
                isPlayerTurn = true;
            }
        }
    }

    /**
     * Performs an action based on the player's input.
     *
     * @param action Action name ("ATTACK", "DEFEND", or "SPECIAL")
     */
    public void performAction(String action) {
        if (state != BattleState.IN_PROGRESS || !isPlayerTurn) return;

        switch (action.toUpperCase()) {
            case "ATTACK":
                performPlayerAction(BattleAction.ATTACK);
                break;
            case "DEFEND":
                performPlayerAction(BattleAction.DEFEND);
                break;
            case "SPECIAL":
                performPlayerAction(BattleAction.SPECIAL);
                break;
        }
    }

    /**
     * Performs a battle action for the player.
     *
     * @param action The action to perform
     */
    public void performPlayerAction(BattleAction action) {
        if (state != BattleState.IN_PROGRESS || !isPlayerTurn) return;

        switch (action) {
            case ATTACK:
                performAttack(playerLutemon, enemyLutemon);
                break;
            case DEFEND:
                performDefend(playerLutemon);
                break;
            case SPECIAL:
                performSpecial(playerLutemon, enemyLutemon);
                break;
        }

        isPlayerTurn = false;
        turnTimer = 0;
    }

    /**
     * Performs an AI turn.
     */
    private void performAITurn() {
        // Simple AI: Randomly choose between attack and defend
        if (random.nextBoolean()) {
            performAttack(enemyLutemon, playerLutemon);
        } else {
            performDefend(enemyLutemon);
        }
    }

    /**
     * Performs an attack action.
     *
     * @param attacker The attacking Lutemon
     * @param defender The defending Lutemon
     */
    private void performAttack(Lutemon attacker, Lutemon defender) {
        // Simple randomness: attack value plus or minus 1
        int attackValue = attacker.getStats().getAttack();
        int randomVariation = random.nextInt(3) - 1; // -1, 0, or 1
        int damage = Math.max(1, attackValue + randomVariation); // Ensure at least 1 damage

        // Debug output
        System.out.println("\n--- BATTLE ATTACK ---");
        System.out.println("Battle: Attack with damage: " + damage +
                         " (Attack: " + attackValue + ", Variation: " + randomVariation + ")");

        // Apply damage (defense and 20% cap are handled in the takeDamage method)
        defender.takeDamage(damage);

        // Record battle stats if this is a finishing blow
        if (!defender.isAlive()) {
            attacker.recordBattle(true);
            defender.recordBattle(false);
            state = BattleState.FINISHED;
            System.out.println("Battle: Defender died!");
        }
    }

    /**
     * Performs a defend action.
     *
     * @param defender The defending Lutemon
     */
    private void performDefend(Lutemon defender) {
        // Temporary defense boost
        defender.getStats().setDefense(defender.getStats().getDefense() + 2);
    }

    /**
     * Performs a special attack action.
     *
     * @param attacker The attacking Lutemon
     * @param defender The defending Lutemon
     */
    private void performSpecial(Lutemon attacker, Lutemon defender) {
        // Special attack with higher damage but lower accuracy
        if (random.nextFloat() < 0.7f) {
            // For special attacks, use attack value + 2 (with small random variation)
            int attackValue = attacker.getStats().getAttack();
            int randomVariation = random.nextInt(3) - 1; // -1, 0, or 1
            int damage = Math.max(1, attackValue + 2 + randomVariation); // Special attack bonus + variation

            // Debug output
            System.out.println("\n--- BATTLE SPECIAL ATTACK ---");
            System.out.println("Battle: Special attack with damage: " + damage +
                             " (Attack: " + attackValue + ", Bonus: 2, Variation: " + randomVariation + ")");

            // Apply damage (defense and 20% cap are handled in the takeDamage method)
            defender.takeDamage(damage);

            // Check if this was a finishing blow
            if (!defender.isAlive()) {
                attacker.recordBattle(true);
                defender.recordBattle(false);
                state = BattleState.FINISHED;
                System.out.println("Battle: Defender died from special attack!");
            }
        } else {
            System.out.println("Battle: Special attack missed!");
        }
    }

    /**
     * Starts the battle.
     */
    public void start() {
        state = BattleState.IN_PROGRESS;
    }

    // Getters and setters
    public BattleState getState() { return state; }

    /**
     * Sets the battle state.
     *
     * @param newState The new battle state
     */
    public void setState(BattleState newState) {
        this.state = newState;
    }

    public boolean isPlayerTurn() { return isPlayerTurn; }
    public Lutemon getPlayerLutemon() { return playerLutemon; }
    public Lutemon getEnemyLutemon() { return enemyLutemon; }
    public float getTurnTimer() { return turnTimer; }
    public float getTurnDuration() { return TURN_DURATION; }
}