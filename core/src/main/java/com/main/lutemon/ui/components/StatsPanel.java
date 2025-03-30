package com.main.lutemon.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.main.lutemon.model.lutemon.Lutemon;

public class StatsPanel extends Actor {
    private static final float PADDING = 10f;
    private final Lutemon lutemon;
    private final BitmapFont font;

    public StatsPanel(Lutemon lutemon) {
        this.lutemon = lutemon;
        this.font = new BitmapFont();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float y = getY() + getHeight() - PADDING;
        
        // Draw name
        font.setColor(Color.WHITE);
        font.draw(batch, "Name: " + lutemon.getName(), getX() + PADDING, y);
        y -= font.getLineHeight() + PADDING;

        // Draw level
        font.draw(batch, "Level: " + lutemon.getLevel(), getX() + PADDING, y);
        y -= font.getLineHeight() + PADDING;

        // Draw stats
        font.draw(batch, "Attack: " + lutemon.getStats().getAttack(), getX() + PADDING, y);
        y -= font.getLineHeight() + PADDING;
        
        font.draw(batch, "Defense: " + lutemon.getStats().getDefense(), getX() + PADDING, y);
        y -= font.getLineHeight() + PADDING;
        
        font.draw(batch, "Experience: " + lutemon.getExperience(), getX() + PADDING, y);
        y -= font.getLineHeight() + PADDING;
        
        font.draw(batch, "Wins: " + lutemon.getWins(), getX() + PADDING, y);
        y -= font.getLineHeight() + PADDING;
        
        font.draw(batch, "Losses: " + lutemon.getLosses(), getX() + PADDING, y);
    }

    public void dispose() {
        font.dispose();
    }
} 