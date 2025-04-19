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

        // Create main table with spacing
        table = new Table();
        table.setFillParent(true);
        table.top(); // Align to top

        // Create scrollable table for Lutemons
        lutemonTable = new Table();
        scrollPane = new ScrollPane(lutemonTable, skin);
        scrollPane.setFadeScrollBars(false);

        float padding = Constants.getPadding();

        // Add title with proper spacing
        Label titleLabel = new Label("Your Lutemons", skin, "title");
        table.add(titleLabel).pad(padding * 2).expandX().center().row();

        // Add scroll pane with proper spacing
        table.add(scrollPane).expand().fill().pad(padding).row();

        // Create button table with proper spacing
        Table buttonTable = new Table();
        buttonTable.bottom();

        // Calculate button dimensions
        float buttonWidth = Constants.getButtonWidth() * 0.8f; // Slightly smaller buttons
        float buttonHeight = Constants.getScreenHeight() * Constants.BUTTON_HEIGHT_PERCENT * 0.8f;

        TextButton createButton = new TextButton("Create New", skin);
        TextButton trainButton = new TextButton("Train", skin);
        TextButton battleButton = new TextButton("Battle", skin);

        // Add buttons horizontally with proper spacing
        buttonTable.add(createButton).size(buttonWidth, buttonHeight).pad(padding);
        buttonTable.add(trainButton).size(buttonWidth, buttonHeight).pad(padding);
        buttonTable.add(battleButton).size(buttonWidth, buttonHeight).pad(padding);

        table.add(buttonTable).expandX().center().pad(padding);

        // Initial update
        updateLutemonList();
    }

    public void updateLutemonList() {
        lutemonTable.clear();
        List<Lutemon> lutemons = Storage.getInstance().getLutemonsAtLocation(Storage.Location.HOME);

        float padding = Constants.getPadding();

        for (Lutemon lutemon : lutemons) {
            Table lutemonRow = new Table();
            lutemonRow.setBackground(skin.getDrawable("button"));
            lutemonRow.pad(padding);

            // Create a container for lutemon info
            Table infoTable = new Table();

            // Lutemon name and stats with proper spacing
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

            // Action buttons with proper spacing
            Table actionTable = new Table();
            TextButton trainButton = new TextButton("Train", skin);
            TextButton battleButton = new TextButton("Battle", skin);

            actionTable.add(trainButton).pad(padding).width(80);
            actionTable.add(battleButton).pad(padding).width(80);

            lutemonRow.add(actionTable).right();

            lutemonTable.add(lutemonRow).expandX().fillX().pad(padding / 2).row();
        }
    }

    public Table getTable() {
        return table;
    }
}
