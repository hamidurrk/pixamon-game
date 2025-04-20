package com.main.lutemon.ui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.main.lutemon.utils.AnimationManager;

public class AnimatedAvatar extends Widget {
    private final String lutemonType;
    private final AnimationManager animationManager;

    public AnimatedAvatar(String lutemonType, float size) {
        this.lutemonType = lutemonType.toLowerCase();
        this.animationManager = AnimationManager.getInstance();
        setSize(size, size);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        animationManager.update(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animationManager.getCurrentFrame(lutemonType, "idle");

        if (currentFrame != null) {
            float textureWidth = currentFrame.getRegionWidth();
            float textureHeight = currentFrame.getRegionHeight();
            float scale = Math.min(getWidth() / textureWidth, getHeight() / textureHeight);

            float x = getX() + (getWidth() - textureWidth * scale) / 2;
            float y = getY() + (getHeight() - textureHeight * scale) / 2;

            batch.draw(currentFrame,
                      x, y,
                      0, 0,
                      textureWidth, textureHeight,
                      scale, scale,
                      0);
        } else {
            Gdx.app.error("AnimatedAvatar", "No frame found for lutemon type: " + lutemonType);
        }
    }
}
