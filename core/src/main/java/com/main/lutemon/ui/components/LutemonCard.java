package com.main.lutemon.ui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.model.lutemon.LutemonType;
import com.main.lutemon.utils.AssetLoader;

public class LutemonCard extends Card {
    private final Lutemon lutemon;
    private final AssetLoader assetLoader;

    public LutemonCard(Lutemon lutemon, float width, Skin skin) {
        super(width, skin);
        this.lutemon = lutemon;
        this.assetLoader = AssetLoader.getInstance();

        // Debug
        setDebug(true);
        Gdx.app.log("LutemonCard", "Creating card for " + lutemon.getName() +
                   " with dimensions: " + width + "x" + height);
    }

    @Override
    protected Table createTitleSection() {
        Table titleTable = new Table();
        titleTable.setDebug(true); // Debug

        Label nameLabel = new Label(lutemon.getName(), skin, "title");
        nameLabel.setColor(Color.WHITE);
        titleTable.add(nameLabel).expandX().left().pad(width * 0.02f);

        Label typeLabel = new Label(lutemon.getType().name(), skin);
        typeLabel.setColor(getTypeColor(lutemon.getType()));
        titleTable.add(typeLabel).right().pad(width * 0.02f);

        return titleTable;
    }

    @Override
    protected Table createImageSection() {
        Table imageTable = new Table();
        imageTable.setDebug(true); // Debug

        // Try to load Lutemon's image from assets
        String imagePath = "lutemons/" + lutemon.getType().name().toLowerCase() + ".png";
        Texture texture = assetLoader.getUIElement(imagePath);

        Image image = new Image(texture);
        imageTable.add(image).size(width * 0.8f).center();

        return imageTable;
    }

    @Override
    protected Table createStatsSection() {
        Table statsTable = new Table();
        statsTable.setDebug(true); // Debug

        // Left column
        Table leftStats = new Table();
        leftStats.defaults().left().pad(5);
        leftStats.add(new Label("HP:", skin)).row();
        leftStats.add(new Label("ATK:", skin)).row();
        leftStats.add(new Label("DEF:", skin));

        // Right column
        Table rightStats = new Table();
        rightStats.defaults().left().pad(5);
        rightStats.add(new Label(
            lutemon.getStats().getCurrentHealth() + "/" + lutemon.getStats().getMaxHealth(),
            skin)).row();
        rightStats.add(new Label(
            String.valueOf(lutemon.getStats().getAttack()),
            skin)).row();
        rightStats.add(new Label(
            String.valueOf(lutemon.getStats().getDefense()),
            skin));

        statsTable.add(leftStats).pad(width * 0.02f);
        statsTable.add(rightStats).expandX().left().pad(width * 0.02f);

        // Experience row
        Table expTable = new Table();
        Label expLabel = new Label("EXP: " + lutemon.getStats().getExperience(), skin);
        expTable.add(expLabel).center();

        statsTable.row();
        statsTable.add(expTable).colspan(2).center().padTop(width * 0.02f);

        return statsTable;
    }

    private Color getTypeColor(LutemonType type) {
        switch (type) {
            case WHITE: return Color.WHITE;
            case GREEN: return Color.GREEN;
            case PINK: return new Color(1, 0.7f, 0.7f, 1);
            case ORANGE: return new Color(1, 0.5f, 0, 1);
            case BLACK: return new Color(0.3f, 0.3f, 0.3f, 1);
            default: return Color.WHITE;
        }
    }
}
