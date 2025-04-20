package com.main.lutemon.ui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.utils.AnimationManager;

/**
 * A UI component that displays a training animation for a Lutemon.
 * Cycles through idle, run, and attack animations.
 */
public class TrainingAnimation extends Widget {
    private Lutemon lutemon;
    private AnimationManager animationManager;
    private float stateTime;
    private float runAnimationDuration = 3.0f; // Duration for run animation
    private float minAttackDisplayTime = 1.0f; // Minimum time to display attack animation
    private int currentAnimationIndex;
    private final String[] animationTypes = {"run", "attack"}; // Removed idle animation
    private float scale = 9.0f; // Increased size 3x
    private boolean waitingForAnimationToFinish;

    /**
     * Creates a new training animation.
     */
    public TrainingAnimation() {
        this.animationManager = AnimationManager.getInstance();
        this.stateTime = 0;
        this.currentAnimationIndex = 0;

        // Set initial size (will be updated in draw)
        setSize(200, 200);
    }

    /**
     * Sets the Lutemon to animate.
     *
     * @param lutemon The Lutemon to animate
     */
    public void setLutemon(Lutemon lutemon) {
        this.lutemon = lutemon;
        this.stateTime = 0;
        this.currentAnimationIndex = 0;
        this.waitingForAnimationToFinish = false;
    }

    /**
     * Updates the animation.
     *
     * @param delta Time elapsed since last update
     */
    public void update(float delta) {
        if (lutemon == null) return;

        stateTime += delta;

        // Get current animation type
        String currentAnimationType = animationTypes[currentAnimationIndex];
        String lutemonType = lutemon.getType().toString().toLowerCase();

        // Handle different animation types
        if (currentAnimationType.equals("attack")) {
            // For attack animation, we need to wait for it to finish
            if (!waitingForAnimationToFinish) {
                // First frame of attack animation - initialize
                stateTime = 0;
                waitingForAnimationToFinish = true;

                // Get the actual duration of this attack animation
                float actualAttackDuration = animationManager.getAnimationDuration(lutemonType, "attack");
                // Ensure we display for at least the minimum time or the full animation duration
                minAttackDisplayTime = Math.max(minAttackDisplayTime, actualAttackDuration);
            } else {
                // Check if we've displayed for minimum time AND the animation has completed at least one cycle
                boolean minTimeElapsed = stateTime >= minAttackDisplayTime;
                boolean animationComplete = animationManager.isAnimationFinished(lutemonType, "attack", stateTime);

                if (minTimeElapsed && animationComplete) {
                    // Attack animation finished and minimum duration passed, move to next animation
                    stateTime = 0;
                    waitingForAnimationToFinish = false;
                    currentAnimationIndex = (currentAnimationIndex + 1) % animationTypes.length;
                }
            }
        } else {
            // For looping animations like run
            waitingForAnimationToFinish = false;
            if (stateTime >= runAnimationDuration) {
                stateTime = 0;
                currentAnimationIndex = (currentAnimationIndex + 1) % animationTypes.length;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (lutemon == null) return;

        String lutemonType = lutemon.getType().toString().toLowerCase();
        String animationType = animationTypes[currentAnimationIndex];

        // Get the current frame using the specific state time for better control
        TextureRegion currentFrame = animationManager.getFrame(lutemonType, animationType, stateTime);

        if (currentFrame != null) {
            float textureWidth = currentFrame.getRegionWidth();
            float textureHeight = currentFrame.getRegionHeight();

            // Use fixed size for all animations to prevent jittering
            float fixedWidth = 64 * scale; // Standard width for all animations
            float fixedHeight = 64 * scale; // Standard height for all animations
            setSize(fixedWidth, fixedHeight);

            // Center the animation in the widget with fixed positioning
            float x = getX() + (fixedWidth - textureWidth * scale) / 2;
            float y = getY() + (fixedHeight - textureHeight * scale) / 2 + 20; // Added vertical offset

            // Draw the character
            batch.draw(
                currentFrame,
                x, y,
                0, 0,
                textureWidth, textureHeight,
                scale, scale,
                0
            );
        } else {
            Gdx.app.error("TrainingAnimation",
                "No frame found for lutemon type: " + lutemonType +
                " and animation: " + animationType);
        }
    }

    /**
     * Sets the scale of the animation.
     *
     * @param scale The new scale
     */
    public void setScale(float scale) {
        this.scale = scale;
    }
}
