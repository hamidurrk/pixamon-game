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
import com.main.lutemon.model.battle.Battle;
import com.main.lutemon.model.battle.BattleState;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.ui.fragments.BattleFragment;
import com.main.lutemon.utils.Constants;

public class BattleScreen implements Screen {
    private final LutemonGame game;
    private Stage stage;
    private final OrthographicCamera camera;
    private TextureRegion backgroundTexture;
    private final BattleFragment battleFragment;
    private Battle currentBattle;

    public BattleScreen(LutemonGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.getScreenWidth(), Constants.getScreenHeight());
        initialize();

        // Create battle fragment
        battleFragment = new BattleFragment(this, game.getAssetLoader().getSkin());
        stage.addActor(battleFragment.getTable());
    }

    private void initialize() {
        stage = new Stage(new FitViewport(Constants.getScreenWidth(), Constants.getScreenHeight(), camera));
        Gdx.input.setInputProcessor(stage);

        // Load background
        backgroundTexture = game.getAssetLoader().getBackground("battle");

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);

        // Title
        Label titleLabel = new Label("Battle", game.getAssetLoader().getSkin(), "title");
        table.add(titleLabel).pad(Constants.getPadding() * 2);
        table.row();

        // Buttons
        TextButton backButton = new TextButton("Back to Home", game.getAssetLoader().getSkin());

        backButton.addListener(event -> {
            game.navigateToHome();
            return true;
        });

        table.add(backButton).pad(10).width(Constants.getButtonWidth());

        stage.addActor(table);
    }

    public void startBattle(Lutemon playerLutemon, Lutemon opponentLutemon) {
        currentBattle = new Battle(playerLutemon, opponentLutemon);
        // Start the battle after a short delay
        Gdx.app.postRunnable(() -> {
            currentBattle.start();
            updateUI();
        });
        updateUI();
    }

    public void performAction(String action) {
        if (currentBattle != null && currentBattle.getState() == BattleState.IN_PROGRESS) {
            currentBattle.performAction(action);
            updateUI();
        }
    }

    private void updateUI() {
        if (currentBattle != null) {
            // Update battle UI elements
            battleFragment.updateBattleState(currentBattle.getState());
        }
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update battle if ongoing
        if (currentBattle != null) {
            currentBattle.update(delta);
            updateUI();
        }

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

        // Re-add the battle fragment
        stage.addActor(battleFragment.getTable());

        // Update battle UI if there's an ongoing battle
        if (currentBattle != null) {
            updateUI();
        }
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

    public Battle getCurrentBattle() {
        return currentBattle;
    }
}
