package com.main.lutemon.model.battle;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.main.lutemon.model.lutemon.Lutemon;

/**
 * Represents a Lutemon in battle with additional battle-specific properties and behaviors.
 */
public class BattleLutemon {
    // Animation states
    public enum AnimationState {
        IDLE, RUN, ATTACK, HURT, DIE
    }

    // Direction the Lutemon is facing
    public enum Direction {
        LEFT, RIGHT
    }

    private final Lutemon lutemon;
    private final Vector2 position;
    private final Vector2 velocity;
    private final Rectangle bounds;
    private Direction direction;
    private AnimationState animationState;
    private float stateTime;
    private boolean isAttacking;
    private boolean isHurt;
    private boolean isDead;
    private boolean isJumping;
    private float jumpVelocity;
    private float gravity;
    private float groundLevel;

    // Battle arena boundaries
    private float minX;
    private float maxX;

    // Constants
    private static final float MOVE_SPEED = 200f;
    private static final float JUMP_VELOCITY = 400f;
    private static final float GRAVITY = 800f;
    private static final float ATTACK_DURATION = 1.5f; // Increased to allow full animation to play
    private static final float HURT_DURATION = 1.5f; // Increased to allow full animation to play

    /**
     * Creates a new battle Lutemon.
     *
     * @param lutemon The base Lutemon
     * @param startX The starting X position
     * @param startY The starting Y position
     * @param arenaWidth The width of the battle arena
     */
    public BattleLutemon(Lutemon lutemon, float startX, float startY, float arenaWidth) {
        this.lutemon = lutemon;
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(0, 0);
        this.bounds = new Rectangle(startX, startY, 64, 64); // Default size, will be updated
        this.direction = Direction.RIGHT;
        this.animationState = AnimationState.IDLE;
        this.stateTime = 0;
        this.isAttacking = false;
        this.isHurt = false;
        this.isDead = false;
        this.isJumping = false;
        this.jumpVelocity = 0;
        this.gravity = GRAVITY;
        this.groundLevel = startY;

        // Set arena boundaries
        this.minX = 0;
        this.maxX = arenaWidth;
    }

    /**
     * Updates the battle Lutemon's state.
     *
     * @param delta Time elapsed since last update
     */
    public void update(float delta) {
        stateTime += delta;

        // Handle jumping and gravity
        if (isJumping) {
            position.y += jumpVelocity * delta;
            jumpVelocity -= gravity * delta;

            // Check if landed
            if (position.y <= groundLevel) {
                position.y = groundLevel;
                isJumping = false;
                jumpVelocity = 0;
            }
        }

        // Handle movement
        position.x += velocity.x * delta;

        // Enforce arena boundaries
        if (position.x < minX) {
            position.x = minX;
        } else if (position.x > maxX - bounds.width) {
            position.x = maxX - bounds.width;
        }

        // Update collision bounds
        bounds.setPosition(position);

        // Update animation state
        updateAnimationState(delta);

        // Check if dead
        if (!lutemon.isAlive() && animationState != AnimationState.DIE) {
            setAnimationState(AnimationState.DIE);
        }
    }

    /**
     * Updates the animation state based on current actions.
     *
     * @param delta Time elapsed since last update
     */
    private void updateAnimationState(float delta) {
        // If dead, keep the DIE animation
        if (isDead) {
            if (animationState != AnimationState.DIE) {
                setAnimationState(AnimationState.DIE);
            }
            return;
        }

        // Handle attack animation timing
        if (isAttacking) {
            // Make sure we're in ATTACK animation state
            if (animationState != AnimationState.ATTACK) {
                setAnimationState(AnimationState.ATTACK);
                stateTime = 0; // Reset state time when animation state changes
            }

            // Only end attack animation after the full duration has elapsed
            // This ensures the complete animation plays
            if (stateTime >= ATTACK_DURATION) {
                isAttacking = false;
                setAnimationState(AnimationState.IDLE);
            }
            return; // Don't process other animations while attacking
        }

        // Handle hurt animation timing
        if (isHurt) {
            // Make sure we're in HURT animation state
            if (animationState != AnimationState.HURT) {
                setAnimationState(AnimationState.HURT);
            }

            if (stateTime >= HURT_DURATION) {
                isHurt = false;
                if (!lutemon.isAlive()) {
                    isDead = true;
                    setAnimationState(AnimationState.DIE);
                } else {
                    setAnimationState(AnimationState.IDLE);
                }
            }
            return; // Don't process other animations while hurt
        }

        // If we're not attacking, hurt, or dead, and we're not moving, make sure we're in IDLE
        if (velocity.x == 0 && animationState != AnimationState.IDLE && !isJumping) {
            setAnimationState(AnimationState.IDLE);
        } else if (velocity.x != 0 && animationState != AnimationState.RUN && !isJumping) {
            setAnimationState(AnimationState.RUN);
        }
    }

