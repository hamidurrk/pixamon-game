package com.main.lutemon.ui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.main.lutemon.model.battle.Battle;
import com.main.lutemon.model.battle.BattleLutemon;
import com.main.lutemon.model.battle.BattleState;
import com.main.lutemon.utils.AssetLoader;

/**
 * A UI component that displays the battle arena with the battling Lutemons.
 */
public class BattleArena extends Group {
    private final float width;
    private final float height;
    private final TextureRegion backgroundTexture;
    private final BattleCharacter playerCharacter;
    private final BattleCharacter enemyCharacter;
    private final HealthBar playerHealthBar;
    private final HealthBar enemyHealthBar;
    private final Label playerNameLabel;
    private final Label enemyNameLabel;
    private final BattleLutemon playerLutemon;
    private final BattleLutemon enemyLutemon;
    private final Battle battle;

    /**
     * Creates a new battle arena.
     *
     * @param width The width of the arena
     * @param height The height of the arena
     * @param battle The battle to display
     * @param skin The skin to use for UI elements
     * @param assetLoader The asset loader to use for loading textures
     */
    public BattleArena(float width, float height, Battle battle, Skin skin, AssetLoader assetLoader) {
        this.width = width;
        this.height = height;
        this.battle = battle;

        // Load background texture
        this.backgroundTexture = assetLoader.getBackground("battle");

        // Create battle Lutemons
        float groundLevel = height * 0.1f; // Lower ground level to accommodate larger characters
        this.playerLutemon = new BattleLutemon(
            battle.getPlayerLutemon(),
            width * 0.15f, // Position player more to the left
            groundLevel,
            width
        );

        this.enemyLutemon = new BattleLutemon(
            battle.getEnemyLutemon(),
            width * 0.85f, // Position enemy more to the right
            groundLevel,
            width
        );

        // Set enemy to face left
        this.enemyLutemon.setDirection(BattleLutemon.Direction.LEFT);
        this.enemyLutemon.setAnimationState(BattleLutemon.AnimationState.IDLE);
        this.enemyLutemon.stopMoving();
        this.enemyLutemon.getPosition().x = width * 0.85f - 100;

        // Create battle characters
        this.playerCharacter = new BattleCharacter(playerLutemon, 9.0f);
        this.enemyCharacter = new BattleCharacter(enemyLutemon, 9.0f);

        // Create health bars
        this.playerHealthBar = new HealthBar(width * 0.1f, height * 1.5f, width * 0.3f, battle.getPlayerLutemon());
        this.enemyHealthBar = new HealthBar(width * 0.6f, height * 1.5f, width * 0.3f, battle.getEnemyLutemon());

        // Create name labels
        this.playerNameLabel = new Label(battle.getPlayerLutemon().getName(), skin);
        this.playerNameLabel.setPosition(width * 0.1f, height * 0.85f);
        this.playerNameLabel.setColor(Color.WHITE);

        this.enemyNameLabel = new Label(battle.getEnemyLutemon().getName(), skin);
        this.enemyNameLabel.setPosition(width * 0.6f, height * 0.85f);
        this.enemyNameLabel.setColor(Color.WHITE);

        // Add actors to group
        addActor(playerCharacter);
        addActor(enemyCharacter);
        addActor(playerHealthBar);
        addActor(enemyHealthBar);
        addActor(playerNameLabel);
        addActor(enemyNameLabel);

        // Set size
        setSize(width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Always update animations even if battle is finished
        playerLutemon.update(delta);
        enemyLutemon.update(delta);

        // Only process gameplay logic if battle is still in progress
        if (battle.getState() == BattleState.IN_PROGRESS) {
            // Check for attacks - only apply damage once per attack animation
            if (playerLutemon.isAttacking() && !playerLutemon.hasDealtDamage() && playerLutemon.attackHits(enemyLutemon)) {
                // Mark that damage has been dealt for this attack
                playerLutemon.setHasDealtDamage(true);

                // Get attack value from player Lutemon
                int attackValue = playerLutemon.getLutemon().getStats().getAttack();

                // Add simple randomness: attack value plus or minus 1
                int randomVariation = (int)(Math.random() * 3) - 1; // -1, 0, or 1
                int damage = Math.max(1, attackValue + randomVariation); // Ensure at least 1 damage

                // Debug output
                Gdx.app.log("BattleArena", "\n--- PLAYER ATTACK ---");
                Gdx.app.log("BattleArena", "Player attacks with damage: " + damage +
                            " (Attack: " + attackValue + ", Variation: " + randomVariation + ")");

                // Apply damage to enemy Lutemon (defense and 20% cap are handled in the takeDamage method)
                enemyLutemon.takeDamage(damage);

                // Check if enemy died
                if (!enemyLutemon.getLutemon().isAlive()) {
                    enemyLutemon.setAnimationState(BattleLutemon.AnimationState.DIE);
                    battle.setState(BattleState.FINISHED);
                    System.out.println("Enemy died!");
                }
            }

            if (enemyLutemon.isAttacking() && !enemyLutemon.hasDealtDamage() && enemyLutemon.attackHits(playerLutemon)) {
                // Mark that damage has been dealt for this attack
                enemyLutemon.setHasDealtDamage(true);

                // Get attack value from enemy Lutemon
                int attackValue = enemyLutemon.getLutemon().getStats().getAttack();

                // Add simple randomness: attack value plus or minus 1
                int randomVariation = (int)(Math.random() * 3) - 1; // -1, 0, or 1
                int damage = Math.max(1, attackValue + randomVariation); // Ensure at least 1 damage

                // Debug output
                Gdx.app.log("BattleArena", "\n--- ENEMY ATTACK ---");
                Gdx.app.log("BattleArena", "Enemy attacks with damage: " + damage +
                            " (Attack: " + attackValue + ", Variation: " + randomVariation + ")");

                // Apply damage to player Lutemon (defense and 20% cap are handled in the takeDamage method)
                playerLutemon.takeDamage(damage);

                // Check if player died
                if (!playerLutemon.getLutemon().isAlive()) {
                    playerLutemon.setAnimationState(BattleLutemon.AnimationState.DIE);
                    battle.setState(BattleState.FINISHED);
                    System.out.println("Player died!");
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Draw background
        batch.draw(backgroundTexture, getX(), getY(), width, height);

        // Draw actors
        super.draw(batch, parentAlpha);
    }

    /**
     * Moves the player Lutemon left.
     */
    public void movePlayerLeft() {
        playerLutemon.moveLeft();
    }

    /**
     * Moves the player Lutemon right.
     */
    public void movePlayerRight() {
        playerLutemon.moveRight();
    }

    /**
     * Stops the player Lutemon's movement.
     */
    public void stopPlayerMovement() {
        playerLutemon.stopMoving();
    }

    /**
     * Makes the player Lutemon attack.
     */
    public void playerAttack() {
        playerLutemon.attack();
    }

    /**
     * Makes the player Lutemon defend (jump).
     */
    public void playerDefend() {
        playerLutemon.jump();
    }

    /**
     * Gets the player's battle Lutemon.
     *
     * @return The player's battle Lutemon
     */
    public BattleLutemon getPlayerLutemon() {
        return playerLutemon;
    }

    /**
     * Gets the enemy's battle Lutemon.
     *
     * @return The enemy's battle Lutemon
     */
    public BattleLutemon getEnemyLutemon() {
        return enemyLutemon;
    }

    // AI state tracking
    private float aiActionTimer = 0;
    private float aiActionInterval = 0.8f; // Time between AI decisions
    private boolean aiWantsToAttack = false;
    private boolean aiWantsToDefend = false;

    /**
     * Updates the enemy AI.
     *
     * @param delta Time elapsed since last update
     */
    public void updateEnemyAI(float delta) {
        // Don't update AI if enemy is in a special state
        if (enemyLutemon.isAttacking() || enemyLutemon.isHurt() || enemyLutemon.isDead()) {
            return;
        }

        // Update AI action timer
        aiActionTimer += delta;

        // Simple AI: move towards player if far away, attack if close
        float distance = Math.abs(enemyLutemon.getPosition().x - playerLutemon.getPosition().x);

        // Make decisions at regular intervals for more consistent behavior
        if (aiActionTimer >= aiActionInterval) {
            aiActionTimer = 0;

            // Reset action flags
            aiWantsToAttack = false;
            aiWantsToDefend = false;

            // Decide on action based on distance
            if (distance <= 300) { // Reduced attack threshold for more aggressive AI
                // Close enough to attack
                if (Math.random() < 0.7) { // 70% chance to attack when in range
                    aiWantsToAttack = true;
                } else if (Math.random() < 0.3) { // 30% chance to defend
                    aiWantsToDefend = true;
                }
            }
        }

        // Execute the decided actions
        if (aiWantsToAttack) {
            enemyLutemon.stopMoving();
            enemyLutemon.attack();
            aiWantsToAttack = false; // Reset after executing
        } else if (aiWantsToDefend) {
            enemyLutemon.stopMoving();
            enemyLutemon.jump();
            aiWantsToDefend = false; // Reset after executing
        } else {
            // Movement logic - approach player if not close enough
            if (distance > 300) {
                // Move towards player
                if (enemyLutemon.getPosition().x > playerLutemon.getPosition().x) {
                    enemyLutemon.moveLeft();
                } else {
                    enemyLutemon.moveRight();
                }
            } else {
                // In attack range but not attacking - stop and wait
                enemyLutemon.stopMoving();
            }
        }
    }
}
