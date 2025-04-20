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
import com.main.lutemon.model.storage.Storage;
import com.main.lutemon.ui.fragments.HomeFragment;
import com.main.lutemon.utils.Constants;

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

        // Back button in upper left corner
        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.top().left();
        TextButton backButton = new TextButton("Back", game.getAssetLoader().getSkin());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        topTable.add(backButton).size(buttonWidth * 0.25f, buttonHeight).pad(padding);

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
//        fragmentContainer.setDebug(true);

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

        bottomTable.add(trainButton).size(buttonWidth, buttonHeight).pad(padding);
        bottomTable.add(battleButton).size(buttonWidth, buttonHeight).pad(padding);

        // Add bottom table to main table
        mainTable.add(bottomTable).padBottom(padding * 2).row();

        // Add all tables to stage
        stage.addActor(topTable);
        stage.addActor(mainTable);
//        stage.addActor(createButtonContainer);
    }

    public void showCreateLutemonDialog() {
        Dialog dialog = new Dialog("Create New Lutemon", game.getAssetLoader().getSkin());
        TextField nameField = new TextField("", game.getAssetLoader().getSkin());
        dialog.getContentTable().add("Choose a name for your new Lutemon:").row();
        dialog.getContentTable().add(nameField).pad(10);

        dialog.button("Create", true);
        dialog.button("Cancel", false);

//        dialog.setResult(result -> {
//            if ((Boolean)result) {
//                String name = nameField.getText().trim();
//                if (!name.isEmpty()) {
//                    createLutemon(name);
//                }
//            }
//        });

        dialog.show(stage);
    }

    private void createLutemon(String name) {
        Lutemon lutemon = new WhiteLutemon(0, name);
        Storage.getInstance().addLutemon(lutemon);
        homeFragment.updateLutemonList();
        game.saveGame();
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