    /**
     * Sets the animation state and resets the state time.
     *
     * @param state The new animation state
     */
    public void setAnimationState(AnimationState state) {
        if (this.animationState != state) {
            this.animationState = state;
            this.stateTime = 0;
        }
    }

    /**
     * Moves the Lutemon left.
     */
    public void moveLeft() {
        if (isAttacking || isHurt || isDead) return;

        velocity.x = -MOVE_SPEED;
        direction = Direction.LEFT;
        setAnimationState(AnimationState.RUN);
    }

    /**
     * Moves the Lutemon right.
     */
    public void moveRight() {
        if (isAttacking || isHurt || isDead) return;

        velocity.x = MOVE_SPEED;
        direction = Direction.RIGHT;
        setAnimationState(AnimationState.RUN);
    }

    /**
     * Stops the Lutemon's movement.
     */
    public void stopMoving() {
        velocity.x = 0;
        // Set to IDLE when stopping, but only if not in a special animation state
        if (!isAttacking && !isHurt && !isDead && !isJumping) {
            setAnimationState(AnimationState.IDLE);
        }
    }

    /**
     * Makes the Lutemon jump.
     */
    public void jump() {
        if (isJumping || isAttacking || isHurt || isDead) return;

        isJumping = true;
        jumpVelocity = JUMP_VELOCITY;
    }

    /**
     * Makes the Lutemon attack.
     *
     * @return True if the attack was initiated, false otherwise
     */
    public boolean attack() {
        if (isAttacking || isHurt || isDead || isJumping) return false;

        isAttacking = true;
        stateTime = 0; // Reset state time to ensure full attack animation plays
        setAnimationState(AnimationState.ATTACK);
        return true;
    }

    /**
     * Makes the Lutemon take damage.
     *
     * @param damage The amount of damage to take
     */
    public void takeDamage(int damage) {
        if (isDead) return;

        lutemon.takeDamage(damage);
        isHurt = true;
        setAnimationState(AnimationState.HURT);

        if (!lutemon.isAlive()) {
            isDead = true;
            // Death animation will be set after hurt animation finishes
        }
    }

    /**
     * Checks if this Lutemon's attack hits another Lutemon.
     *
     * @param other The other Lutemon to check
     * @return True if the attack hits, false otherwise
     */
    public boolean attackHits(BattleLutemon other) {
        if (!isAttacking) return false;

        // Create attack hitbox based on direction with increased range
        Rectangle attackBounds = new Rectangle(bounds);
        if (direction == Direction.RIGHT) {
            attackBounds.x += bounds.width;
            attackBounds.width = bounds.width * 1.5f; // Increased attack range (1.5x wider)
        } else {
            attackBounds.x -= bounds.width * 2f; // Increased attack range
            attackBounds.width = bounds.width * 2f; // Increased attack range (1.5x wider)
        }

        return attackBounds.overlaps(other.getBounds());
    }

    // Getters and setters
    public Lutemon getLutemon() { return lutemon; }
    public Vector2 getPosition() { return position; }
    public Direction getDirection() { return direction; }

    /**
     * Sets the direction the Lutemon is facing.
     *
     * @param direction The direction to face
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public AnimationState getAnimationState() { return animationState; }
    public float getStateTime() { return stateTime; }
    public Rectangle getBounds() { return bounds; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isHurt() { return isHurt; }
    public boolean isDead() { return isDead; }
    public boolean isJumping() { return isJumping; }
    public Vector2 getVelocity() { return velocity; }
}
