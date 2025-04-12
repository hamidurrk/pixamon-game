package com.main.lutemon.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class AssetLoader implements Disposable {
    private static AssetLoader instance;
    private final AssetManager manager;
    private Skin skin;
    private Texture defaultTexture; // For fallback when assets are missing
    private FontManager fontManager;

    private AssetLoader() {
        manager = new AssetManager();
        fontManager = FontManager.getInstance();
        createDefaultTexture();
        loadAssets();
    }

    public static AssetLoader getInstance() {
        if (instance == null) {
            instance = new AssetLoader();
        }
        return instance;
    }

    private void createDefaultTexture() {
        // Create a simple black texture to use as fallback
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        defaultTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void loadAssets() {
        try {
            // Initialize fonts first
            fontManager.initializeFonts();

            // Try to load UI skin
            try {
                manager.load("skins/uiskin.json", Skin.class);
                manager.finishLoading();
                skin = manager.get("skins/uiskin.json", Skin.class);

                // Update skin with our custom fonts
                fontManager.updateSkin(skin);
            } catch (GdxRuntimeException e) {
                Gdx.app.error("AssetLoader", "Error loading skin: " + e.getMessage());
                // Create a basic skin
                skin = new Skin();

                // Add our fonts to the basic skin
                fontManager.updateSkin(skin);
            }

            // Try to load backgrounds
            tryLoadTexture("backgrounds/main_menu.png");
            tryLoadTexture("backgrounds/home.png");
            tryLoadTexture("backgrounds/training.png");
            tryLoadTexture("backgrounds/battle.png");

            // Try to load Lutemon animations
            String[] lutemonTypes = {"white", "green", "pink", "orange", "black"};
            for (String type : lutemonTypes) {
                tryLoadTextureAtlas("lutemons/" + type + ".atlas");
            }

            // Try to load UI elements
            tryLoadTexture("ui/buttons.png");
            tryLoadTexture("ui/dialogs.png");
            tryLoadTexture("ui/icons.png");

            manager.finishLoading();
        } catch (Exception e) {
            Gdx.app.error("AssetLoader", "Error loading assets: " + e.getMessage());
        }
    }

    private void tryLoadTexture(String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                manager.load(path, Texture.class);
            } else {
                Gdx.app.log("AssetLoader", "Warning: File not found: " + path);
            }
        } catch (Exception e) {
            Gdx.app.error("AssetLoader", "Error loading texture " + path + ": " + e.getMessage());
        }
    }

    private void tryLoadTextureAtlas(String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                manager.load(path, TextureAtlas.class);
            } else {
                Gdx.app.log("AssetLoader", "Warning: File not found: " + path);
            }
        } catch (Exception e) {
            Gdx.app.error("AssetLoader", "Error loading texture atlas " + path + ": " + e.getMessage());
        }
    }

    public Skin getSkin() {
        return skin;
    }

    public TextureRegion getBackground(String screenName) {
        String path = "backgrounds/" + screenName.toLowerCase() + ".png";
        try {
            if (manager.isLoaded(path)) {
                return new TextureRegion(manager.get(path, Texture.class));
            } else {
                Gdx.app.log("AssetLoader", "Warning: Background not loaded: " + path + ", using default");
                return new TextureRegion(defaultTexture);
            }
        } catch (Exception e) {
            Gdx.app.error("AssetLoader", "Error getting background " + path + ": " + e.getMessage());
            return new TextureRegion(defaultTexture);
        }
    }

    public TextureAtlas getLutemonAtlas(String type) {
        String path = "lutemons/" + type.toLowerCase() + ".atlas";
        try {
            if (manager.isLoaded(path)) {
                return manager.get(path, TextureAtlas.class);
            } else {
                Gdx.app.log("AssetLoader", "Warning: Lutemon atlas not loaded: " + path);
                return null;
            }
        } catch (Exception e) {
            Gdx.app.error("AssetLoader", "Error getting lutemon atlas " + path + ": " + e.getMessage());
            return null;
        }
    }

    public Texture getUIElement(String name) {
        String path = "ui/" + name + ".png";
        try {
            if (manager.isLoaded(path)) {
                return manager.get(path, Texture.class);
            } else {
                Gdx.app.log("AssetLoader", "Warning: UI element not loaded: " + path + ", using default");
                return defaultTexture;
            }
        } catch (Exception e) {
            Gdx.app.error("AssetLoader", "Error getting UI element " + path + ": " + e.getMessage());
            return defaultTexture;
        }
    }

    @Override
    public void dispose() {
        if (defaultTexture != null) {
            defaultTexture.dispose();
        }
        if (fontManager != null) {
            fontManager.dispose();
        }
        manager.dispose();
    }
}