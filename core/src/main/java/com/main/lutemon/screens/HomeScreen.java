package com.main.lutemon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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
    private final HomeFragment homeFragment;

    public HomeScreen(LutemonGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        initialize();

        // Create home fragment
        homeFragment = new HomeFragment(this, game.getAssetLoader().getSkin());
        stage.addActor(homeFragment.getTable());
    }

    private void initialize() {
        stage = new Stage(new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        // Load background
        backgroundTexture = game.getAssetLoader().getBackground("home");

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);

        // Title
        Label titleLabel = new Label("Home", game.getAssetLoader().getSkin());
        table.add(titleLabel).pad(20);
        table.row();

        // Buttons
        TextButton createButton = new TextButton("Create New Lutemon", game.getAssetLoader().getSkin());
        TextButton trainButton = new TextButton("Go to Training", game.getAssetLoader().getSkin());
        TextButton battleButton = new TextButton("Go to Battle", game.getAssetLoader().getSkin());
        TextButton backButton = new TextButton("Back to Menu", game.getAssetLoader().getSkin());

        createButton.addListener(event -> {
            showCreateLutemonDialog();
            return true;
        });

        trainButton.addListener(event -> {
            game.navigateToTraining();
            return true;
        });

        battleButton.addListener(event -> {
            game.navigateToBattle();
            return true;
        });

        backButton.addListener(event -> {
            game.setScreen(new MainMenuScreen(game));
            return true;
        });

        table.add(createButton).pad(10).width(Constants.BUTTON_WIDTH);
        table.row();
        table.add(trainButton).pad(10).width(Constants.BUTTON_WIDTH);
        table.row();
        table.add(battleButton).pad(10).width(Constants.BUTTON_WIDTH);
        table.row();
        table.add(backButton).pad(10).width(Constants.BUTTON_WIDTH);

        stage.addActor(table);
    }

    public void showCreateLutemonDialog() {
        Dialog dialog = new Dialog("Create New Lutemon", game.getAssetLoader().getSkin());
        dialog.text("Choose a name for your new Lutemon:");

        TextField nameField = new TextField("", game.getAssetLoader().getSkin());
        dialog.getContentTable().add(nameField).pad(10);

        dialog.button("Create", true);
        dialog.button("Cancel", false);

        dialog.show(stage);

//        dialog.result(result -> {
//            if (result) {
//                String name = nameField.getText().trim();
//                if (!name.isEmpty()) {
//                    createLutemon(name);
//                }
//            }
//        });
    }

    private void createLutemon(String name) {
        // Create new Lutemon logic here
        Lutemon lutemon = new WhiteLutemon(0, name); // Default to White type for now
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
        game.getBatch().draw(backgroundTexture, 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        game.getBatch().end();

        // Draw UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
}
