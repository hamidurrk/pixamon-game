package com.main.lutemon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.main.lutemon.screens.BattleScreen;
import com.main.lutemon.screens.HomeScreen;
import com.main.lutemon.screens.MainMenuScreen;
import com.main.lutemon.screens.TrainingScreen;
import com.main.lutemon.util.FileManager;
import com.main.lutemon.utils.AssetLoader;

public class Main extends LutemonGame {
    public SpriteBatch batch;
    public AssetManager assetManager;
    private AssetLoader assetLoader;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        assetLoader = AssetLoader.getInstance();

        // Load assets
        assetManager.load("lutemons/lutemons.atlas", TextureAtlas.class);
        assetManager.load("backgrounds/battle_bg.png", com.badlogic.gdx.graphics.Texture.class);
        assetManager.finishLoading();

        // Load saved game data if available
        FileManager.loadGame();

        // Set the battle screen
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
        super.dispose();
        assetLoader.dispose();
        getScreen().dispose();
    }

    public void navigateToHome() {
        setScreen(new HomeScreen(this));
    }

    public void navigateToTraining() {
        setScreen(new TrainingScreen(this));
    }

    public void navigateToBattle() {
        setScreen(new BattleScreen(this));
    }

    public AssetLoader getAssetLoader() {
        return assetLoader;
    }
}
