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
import com.main.lutemon.model.storage.Storage.Location;
import com.main.lutemon.screens.HomeScreen;
import com.main.lutemon.ui.components.AnimatedAvatar;
import com.main.lutemon.utils.Constants;
import com.main.lutemon.utils.ProfileManager;

import java.util.List;

public class HomeFragment extends Table {
    private final HomeScreen screen;
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

        // Set size for this table (now 'this' is the table)
        setSize(width, height);

        lutemonTable = new Table();
        lutemonTable.top().left();
        lutemonTable.defaults().expandX().fillX();

        scrollPane = new ScrollPane(lutemonTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);
        scrollPane.setScrollingDisabled(true, false);

        // Add scrollPane to this table
        add(scrollPane)
            .width(width)
            .height(height)
            .expand().fill().top();

        createDefaultLutemons();
    }

    public void updateLutemonList() {
        lutemonTable.clear();
        lutemonTable.top().left();  // Ensure alignment to top-left

        List<Lutemon> lutemons = Storage.getInstance().getLutemonsAtLocation(Storage.Location.HOME);
        Gdx.app.log("HomeFragment", "Updating lutemon list with " + lutemons.size() + " lutemons at HOME");

        // Also log all lutemons in storage for debugging
        List<Lutemon> allLutemons = Storage.getInstance().getAllLutemons();
        Gdx.app.log("HomeFragment", "Total lutemons in storage: " + allLutemons.size());
        for (Lutemon lutemon : allLutemons) {
            Gdx.app.log("HomeFragment", "Lutemon in storage: " + lutemon.getName() +
                      ", Location: " + Storage.getInstance().getLutemonLocation(lutemon.getId()));
        }

        float padding = Constants.getPadding();
        float rowHeight = height * 0.45f;

        // Add headers
//        Table headerRow = new Table();
//        headerRow.add(new Label("Name", skin, "default")).width(width * 0.4f).left().pad(padding);
//        headerRow.add(new Label("Stats", skin, "default")).width(width * 0.6f).left().pad(padding);
//        lutemonTable.add(headerRow).expandX().fillX().pad(5).row();

        // Add separator
//        Table separator = new Table();
//        separator.setBackground(createColoredBackground(new Color(1, 1, 1, 0.3f)));
//        lutemonTable.add(separator).height(2).expandX().fillX().pad(2).row();

        // Debug the table structure
        Gdx.app.log("HomeFragment", "Added headers to lutemonTable");

        // Set fixed column widths for consistent alignment
        float avatarColumnWidth = width * 0.2f;
        float nameColumnWidth = width * 0.4f;
        float statsColumnWidth = width * 0.4f;

        for (Lutemon lutemon : lutemons) {
            // Debug each Lutemon
            Gdx.app.log("HomeFragment", "Adding Lutemon: " + lutemon.getName());

            Table lutemonRow = new Table();
            lutemonRow.setBackground(createLutemonBackground(lutemon));

            // Avatar column
            Table avatarContainer = new Table();

            // Create placeholder image with darker gray
//            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
//            pixmap.setColor(0.3f, 0.3f, 0.3f, 1);
//            pixmap.fill();
//            TextureRegionDrawable placeholder = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
//            pixmap.dispose();
//
//            Image avatarImage = new Image(placeholder);
//            avatarContainer.add(avatarImage).size(height * 0.18f).pad(padding);  // Square image, 20% of height
            float avatarSize = height * 0.8f; // 20% of fragment height
            int avatarPaddingBottom = 180;

            if (lutemon.getType() == LutemonType.ORANGE) {
                avatarSize = height * 0.4f;
                avatarPaddingBottom = 20;
            }
            if (lutemon.getType() == LutemonType.GREEN) {
                avatarSize = height * 0.6f;
                avatarPaddingBottom = 200;
            }
            if (lutemon.getType() == LutemonType.BLACK) {
                avatarPaddingBottom = 50;
            }
            if (lutemon.getType() == LutemonType.PINK) {
                avatarSize = height;
                avatarPaddingBottom = -20;
            }
            AnimatedAvatar avatar = new AnimatedAvatar(lutemon.getType().toString(), avatarSize);
            avatarContainer.add(avatar).size(avatarSize).pad(10, 5, avatarPaddingBottom, 5);

            lutemonRow.add(avatarContainer).width(avatarColumnWidth).top().pad(padding);

            // Name column
            Label nameLabel = new Label(lutemon.getName(), skin);
            lutemonRow.add(nameLabel).width(nameColumnWidth).left().pad(padding);

            // Stats column
            Table statsTable = new Table();
            statsTable.defaults().left().padBottom(5);

            // Get the stats
            int currentHealth = lutemon.getStats().getCurrentHealth();
            int maxHealth = lutemon.getStats().getMaxHealth();
            int attack = lutemon.getStats().getAttack();
            int defense = lutemon.getStats().getDefense();
            int experience = lutemon.getStats().getExperience();

            Gdx.app.log("HomeFragment", "Lutemon stats - HP: " + currentHealth + "/" + maxHealth +
                      ", ATK: " + attack + ", DEF: " + defense + ", EXP: " + experience);

            statsTable.add(new Label("HP: " + currentHealth + "/" + maxHealth, skin)).row();
            statsTable.add(new Label("ATK: " + attack + " DEF: " + defense, skin)).row();
            statsTable.add(new Label("EXP: " + experience, skin));

            lutemonRow.add(statsTable).width(statsColumnWidth).left().pad(padding);

            lutemonTable.add(lutemonRow).expandX().fillX().height(rowHeight).pad(5).row();
        }

        lutemonTable.setSize(width, height);

        scrollPane.setScrollY(0);
        scrollPane.updateVisualScroll();
    }

    private void createDefaultLutemons() {
        Storage storage = Storage.getInstance();

        Gdx.app.log("HomeFragment", "Checking if default Lutemons should be created");

        // Only create default Lutemons for new profiles
        boolean isNewProfile = ProfileManager.getInstance().isNewProfile();
        boolean hasNoLutemons = storage.getAllLutemons().isEmpty();

        Gdx.app.log("HomeFragment", "Profile is new: " + isNewProfile + ", has no lutemons: " + hasNoLutemons);

        if (hasNoLutemons && isNewProfile) {

            Gdx.app.log("HomeFragment", "Creating default Lutemons for new profile");

            // Create default lutemons and make sure they're counted in the statistics
            storage.addLutemon(new WhiteLutemon(storage.getNextId(), "White Warrior"));
            storage.addLutemon(new GreenLutemon(storage.getNextId(), "Green Fighter"));
            storage.addLutemon(new PinkLutemon(storage.getNextId(), "Pink Striker"));
            storage.addLutemon(new OrangeLutemon(storage.getNextId(), "Orange Blade"));
            storage.addLutemon(new BlackLutemon(storage.getNextId(), "Black Shadow"));

            // Log the total lutemons created
            com.main.lutemon.utils.StatisticsManager statsManager = com.main.lutemon.utils.StatisticsManager.getInstance();
            Gdx.app.log("HomeFragment", "Total lutemons created after adding default lutemons: " +
                      statsManager.getTotalLutemonsCreated());

            Gdx.app.log("HomeFragment", "Added 5 default lutemons to the new profile");

            // Verify Lutemons were added
            Gdx.app.log("HomeFragment", "Created " +
                storage.getLutemonsAtLocation(Storage.Location.HOME).size() + " default Lutemons");

            // Save the game to persist the default Lutemons
            screen.getGame().saveGame();
        } else {
            Gdx.app.log("HomeFragment", "No default Lutemons needed");

            List<Lutemon> existingLutemons = storage.getAllLutemons();
            Gdx.app.log("HomeFragment", "Found " + existingLutemons.size() + " existing Lutemons");

            // Make sure all lutemons are at HOME location
            for (Lutemon lutemon : existingLutemons) {
                Location currentLocation = storage.getLutemonLocation(lutemon.getId());
                if (currentLocation != Storage.Location.HOME) {
                    Gdx.app.log("HomeFragment", "Moving Lutemon " + lutemon.getName() + " from " +
                              currentLocation + " to HOME");
                    storage.moveToLocation(lutemon.getId(), Storage.Location.HOME);
                }
            }
        }

        updateLutemonList();
    }

    private TextureRegionDrawable createLutemonBackground(Lutemon lutemon) {
        Color color;
        switch (lutemon.getType()) {
            case WHITE:
                color = new Color(1, 1, 1, 0.3f);
                break;
            case GREEN:
                color = new Color(0, 1, 0, 0.3f);
                break;
            case PINK:
                color = new Color(1, 0.7f, 0.7f, 0.3f);
                break;
            case ORANGE:
                color = new Color(1, 0.5f, 0, 0.3f);
                break;
            case BLACK:
                color = new Color(0.3f, 0.3f, 0.3f, 0.3f);
                break;
            default:
                color = new Color(0.5f, 0.5f, 0.5f, 0.3f);
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
}
