package com.main.lutemon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.main.lutemon.LutemonGame;
import com.main.lutemon.model.lutemon.*;
import com.main.lutemon.model.profile.Profile;
import com.main.lutemon.model.storage.Storage;
import com.main.lutemon.ui.dialogs.CreateLutemonDialog;
import com.main.lutemon.ui.fragments.HomeFragment;
import com.main.lutemon.utils.Constants;
import com.main.lutemon.utils.ProfileManager;

public class HomeScreen implements Screen {
    private final LutemonGame game;
    private Stage stage;
    private final OrthographicCamera camera;
    private TextureRegion backgroundTexture;
    private HomeFragment homeFragment;

    public HomeScreen(LutemonGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.getScreenWidth(), Constants.getScreenHeight());
        initialize();
    }

    private void initialize() {
        try {
            stage = new Stage(new FitViewport(Constants.getScreenWidth(), Constants.getScreenHeight(), camera));
            Gdx.input.setInputProcessor(stage);
            backgroundTexture = game.getAssetLoader().getBackground("home");
            createUI();
        } catch (Exception e) {
            Gdx.app.error("HomeScreen", "Error initializing: " + e.getMessage());
            throw e;
        }
    }

    private void createUI() {
        // Main container table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        float padding = Constants.getPadding();
        float buttonWidth = Constants.getButtonWidth();
        float buttonHeight = Constants.getScreenHeight() * Constants.BUTTON_HEIGHT_PERCENT;

        // Top bar with back button and profile info
        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.top();

        // Back button on the left
        Table leftTopTable = new Table();
        leftTopTable.left();
        TextButton backButton = new TextButton("Back", game.getAssetLoader().getSkin());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Save profile before going back to main menu
                game.saveGame();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        leftTopTable.add(backButton).size(buttonWidth * 0.25f, buttonHeight).pad(padding);

        // Profile info on the right
        Table rightTopTable = new Table();
        rightTopTable.center();
        Profile currentProfile = ProfileManager.getInstance().getCurrentProfile();
        if (currentProfile != null) {
            Label profileLabel = new Label("Profile: " + currentProfile.getName(), game.getAssetLoader().getSkin());
            rightTopTable.add(profileLabel).pad(150, 0, 0, 0);
        }

        topTable.add(leftTopTable).expandX().fillX();
        topTable.add(rightTopTable).expandX().fillX();

        TextButton createButton = new TextButton("Create", game.getAssetLoader().getSkin());
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCreateLutemonDialog();
            }
        });
        topTable.add(createButton).size(buttonWidth * 0.35f, buttonHeight).pad(padding).expandX().right();


        // Title
        Label titleLabel = new Label("Your Lutemons", game.getAssetLoader().getSkin(), "title");
        mainTable.add(titleLabel).pad(padding * 2).expandX().center().row();

        Table fragmentContainer = new Table();
        fragmentContainer.setFillParent(true);

        // Create and position HomeFragment directly
        float fragmentX = Constants.getScreenWidth() * 0.05f;
        float fragmentY = Constants.getScreenHeight() * 0.2f;
        float fragmentWidth = Constants.getScreenWidth() * 0.9f;
        float fragmentHeight = Constants.getScreenHeight() * 0.6f;

        homeFragment = new HomeFragment(this, game.getAssetLoader().getSkin(), fragmentWidth, fragmentHeight);
        homeFragment.setPosition(fragmentX, fragmentY);
        fragmentContainer.add(homeFragment).expand().fill();
        mainTable.add(fragmentContainer).expand().fill().center().pad(padding).row();

        // Bottom navigation buttons
        Table bottomTable = new Table();
        bottomTable.bottom();

        TextButton trainButton = new TextButton("Train", game.getAssetLoader().getSkin());
        TextButton battleButton = new TextButton("Battle", game.getAssetLoader().getSkin());
        TextButton statsButton = new TextButton("Stats", game.getAssetLoader().getSkin());

        trainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.navigateToTraining();
            }
        });

        battleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.navigateToBattle();
            }
        });

        statsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.navigateToStatistics();
            }
        });

        bottomTable.add(trainButton).size(buttonWidth * 0.7f, buttonHeight).pad(padding);
        bottomTable.add(battleButton).size(buttonWidth * 0.7f, buttonHeight).pad(padding);
        bottomTable.add(statsButton).size(buttonWidth * 0.7f, buttonHeight).pad(padding);

        // Add bottom table to main table
        mainTable.add(bottomTable).padBottom(padding * 2).row();

        // Add all tables to stage
        stage.addActor(topTable);
        stage.addActor(mainTable);
    }

    /**
     * Shows the dialog for creating a new Lutemon.
     */
    public void showCreateLutemonDialog() {
        CreateLutemonDialog dialog = new CreateLutemonDialog(this, game.getAssetLoader().getSkin());
        dialog.show(stage);
    }

    /**
     * Updates the Lutemon list in the home fragment.
     * Called after a new Lutemon is created.
     */
    public void updateLutemonList() {
        homeFragment.updateLutemonList();
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();

        // Draw background
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
        game.getBatch().draw(backgroundTexture, 0, 0, Constants.getScreenWidth(), Constants.getScreenHeight());
        game.getBatch().end();

        // Draw UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update camera and viewport
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);

        // Recreate UI to adjust to new screen size
        stage.clear();
        createUI();

        // Re-add the home fragment
        stage.addActor(homeFragment);
        homeFragment.updateLutemonList();
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (backgroundTexture != null && backgroundTexture.getTexture() != null) {
            backgroundTexture.getTexture().dispose();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    public LutemonGame getGame() {
        return game;
    }

    private Pixmap createFromInnerRectangle(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);
        return pixmap;
    }
}
