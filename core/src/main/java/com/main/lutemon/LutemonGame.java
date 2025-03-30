package com.main.lutemon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.screens.*;
import com.main.lutemon.utils.AssetLoader;
import com.main.lutemon.utils.SaveManager;

public class LutemonGame extends Game {
    private SpriteBatch batch;
    private AssetLoader assetLoader;
    private SaveManager saveManager;
    private MainMenuScreen mainMenuScreen;
    private HomeScreen homeScreen;
    private TrainingScreen trainingScreen;
    private BattleScreen battleScreen;

    @Override
    public void create() {
        try {
            batch = new SpriteBatch();
            // Load assets first
            assetLoader = AssetLoader.getInstance();
            saveManager = new SaveManager();

            // Initialize screens safely
            initializeScreens();

            // Set initial screen
            setScreen(mainMenuScreen);
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error creating game: " + e.getMessage());
            if (e.getCause() != null) {
                Gdx.app.error("LutemonGame", "Caused by: " + e.getCause().getMessage());
            }
            // Try to recover by at least showing the main menu
            if (mainMenuScreen == null && batch != null && assetLoader != null) {
                try {
                    mainMenuScreen = new MainMenuScreen(this);
                    setScreen(mainMenuScreen);
                } catch (Exception ex) {
                    Gdx.app.error("LutemonGame", "Fatal error creating main menu: " + ex.getMessage());
                }
            }
        }
    }

    private void initializeScreens() {
        try {
            mainMenuScreen = new MainMenuScreen(this);
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error creating MainMenuScreen: " + e.getMessage());
            mainMenuScreen = null;
        }

        try {
            homeScreen = new HomeScreen(this);
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error creating HomeScreen: " + e.getMessage());
            homeScreen = null;
        }

        try {
            trainingScreen = new TrainingScreen(this);
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error creating TrainingScreen: " + e.getMessage());
            trainingScreen = null;
        }

        try {
            battleScreen = new BattleScreen(this);
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error creating BattleScreen: " + e.getMessage());
            battleScreen = null;
        }
    }

    @Override
    public void render() {
        try {
            super.render();
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error in render: " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        try {
            if (batch != null) batch.dispose();
            if (assetLoader != null) assetLoader.dispose();
            super.dispose();
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error in dispose: " + e.getMessage());
        }
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public AssetLoader getAssetLoader() {
        return assetLoader;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    public void navigateToHome() {
        if (homeScreen != null) {
            setScreen(homeScreen);
        } else {
            Gdx.app.error("LutemonGame", "Cannot navigate to home: screen is null");
        }
    }

    public void navigateToTraining() {
        if (trainingScreen != null) {
            setScreen(trainingScreen);
        } else {
            Gdx.app.error("LutemonGame", "Cannot navigate to training: screen is null");
        }
    }

    public void navigateToBattle() {
        if (battleScreen != null) {
            setScreen(battleScreen);
        } else {
            Gdx.app.error("LutemonGame", "Cannot navigate to battle: screen is null");
        }
    }

    public void showCreateLutemonDialog() {
        if (homeScreen != null) {
            homeScreen.showCreateLutemonDialog();
        } else {
            Gdx.app.error("LutemonGame", "Cannot show dialog: homeScreen is null");
        }
    }

    public void startBattle(Lutemon playerLutemon, Lutemon opponentLutemon) {
        if (battleScreen != null) {
            battleScreen.startBattle(playerLutemon, opponentLutemon);
        } else {
            Gdx.app.error("LutemonGame", "Cannot start battle: battleScreen is null");
        }
    }

    public void performBattleAction(String action) {
        if (battleScreen != null) {
            battleScreen.performAction(action);
        } else {
            Gdx.app.error("LutemonGame", "Cannot perform action: battleScreen is null");
        }
    }

    public boolean saveGame() {
        try {
            return saveManager.saveGame();
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error saving game: " + e.getMessage());
            return false;
        }
    }

    public boolean loadGame() {
        try {
            return saveManager.loadGame();
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error loading game: " + e.getMessage());
            return false;
        }
    }

    public boolean hasSaveFile() {
        try {
            return saveManager.hasSaveFile();
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error checking save file: " + e.getMessage());
            return false;
        }
    }
} 