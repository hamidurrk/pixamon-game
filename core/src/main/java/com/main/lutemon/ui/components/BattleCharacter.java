package com.main.lutemon.ui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.main.lutemon.model.battle.BattleLutemon;
import com.main.lutemon.utils.AnimationManager;

/**
 * A UI component that displays an animated Lutemon character in battle.
 */
public class BattleCharacter extends Widget {
    private final BattleLutemon battleLutemon;
    private final AnimationManager animationManager;
    private float scale;

    /**
     * Creates a new battle character.
     *
     * @param battleLutemon The battle Lutemon to display
     * @param scale The scale to apply to the character
     */
    public BattleCharacter(BattleLutemon battleLutemon, float scale) {
        this.battleLutemon = battleLutemon;
        this.animationManager = AnimationManager.getInstance();
        this.scale = scale;

        // Set initial size (will be updated in draw)
        setSize(100, 100);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Update position based on battle Lutemon
        setPosition(battleLutemon.getPosition().x, battleLutemon.getPosition().y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        String lutemonType = battleLutemon.getLutemon().getType().toString().toLowerCase();
        String animationType = getAnimationTypeString(battleLutemon.getAnimationState());

        // Use the specific state time from the battle lutemon for better animation control
        TextureRegion currentFrame = animationManager.getFrame(lutemonType, animationType, battleLutemon.getStateTime());

        if (currentFrame != null) {
            float textureWidth = currentFrame.getRegionWidth();
            float textureHeight = currentFrame.getRegionHeight();

            // Update widget size based on texture
            setSize(textureWidth * scale, textureHeight * scale);

            // Determine if we need to flip the texture based on direction
            boolean flipX = battleLutemon.getDirection() == BattleLutemon.Direction.LEFT;

            // Apply color tint based on state
            Color originalColor = batch.getColor().cpy();
            if (battleLutemon.isHurt()) {
                batch.setColor(1.0f, 0.5f, 0.5f, 1.0f);
            }

            // Draw the character with proper flipping
            if (flipX) {
                batch.draw(
                    currentFrame,
                    getX() + textureWidth * scale, getY(),
                    0, 0,
                    textureWidth, textureHeight,
                    -scale, scale,
                    0
                );
            } else {
                batch.draw(
                    currentFrame,
                    getX(), getY(),
                    0, 0,
                    textureWidth, textureHeight,
                    scale, scale,
                    0
                );
            }

            batch.setColor(originalColor);
        } else {
            Gdx.app.error("BattleCharacter",
                "No frame found for lutemon type: " + lutemonType +
                " and animation: " + animationType);
        }
    }

    /**
     * Converts an animation state to its corresponding string representation.
     *
     * @param state The animation state
     * @return The string representation of the animation state
     */
    private String getAnimationTypeString(BattleLutemon.AnimationState state) {
        switch (state) {
            case IDLE: return "idle";
            case RUN: return "run";
            case ATTACK: return "attack";
            case HURT: return "hurt";
            case DIE: return "die";
            default: return "idle";
        }
    }

    /**
     * Sets the scale of the character.
     *
     * @param scale The new scale
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Gets the battle Lutemon associated with this character.
     *
     * @return The battle Lutemon
     */
    public BattleLutemon getBattleLutemon() {
        return battleLutemon;
    }
}
