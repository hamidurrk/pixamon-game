package com.main.lutemon.ui.fragments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.main.lutemon.model.lutemon.*;
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
    private final float width;
    private final float height;

    public HomeFragment(HomeScreen screen, Skin skin, float width, float height) {
        this.screen = screen;
        this.skin = skin;
        this.width = width;
        this.height = height;

        // Debug initialization
        Gdx.app.log("HomeFragment", "Initializing HomeFragment");

        table = new Table();
        table.setSize(width, height);  // Set exact size

        lutemonTable = new Table();
        lutemonTable.top();

        scrollPane = new ScrollPane(lutemonTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);

        // Add scrollPane to main table with exact sizing
        table.add(scrollPane)
            .width(width)
            .height(height)
            .expand();

        // Create default Lutemons automatically
        createDefaultLutemons();

        // Debug final setup
        Gdx.app.log("HomeFragment", String.format("HomeFragment initialized with dimensions: %.2f x %.2f", width, height));
    }

    public void updateLutemonList() {
        lutemonTable.clear();
        lutemonTable.setSize(width, height);  // Set exact size

        List<Lutemon> lutemons = Storage.getInstance().getLutemonsAtLocation(Storage.Location.HOME);

        float padding = Constants.getPadding();
        float rowHeight = height * 0.17f;  // Adjust row height based on container height

        // Headers
        Table headerRow = new Table();
        headerRow.add(new Label("Name", skin)).expandX().left().pad(padding);
        headerRow.add(new Label("Stats", skin)).expandX().left().pad(padding);
        lutemonTable.add(headerRow).expandX().fillX().pad(5).row();

        // Debug the table structure
        Gdx.app.log("HomeFragment", "Added headers to lutemonTable");

        // Add separator
        Table separator = new Table();
        separator.setBackground(createColoredBackground(new Color(1, 1, 1, 0.3f)));
        lutemonTable.add(separator).height(2).expandX().fillX().pad(2).row();

        for (Lutemon lutemon : lutemons) {
            // Debug each Lutemon
            Gdx.app.log("HomeFragment", "Adding Lutemon: " + lutemon.getName());

            Table lutemonRow = new Table();
            lutemonRow.setBackground(createLutemonBackground(lutemon));

            // Name column
            Label nameLabel = new Label(lutemon.getName(), skin);
            lutemonRow.add(nameLabel).expandX().left().pad(padding);

            // Stats column
            Table statsTable = new Table();
            statsTable.add(new Label("HP: " + lutemon.getStats().getCurrentHealth() + "/" +
                         lutemon.getStats().getMaxHealth(), skin)).left().row();
            statsTable.add(new Label("ATK: " + lutemon.getStats().getAttack() +
                         " DEF: " + lutemon.getStats().getDefense(), skin)).left().row();
            statsTable.add(new Label("EXP: " + lutemon.getStats().getExperience(), skin)).left();
            lutemonRow.add(statsTable).expandX().left().pad(padding);

            lutemonTable.add(lutemonRow).expandX().fillX().height(rowHeight).pad(2).row();
        }

        // Ensure the tables are properly sized
        lutemonTable.setFillParent(true);
        table.setFillParent(true);

        // Debug final table structure
        Gdx.app.log("HomeFragment", "Table structure: " + table.toString());
    }

    private void createDefaultLutemons() {
        Storage storage = Storage.getInstance();

        // Add debug logging
        Gdx.app.log("HomeFragment", "Creating default Lutemons");

        // Create default Lutemons if they don't exist
        if (storage.getLutemonsAtLocation(Storage.Location.HOME).isEmpty()) {
            storage.addLutemon(new WhiteLutemon(storage.getNextId(), "White Warrior"));
            storage.addLutemon(new GreenLutemon(storage.getNextId(), "Green Fighter"));
            storage.addLutemon(new PinkLutemon(storage.getNextId(), "Pink Striker"));
            storage.addLutemon(new OrangeLutemon(storage.getNextId(), "Orange Blade"));
            storage.addLutemon(new BlackLutemon(storage.getNextId(), "Black Shadow"));

            // Verify Lutemons were added
            Gdx.app.log("HomeFragment", "Created " +
                storage.getLutemonsAtLocation(Storage.Location.HOME).size() + " default Lutemons");
        }

        updateLutemonList();
    }

    private TextureRegionDrawable createLutemonBackground(Lutemon lutemon) {
        Color color;
        switch (lutemon.getType()) {
            case WHITE:
                color = new Color(1, 1, 1, 0.2f);
                break;
            case GREEN:
                color = new Color(0, 1, 0, 0.2f);
                break;
            case PINK:
                color = new Color(1, 0.7f, 0.7f, 0.2f);
                break;
            case ORANGE:
                color = new Color(1, 0.5f, 0, 0.2f);
                break;
            case BLACK:
                color = new Color(0.3f, 0.3f, 0.3f, 0.2f);
                break;
            default:
                color = new Color(0.5f, 0.5f, 0.5f, 0.2f);
        }
        return createColoredBackground(color);
    }

    private TextureRegionDrawable createColoredBackground(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        return background;
    }

    public Table getTable() {
        return table;
    }
}
