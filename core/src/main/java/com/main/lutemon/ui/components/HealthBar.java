package com.main.lutemon.ui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.main.lutemon.model.lutemon.Lutemon;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class HealthBar extends Actor {
    private static final float BAR_WIDTH = 200;
    private static final float BAR_HEIGHT = 20;
    private static final float TEXT_PADDING = 5;

    private final Rectangle bounds;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private Lutemon lutemon;
    private float healthPercentage;
    private int currentHealth;
    private int maxHealth;

    public HealthBar(float x, float y, float width, Lutemon lutemon) {
        this.bounds = new Rectangle(x, y, width, BAR_HEIGHT);
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.lutemon = lutemon;
        this.healthPercentage = 1.0f;
        this.currentHealth = 100;
        this.maxHealth = 100;

        if (lutemon != null) {
            this.currentHealth = lutemon.getStats().getCurrentHealth();
            this.maxHealth = lutemon.getStats().getMaxHealth();
            updateHealthPercentage();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (lutemon != null) {
            currentHealth = lutemon.getStats().getCurrentHealth();
            maxHealth = lutemon.getStats().getMaxHealth();
            updateHealthPercentage();
        }
    }

    public void update(int currentHealth, int maxHealth) {
        this.currentHealth = currentHealth;
        this.maxHealth = maxHealth;
        updateHealthPercentage();
    }

    private void updateHealthPercentage() {
        healthPercentage = maxHealth > 0 ? (float) currentHealth / maxHealth : 0;
        healthPercentage = Math.max(0, Math.min(1, healthPercentage)); // Clamp between 0 and 1
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // End the batch to draw with ShapeRenderer
        batch.end();

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);

        // Draw background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        // Draw health bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(getHealthColor(healthPercentage));
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width * healthPercentage, bounds.height);
        shapeRenderer.end();

        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        batch.begin();

    }

    private Color getHealthColor(float percentage) {
        if (percentage > 0.6f) return Color.GREEN;
        if (percentage > 0.3f) return Color.YELLOW;
        return Color.RED;
    }

    @Override
    public float getWidth() {
        return BAR_WIDTH;
    }

    @Override
    public float getHeight() {
        return BAR_HEIGHT + TEXT_PADDING + font.getLineHeight();
    }

    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }

    public void setLutemon(Lutemon lutemon) {
        this.lutemon = lutemon;
        if (lutemon != null) {
            this.currentHealth = lutemon.getStats().getCurrentHealth();
            this.maxHealth = lutemon.getStats().getMaxHealth();
            updateHealthPercentage();
        }
    }
}
