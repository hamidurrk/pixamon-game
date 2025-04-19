package com.main.lutemon.ui.fragments;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.model.storage.Storage;
import com.main.lutemon.screens.HomeScreen;
import com.main.lutemon.utils.Constants;

import java.util.List;

public class HomeFragment {
    private final HomeScreen screen;
    private final Table table;
    private final ScrollPane scrollPane;
    private final Table lutemonTable;
    private final Skin skin;

    public HomeFragment(HomeScreen screen, Skin skin) {
        this.screen = screen;
        this.skin = skin;

        table = new Table();
        table.top();

        // Create a placeholder background
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.2f, 0.2f, 0.2f, 0.8f); // Semi-transparent dark gray
        pixmap.fill();
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        table.setBackground(background);

        lutemonTable = new Table();

        scrollPane = new ScrollPane(lutemonTable, skin);
        scrollPane.setFadeScrollBars(false);

        float padding = Constants.getPadding();

        Label subtitleLabel = new Label("Your Lutemons", skin);
        table.add(subtitleLabel).pad(padding).row();

        table.add(scrollPane).expand().fill().pad(padding);

        updateLutemonList();
    }

    public void updateLutemonList() {
        lutemonTable.clear();
        List<Lutemon> lutemons = Storage.getInstance().getLutemonsAtLocation(Storage.Location.HOME);

        float padding = Constants.getPadding();

        if (lutemons.isEmpty()) {
            Label emptyLabel = new Label("No Lutemons available", skin);
            lutemonTable.add(emptyLabel).pad(padding).center();
            return;
        }

        for (Lutemon lutemon : lutemons) {
            Table lutemonRow = new Table();
            lutemonRow.pad(padding);

            // Lutemon info
            Table infoTable = new Table();
            Label nameLabel = new Label(lutemon.getName(), skin);
            Label levelLabel = new Label("Level: " + lutemon.getLevel(), skin);
            Label healthLabel = new Label("HP: " + lutemon.getStats().getCurrentHealth() + "/" +
                                        lutemon.getStats().getMaxHealth(), skin);
            Label expLabel = new Label("EXP: " + lutemon.getStats().getExperience(), skin);

            infoTable.add(nameLabel).pad(padding).left();
            infoTable.add(levelLabel).pad(padding);
            infoTable.add(healthLabel).pad(padding);
            infoTable.add(expLabel).pad(padding);

            lutemonRow.add(infoTable).expandX().left();

            // Action buttons
            Table actionTable = new Table();
            TextButton trainButton = new TextButton("Train", skin);
            TextButton battleButton = new TextButton("Battle", skin);

            float buttonWidth = 80f;

            trainButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Storage.getInstance().moveToLocation(lutemon.getId(), Storage.Location.TRAINING);
                    updateLutemonList();
                }
            });

            battleButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Storage.getInstance().moveToLocation(lutemon.getId(), Storage.Location.BATTLE);
                    updateLutemonList();
                }
            });

            actionTable.add(trainButton).pad(padding).width(buttonWidth);
            actionTable.add(battleButton).pad(padding).width(buttonWidth);

            lutemonRow.add(actionTable).right();

            lutemonTable.add(lutemonRow).expandX().fillX().pad(padding / 2).row();
        }
    }

    public Table getTable() {
        return table;
    }
}
