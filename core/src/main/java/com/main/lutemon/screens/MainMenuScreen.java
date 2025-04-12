package com.main.lutemon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.main.lutemon.LutemonGame;
import com.main.lutemon.utils.Constants;

public class MainMenuScreen implements Screen {
    private final LutemonGame game;
    private Stage stage;
    private final OrthographicCamera camera;
    private TextureRegion backgroundTexture;

    public MainMenuScreen(LutemonGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.getScreenWidth(), Constants.getScreenHeight());
        initialize();
    }

    private void initialize() {
        stage = new Stage(new FitViewport(Constants.getScreenWidth(), Constants.getScreenHeight(), camera));
        Gdx.input.setInputProcessor(stage);

        // Load background
        backgroundTexture = game.getAssetLoader().getBackground("main_menu");

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);

        // Title
        Label titleLabel = new Label("Lutemon Game", game.getAssetLoader().getSkin(), "title");
        table.add(titleLabel).pad(Constants.getPadding() * 5);
        table.row();

        // Buttons
        TextButton newGameButton = new TextButton("New Game", game.getAssetLoader().getSkin());
        TextButton loadGameButton = new TextButton("Load Game", game.getAssetLoader().getSkin());
        TextButton exitButton = new TextButton("Exit", game.getAssetLoader().getSkin());

        newGameButton.addListener(event -> {
            game.navigateToHome();
            return true;
        });

        loadGameButton.addListener(event -> {
            if (game.hasSaveFile()) {
                if (game.loadGame()) {
                    game.navigateToHome();
                } else {
                    showErrorDialog("Failed to load game");
                }
            } else {
                showErrorDialog("No save file found");
            }
            return true;
        });

        exitButton.addListener(event -> {
            Gdx.app.exit();
            return true;
        });

        table.add(newGameButton).pad(Constants.getPadding()).width(Constants.getButtonWidth());
        table.row();
        table.add(loadGameButton).pad(Constants.getPadding()).width(Constants.getButtonWidth());
        table.row();
        table.add(exitButton).pad(Constants.getPadding()).width(Constants.getButtonWidth());

        stage.addActor(table);
    }

    private void showErrorDialog(String message) {
        Dialog dialog = new Dialog("Error", game.getAssetLoader().getSkin());
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
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
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.getTexture().dispose();
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
}