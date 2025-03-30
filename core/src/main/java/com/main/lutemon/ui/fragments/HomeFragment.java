package com.main.lutemon.ui.fragments;

import com.badlogic.gdx.scenes.scene2d.ui.*;
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

        // Create main table
        table = new Table();
        table.setFillParent(true);

        // Create scrollable table for Lutemons
        lutemonTable = new Table();
        scrollPane = new ScrollPane(lutemonTable, skin);
        scrollPane.setFadeScrollBars(false);

        // Add title
        Label titleLabel = new Label("Your Lutemons", skin);
        table.add(titleLabel).pad(20);
        table.row();

        // Add scroll pane
        table.add(scrollPane).expand().fill();
        table.row();

        // Add buttons
        Table buttonTable = new Table();
        buttonTable.bottom().pad(20);

        TextButton createButton = new TextButton("Create New Lutemon", skin);
        TextButton trainButton = new TextButton("Go to Training", skin);
        TextButton battleButton = new TextButton("Go to Battle", skin);

        createButton.addListener(event -> {
            screen.showCreateLutemonDialog();
            return true;
        });

        trainButton.addListener(event -> {
            screen.getGame().navigateToTraining();
            return true;
        });

        battleButton.addListener(event -> {
            screen.getGame().navigateToBattle();
            return true;
        });

        buttonTable.add(createButton).pad(10).width(150);
        buttonTable.add(trainButton).pad(10).width(150);
        buttonTable.add(battleButton).pad(10).width(150);

        table.add(buttonTable);

        // Initial update
        updateLutemonList();
    }

    public void updateLutemonList() {
        lutemonTable.clear();
        List<Lutemon> lutemons = Storage.getInstance().getLutemonsAtLocation(Storage.Location.HOME);

        for (Lutemon lutemon : lutemons) {
            Table lutemonRow = new Table();
            lutemonRow.setBackground(skin.getDrawable("button"));

            // Lutemon name and type
            Label nameLabel = new Label(lutemon.getName(), skin);
            Label levelLabel = new Label("Level: " + lutemon.getLevel(), skin);
            Label healthLabel = new Label("HP: " + lutemon.getStats().getCurrentHealth() + "/" + 
                                        lutemon.getStats().getMaxHealth(), skin);
            lutemonRow.add(nameLabel).pad(5);
            lutemonRow.add(levelLabel).pad(5);
            lutemonRow.add(healthLabel).pad(5);

            // Stats
            Label expLabel = new Label("EXP: " + lutemon.getStats().getExperience(), skin);
            lutemonRow.add(expLabel).pad(5);

            // Action buttons
            TextButton trainButton = new TextButton("Train", skin);
            TextButton battleButton = new TextButton("Battle", skin);

            trainButton.addListener(event -> {
                Storage.getInstance().moveToLocation(lutemon.getId(), Storage.Location.TRAINING);
                updateLutemonList();
                return true;
            });

            battleButton.addListener(event -> {
                Storage.getInstance().moveToLocation(lutemon.getId(), Storage.Location.BATTLE);
                updateLutemonList();
                return true;
            });

            lutemonRow.add(trainButton).pad(5).width(80);
            lutemonRow.add(battleButton).pad(5).width(80);

            lutemonTable.add(lutemonRow).fillX().pad(5);
            lutemonTable.row();
        }
    }

    public Table getTable() {
        return table;
    }
} 