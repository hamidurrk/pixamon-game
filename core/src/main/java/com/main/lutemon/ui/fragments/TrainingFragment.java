package com.main.lutemon.ui.fragments;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.model.storage.Storage;
import com.main.lutemon.screens.TrainingScreen;
import com.main.lutemon.utils.Constants;

import java.util.List;

public class TrainingFragment {
    private final TrainingScreen screen;
    private final Table table;
    private final ScrollPane scrollPane;
    private final Table lutemonTable;
    private final Skin skin;

    public TrainingFragment(TrainingScreen screen, Skin skin) {
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
        Label titleLabel = new Label("Training Area", skin);
        table.add(titleLabel).pad(20);
        table.row();

        // Add scroll pane
        table.add(scrollPane).expand().fill();
        table.row();

        // Add buttons
        Table buttonTable = new Table();
        buttonTable.bottom().pad(20);

        TextButton homeButton = new TextButton("Return Home", skin);
        TextButton trainAllButton = new TextButton("Train All", skin);

        homeButton.addListener(event -> {
            screen.getGame().navigateToHome();
            return true;
        });

        trainAllButton.addListener(event -> {
            trainAllLutemons();
            return true;
        });

        buttonTable.add(homeButton).pad(10).width(150);
        buttonTable.add(trainAllButton).pad(10).width(150);

        table.add(buttonTable);

        // Initial update
        updateLutemonList();
    }

    private void trainAllLutemons() {
        List<Lutemon> lutemons = Storage.getInstance().getLutemonsAtLocation(Storage.Location.TRAINING);
        for (Lutemon lutemon : lutemons) {
            Storage.getInstance().trainLutemon(lutemon.getId());
        }
        updateLutemonList();
    }

    public void updateLutemonList() {
        lutemonTable.clear();
        List<Lutemon> lutemons = Storage.getInstance().getLutemonsAtLocation(Storage.Location.TRAINING);

        for (Lutemon lutemon : lutemons) {
            Table lutemonRow = new Table();
            lutemonRow.setBackground(skin.getDrawable("button"));

            // Lutemon name and type
            Label nameLabel = new Label(lutemon.getName(), skin);
            Label typeLabel = new Label(lutemon.getType().name(), skin);
            lutemonRow.add(nameLabel).pad(5);
            lutemonRow.add(typeLabel).pad(5);

            // Stats
            Label healthLabel = new Label("HP: " + lutemon.getStats().getCurrentHealth() + "/" + 
                                        lutemon.getStats().getMaxHealth(), skin);
            Label expLabel = new Label("EXP: " + lutemon.getStats().getExperience(), skin);
            Label trainingDaysLabel = new Label("Training Days: " + lutemon.getStats().getTrainingDays(), skin);
            lutemonRow.add(healthLabel).pad(5);
            lutemonRow.add(expLabel).pad(5);
            lutemonRow.add(trainingDaysLabel).pad(5);

            // Action buttons
            TextButton trainButton = new TextButton("Train", skin);
            TextButton homeButton = new TextButton("Return Home", skin);

            trainButton.addListener(event -> {
                Storage.getInstance().trainLutemon(lutemon.getId());
                updateLutemonList();
                return true;
            });

            homeButton.addListener(event -> {
                Storage.getInstance().moveToLocation(lutemon.getId(), Storage.Location.HOME);
                updateLutemonList();
                return true;
            });

            lutemonRow.add(trainButton).pad(5).width(80);
            lutemonRow.add(homeButton).pad(5).width(100);

            lutemonTable.add(lutemonRow).fillX().pad(5);
            lutemonTable.row();
        }
    }

    public Table getTable() {
        return table;
    }
} 