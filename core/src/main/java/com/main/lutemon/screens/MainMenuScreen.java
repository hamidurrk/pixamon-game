package com.main.lutemon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.main.lutemon.LutemonGame;
import com.main.lutemon.ui.dialogs.CreateProfileDialog;
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

        // Title image instead of label
        titleTexture = new Texture(Gdx.files.internal("title.png"));
        Image titleImage = new Image(new TextureRegionDrawable(new TextureRegion(titleTexture)));
        // Scale the image to fit nicely
        float titleWidth = Constants.getScreenWidth() * 0.4f; // 70% of screen width
        float aspectRatio = (float)titleTexture.getHeight() / titleTexture.getWidth();
        float titleHeight = titleWidth * aspectRatio;
        table.add(titleImage).size(titleWidth, titleHeight).pad(Constants.getPadding() * 4, 0, Constants.getPadding() * 2, 0).center().row();

        // Button dimensions
        float buttonWidth = Constants.getButtonWidth();
        float buttonHeight = Constants.getScreenHeight() * Constants.BUTTON_HEIGHT_PERCENT;
        float padding = Constants.getPadding();

        // Buttons
        TextButton newGameButton = new TextButton("New Game", game.getAssetLoader().getSkin());
        TextButton loadGameButton = new TextButton("Load Game", game.getAssetLoader().getSkin());
        TextButton exitButton = new TextButton("Exit", game.getAssetLoader().getSkin());

        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCreateProfileDialog();
            }
        });

        loadGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ProfileSelectionScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(newGameButton).size(buttonWidth, buttonHeight).pad(padding).row();
        table.add(loadGameButton).size(buttonWidth, buttonHeight).pad(padding).row();
        table.add(exitButton).size(buttonWidth, buttonHeight).pad(padding).row();

        stage.addActor(table);
    }

    private void showErrorDialog(String message) {
        Dialog dialog = new Dialog("Error", game.getAssetLoader().getSkin());
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }

    /**
     * Shows the dialog for creating a new profile.
     */
    private void showCreateProfileDialog() {
        CreateProfileDialog dialog = new CreateProfileDialog(this, game.getAssetLoader().getSkin());
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

    private Texture titleTexture; // Add field to track the title texture

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.getTexture().dispose();
        // Dispose the title texture if it was created
        if (titleTexture != null) {
            titleTexture.dispose();
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

    /**
     * Gets the game instance.
     *
     * @return The game instance
     */
    public LutemonGame getGame() {
        return game;
    }
}
