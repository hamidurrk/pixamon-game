package com.main.lutemon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.screens.*;
import com.main.lutemon.utils.AssetLoader;
import com.main.lutemon.utils.ProfileManager;
import com.main.lutemon.utils.SaveManager;
import com.main.lutemon.utils.StatisticsManager;

public class LutemonGame extends Game {
    private SpriteBatch batch;
    private AssetLoader assetLoader;
    private SaveManager saveManager;
    private ProfileManager profileManager;
    private MainMenuScreen mainMenuScreen;
    private HomeScreen homeScreen;
    private TrainingScreen trainingScreen;
    private BattleScreen battleScreen;
    private StatisticsScreen statisticsScreen;

    @Override
    public void create() {
        try {
            batch = new SpriteBatch();
            // Load assets first
            assetLoader = AssetLoader.getInstance();
            saveManager = new SaveManager();

            // Initialize profile manager
            profileManager = ProfileManager.getInstance();
            Gdx.app.log("LutemonGame", "ProfileManager initialized");

            // Initialize screens safely
            initializeScreens();

            // Set initial screen
            if (mainMenuScreen != null) {
                setScreen(mainMenuScreen);
            } else {
                Gdx.app.error("LutemonGame", "MainMenuScreen is null after initialization");
            }
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error creating game: " + e.getMessage());
            if (e.getCause() != null) {
                Gdx.app.error("LutemonGame", "Caused by: " + e.getCause().getMessage());
            }
        }
    }

    private void initializeScreens() {
        try {
            mainMenuScreen = new MainMenuScreen(this);
            homeScreen = new HomeScreen(this);
            trainingScreen = new TrainingScreen(this);
            battleScreen = new BattleScreen(this);
            statisticsScreen = new StatisticsScreen(this);
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error initializing screens: " + e.getMessage());
            throw e; // Rethrow to be caught by create()
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
        // Heal all Lutemons when returning to home
        healAllLutemons();

        if (homeScreen == null) {
            try {
                homeScreen = new HomeScreen(this);
            } catch (Exception e) {
                Gdx.app.error("LutemonGame", "Error creating new HomeScreen: " + e.getMessage());
                return;
            }
        } else {
            // If homeScreen already exists, make sure to update the lutemon list
            Gdx.app.log("LutemonGame", "Updating existing HomeScreen");
            homeScreen.updateLutemonList();
        }
        setScreen(homeScreen);
    }

    /**
     * Heals all Lutemons to full health.
     * This is called when returning to the home screen after battles.
     */
    private void healAllLutemons() {
        try {
            // First, heal all Lutemons at HOME location
            com.main.lutemon.model.storage.Storage.getInstance().healAllAtHome();

            // Then, heal all Lutemons that were in battle
            for (Lutemon lutemon : com.main.lutemon.model.storage.Storage.getInstance().getLutemonsAtLocation(
                    com.main.lutemon.model.storage.Storage.Location.BATTLE)) {
                lutemon.heal();
                Gdx.app.log("LutemonGame", "Healed battle Lutemon: " + lutemon.getName());
            }

            // Also heal any Lutemons in training
            for (Lutemon lutemon : com.main.lutemon.model.storage.Storage.getInstance().getLutemonsAtLocation(
                    com.main.lutemon.model.storage.Storage.Location.TRAINING)) {
                lutemon.heal();
                Gdx.app.log("LutemonGame", "Healed training Lutemon: " + lutemon.getName());
            }

            Gdx.app.log("LutemonGame", "All Lutemons healed successfully");
        } catch (Exception e) {
            Gdx.app.error("LutemonGame", "Error healing Lutemons: " + e.getMessage());
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

    public void navigateToStatistics() {
        if (statisticsScreen != null) {
            setScreen(statisticsScreen);
        } else {
            try {
                statisticsScreen = new StatisticsScreen(this);
                setScreen(statisticsScreen);
            } catch (Exception e) {
                Gdx.app.error("LutemonGame", "Error creating StatisticsScreen: " + e.getMessage());
            }
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
            // Save current profile if one is loaded
            if (profileManager.getCurrentProfile() != null) {
                return profileManager.saveCurrentProfile();
            } else {
                // Fall back to old save system if no profile is loaded
                return saveManager.saveGame();
            }
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
